package com.example.flagsentinelapi.services;

import com.example.flagsentinelapi.dto.bootstrap.BootstrapFeatureFlagDTO;
import com.example.flagsentinelapi.dto.flag.CreateFeatureFlagRequest;
import com.example.flagsentinelapi.dto.flag.FeatureFlagResponse;
import com.example.flagsentinelapi.dto.flag.UpdateFeatureFlagRequest;
import com.example.flagsentinelapi.exception.ConflictException;
import com.example.flagsentinelapi.exception.NotFoundException;
import com.example.flagsentinelapi.mapper.FeatureFlagMapper;
import com.example.flagsentinelapi.model.FeatureFlag;
import com.example.flagsentinelapi.model.Rule;
import com.example.flagsentinelapi.repository.FeatureFlagRepository;
import com.example.flagsentinelapi.repository.RuleRepository;
import com.example.flagsentinelapi.service.FeatureFlagService;
import com.example.flagsentinelapi.websocket.WebSocketEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeatureFlagServiceTest {

    @Mock
    private FeatureFlagRepository repo;

    @Mock
    private FeatureFlagMapper mapper;

    @Mock
    private RuleRepository ruleRepo;

    @Mock
    private WebSocketEventPublisher ws;

    @InjectMocks
    private FeatureFlagService service;

    // ---------------------------------------------------------
    // CREATE SUCCESS
    // ---------------------------------------------------------
    @Test
    void shouldCreateFeatureFlag() {

        CreateFeatureFlagRequest request = new CreateFeatureFlagRequest();
        request.setFlagCode("new_flag");

        FeatureFlag entity = new FeatureFlag();
        entity.setFlagCode("new_flag");

        FeatureFlag saved = new FeatureFlag();
        saved.setId(1L);
        saved.setFlagCode("new_flag");

        FeatureFlagResponse response = new FeatureFlagResponse();

        when(repo.findByFlagCode("new_flag")).thenReturn(Optional.empty());
        when(mapper.toEntity(request)).thenReturn(entity);
        when(repo.save(entity)).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(response);
        when(mapper.toBootstrapDTO(saved)).thenReturn(new BootstrapFeatureFlagDTO());

        FeatureFlagResponse result = service.create(request);

        assertThat(result).isEqualTo(response);

        verify(repo).save(entity);
        verify(ws).publishFlagUpdate(any());
    }

    // ---------------------------------------------------------
    // CREATE DUPLICATE
    // ---------------------------------------------------------
    @Test
    void shouldFailWhenFlagCodeExists() {

        CreateFeatureFlagRequest request = new CreateFeatureFlagRequest();
        request.setFlagCode("dup");

        when(repo.findByFlagCode("dup"))
                .thenReturn(Optional.of(new FeatureFlag()));

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(ConflictException.class);

        verify(repo, never()).save(any());
    }

    // ---------------------------------------------------------
    // UPDATE SUCCESS
    // ---------------------------------------------------------
    @Test
    void shouldUpdateFlag() {

        Long id = 1L;

        UpdateFeatureFlagRequest request = new UpdateFeatureFlagRequest();

        FeatureFlag flag = new FeatureFlag();
        flag.setId(id);

        FeatureFlag saved = new FeatureFlag();
        saved.setId(id);

        FeatureFlagResponse response = new FeatureFlagResponse();

        when(repo.findById(id)).thenReturn(Optional.of(flag));
        when(repo.save(flag)).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(response);
        when(mapper.toBootstrapDTO(saved)).thenReturn(new BootstrapFeatureFlagDTO());

        FeatureFlagResponse result = service.update(id, request);

        assertThat(result).isEqualTo(response);

        verify(mapper).updateEntity(flag, request);
        verify(ws).publishFlagUpdate(any());
    }

    // ---------------------------------------------------------
    // UPDATE NOT FOUND
    // ---------------------------------------------------------
    @Test
    void shouldFailUpdateWhenFlagNotFound() {

        when(repo.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(1L, new UpdateFeatureFlagRequest()))
                .isInstanceOf(NotFoundException.class);
    }

    // ---------------------------------------------------------
    // GET BY ID
    // ---------------------------------------------------------
    @Test
    void shouldReturnFlagById() {

        FeatureFlag flag = new FeatureFlag();
        FeatureFlagResponse response = new FeatureFlagResponse();

        when(repo.findById(1L)).thenReturn(Optional.of(flag));
        when(mapper.toResponse(flag)).thenReturn(response);

        FeatureFlagResponse result = service.getById(1L);

        assertThat(result).isEqualTo(response);
    }

    // ---------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------
    @Test
    void shouldDeleteFlag() {

        FeatureFlag flag = new FeatureFlag();
        flag.setId(1L);
        flag.setFlagCode("flag");

        when(repo.findById(1L)).thenReturn(Optional.of(flag));

        service.delete(1L);

        verify(repo).delete(flag);
        verify(ws).publishFlagUpdate("Flag_deleted:1");
    }

    // ---------------------------------------------------------
    // RULE VALIDATION FAIL
    // ---------------------------------------------------------
    @Test
    void shouldFailWhenRuleIdsInvalid() {

        CreateFeatureFlagRequest request = new CreateFeatureFlagRequest();
        request.setFlagCode("flag");
        request.setRuleCodes(List.of(1L, 2L));

        when(repo.findByFlagCode("flag")).thenReturn(Optional.empty());
        when(mapper.toEntity(request)).thenReturn(new FeatureFlag());
        when(ruleRepo.findByIdIn(any())).thenReturn(List.of(new Rule()));

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(NotFoundException.class);
    }

    // ---------------------------------------------------------
    // PAGINATION
    // ---------------------------------------------------------
    @Test
    void shouldReturnPagedFlags() {

        Pageable pageable = PageRequest.of(0, 10);

        FeatureFlag flag = new FeatureFlag();
        FeatureFlagResponse response = new FeatureFlagResponse();

        Page<FeatureFlag> entityPage =
                new PageImpl<>(List.of(flag), pageable, 1);

        when(repo.findAll(pageable)).thenReturn(entityPage);
        when(mapper.toResponse(flag)).thenReturn(response);

        Page<FeatureFlagResponse> result = service.findAllPaged(pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
    }
}