package com.example.flagsentinelapi.service;

import com.example.flagsentinelapi.dto.CreateFeatureFlagRequest;
import com.example.flagsentinelapi.dto.FeatureFlagResponse;
import com.example.flagsentinelapi.dto.RuleDTO;
import com.example.flagsentinelapi.dto.UpdateFeatureFlagRequest;
import com.example.flagsentinelapi.mapper.FeatureFlagMapper;
import com.example.flagsentinelapi.model.FeatureFlag;
import com.example.flagsentinelapi.repository.FeatureFlagRepository;
import com.example.flagsentinelapi.websocket.WebSocketEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FeatureFlagServiceTest {

    // --------- DEPENDENCIAS MOCKEADAS ----------
    // Todo lo que es externo al servicio se mockea:
    // - repositorio (BD)
    // - mapper (transformación DTO <-> entidad)
    // - websocket (side effects)
    @Mock
    private FeatureFlagRepository repo;

    @Mock
    private FeatureFlagMapper mapper;

    @Mock
    private WebSocketEventPublisher ws;

    // --------- CLASE BAJO TEST ----------
    // Se crea una instancia real del servicio
    // y se inyectan los mocks en su constructor.
    @InjectMocks
    private FeatureFlagService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // =========================================================
    // create()
    // =========================================================

    @Test
    void create_shouldSaveNewFlagAndPublishUpdate() {
        // Arrange
        // Usamos el constructor real del DTO:
        // (String key, boolean enabled, List<RuleDTO> rules)
        CreateFeatureFlagRequest request =
                new CreateFeatureFlagRequest("new-feature", true, Collections.emptyList());

        FeatureFlag flag = new FeatureFlag();   // entidad antes de guardar
        FeatureFlag saved = new FeatureFlag();  // entidad devuelta por repo.save()
        FeatureFlagResponse response = new FeatureFlagResponse(); // respuesta final

        // La clave NO existe todavía
        when(repo.findByKey("new-feature")).thenReturn(Optional.empty());

        // Mapper: DTO -> entidad
        when(mapper.toEntity(request)).thenReturn(flag);

        // Repositorio: guardamos la entidad
        when(repo.save(flag)).thenReturn(saved);

        // Mapper: entidad -> respuesta
        when(mapper.toResponse(saved)).thenReturn(response);

        // Act
        FeatureFlagResponse result = service.create(request);

        // Assert
        assertEquals(response, result);
        verify(ws).publishFlagUpdate(response); // se notifica por WebSocket
    }

    @Test
    void create_shouldThrowIfKeyExists() {
        // Arrange
        CreateFeatureFlagRequest request =
                new CreateFeatureFlagRequest("existing", true, Collections.emptyList());

        // Simulamos que ya existe un flag con esa key
        when(repo.findByKey("existing")).thenReturn(Optional.of(new FeatureFlag()));

        // Act + Assert
        assertThrows(RuntimeException.class, () -> service.create(request));

        // No se debe guardar nada ni publicar nada
        verify(repo, never()).save(any());
        verify(ws, never()).publishFlagUpdate(any());
    }

    // =========================================================
    // update()
    // =========================================================

    @Test
    void update_shouldModifyFlagAndPublishUpdate() {
        // Arrange
        Long id = 1L;

        // DTO real: (String key, boolean enabled, List<RuleDTO> rules)
        UpdateFeatureFlagRequest request =
                new UpdateFeatureFlagRequest("updated-key", true, List.of(new RuleDTO()));

        FeatureFlag flag = new FeatureFlag();
        FeatureFlag saved = new FeatureFlag();
        FeatureFlagResponse response = new FeatureFlagResponse();

        // El flag existe en BD
        when(repo.findById(id)).thenReturn(Optional.of(flag));

        // El mapper actualiza la entidad existente con los datos del request
        // No necesitamos lógica real aquí, solo que se llame.
        doNothing().when(mapper).updateEntity(flag, request);

        // Guardamos la entidad actualizada
        when(repo.save(flag)).thenReturn(saved);

        // Mapeamos a respuesta
        when(mapper.toResponse(saved)).thenReturn(response);

        // Act
        FeatureFlagResponse result = service.update(id, request);

        // Assert
        assertEquals(response, result);
        verify(mapper).updateEntity(flag, request);
        verify(ws).publishFlagUpdate(response);
    }

    @Test
    void update_shouldThrowIfNotFound() {
        // Arrange
        Long id = 99L;
        UpdateFeatureFlagRequest request =
                new UpdateFeatureFlagRequest("key", true, Collections.emptyList());

        when(repo.findById(id)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RuntimeException.class, () -> service.update(id, request));
    }

    // =========================================================
    // getById()
    // =========================================================

    @Test
    void getById_shouldReturnMappedResponse() {
        // Arrange
        Long id = 1L;
        FeatureFlag flag = new FeatureFlag();
        FeatureFlagResponse response = new FeatureFlagResponse();

        when(repo.findById(id)).thenReturn(Optional.of(flag));
        when(mapper.toResponse(flag)).thenReturn(response);

        // Act
        FeatureFlagResponse result = service.getById(id);

        // Assert
        assertEquals(response, result);
    }

    @Test
    void getById_shouldThrowIfNotFound() {
        // Arrange
        Long id = 99L;
        when(repo.findById(id)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RuntimeException.class, () -> service.getById(id));
    }

    // =========================================================
    // getAll()
    // =========================================================

    @Test
    void getAll_shouldReturnMappedList() {
        // Arrange
        FeatureFlag flag1 = new FeatureFlag();
        FeatureFlag flag2 = new FeatureFlag();

        FeatureFlagResponse resp1 = new FeatureFlagResponse();
        FeatureFlagResponse resp2 = new FeatureFlagResponse();

        when(repo.findAll()).thenReturn(List.of(flag1, flag2));
        when(mapper.toResponse(flag1)).thenReturn(resp1);
        when(mapper.toResponse(flag2)).thenReturn(resp2);

        // Act
        List<FeatureFlagResponse> result = service.getAll();

        // Assert
        assertEquals(List.of(resp1, resp2), result);
    }

    // =========================================================
    // delete()
    // =========================================================

    @Test
    void delete_shouldRemoveFlagAndPublishDeletion() {
        // Arrange
        Long id = 1L;
        FeatureFlag flag = new FeatureFlag();

        when(repo.findById(id)).thenReturn(Optional.of(flag));

        // Act
        service.delete(id);

        // Assert
        verify(repo).delete(flag);
        verify(ws).publishFlagUpdate("deleted:" + id);
    }

    @Test
    void delete_shouldThrowIfNotFound() {
        // Arrange
        Long id = 99L;
        when(repo.findById(id)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RuntimeException.class, () -> service.delete(id));
    }
}
