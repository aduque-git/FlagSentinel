package com.example.flagsentinelapi.service;

import com.example.flagsentinelapi.dto.CreateRuleRequest;
import com.example.flagsentinelapi.dto.RuleResponse;
import com.example.flagsentinelapi.dto.UpdateRuleRequest;
import com.example.flagsentinelapi.mapper.RuleMapper;
import com.example.flagsentinelapi.model.Rule;
import com.example.flagsentinelapi.repository.RuleRepository;
import com.example.flagsentinelapi.websocket.WebSocketEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RuleServiceTest {

    @Mock
    private RuleRepository repo;

    @Mock
    private RuleMapper mapper;

    @Mock
    private WebSocketEventPublisher ws;

    @InjectMocks
    private RuleService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------
    @Test
    void create_shouldSaveRuleAndPublishEvent() {
        CreateRuleRequest req = new CreateRuleRequest("country", "equals", "ES");

        Rule entity = new Rule();
        entity.setAttribute("country");
        entity.setOperator("equals");
        entity.setValue("ES");

        Rule saved = new Rule();
        saved.setId(1L);
        saved.setAttribute("country");
        saved.setOperator("equals");
        saved.setValue("ES");

        RuleResponse response = new RuleResponse();
        response.setId(1L);
        response.setAttribute("country");
        response.setOperator("equals");
        response.setValue("ES");

        when(mapper.toEntity(req)).thenReturn(entity);
        when(repo.save(entity)).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(response);

        RuleResponse result = service.create(req);

        assertEquals(response, result);
        verify(repo).save(entity);
        verify(ws).publishRuleUpdate(response);
    }

    // ---------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------
    @Test
    void update_shouldModifyRuleAndPublishEvent() {
        UpdateRuleRequest req = new UpdateRuleRequest("age", "greater_than", "18");

        Rule existing = new Rule();
        existing.setId(1L);
        existing.setAttribute("country");
        existing.setOperator("equals");
        existing.setValue("ES");

        Rule saved = new Rule();
        saved.setId(1L);
        saved.setAttribute("age");
        saved.setOperator("greater_than");
        saved.setValue("18");

        RuleResponse response = new RuleResponse();
        response.setId(1L);
        response.setAttribute("age");
        response.setOperator("greater_than");
        response.setValue("18");

        when(repo.findById(1L)).thenReturn(Optional.of(existing));

        doAnswer(invocation -> {
            existing.setAttribute(req.getAttribute());
            existing.setOperator(req.getOperator());
            existing.setValue(req.getValue());
            return null;
        }).when(mapper).updateEntity(existing, req);

        when(repo.save(existing)).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(response);

        RuleResponse result = service.update(1L, req);

        assertEquals(response, result);
        verify(repo).save(existing);
        verify(ws).publishRuleUpdate(response);
    }

    @Test
    void update_shouldThrow_whenRuleNotFound() {
        UpdateRuleRequest req = new UpdateRuleRequest("age", "greater_than", "18");

        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.update(99L, req));
    }

    // ---------------------------------------------------------
    // GET BY ID
    // ---------------------------------------------------------
    @Test
    void getById_shouldReturnRuleResponse() {
        Rule entity = new Rule();
        entity.setId(1L);
        entity.setAttribute("country");
        entity.setOperator("equals");
        entity.setValue("ES");

        RuleResponse response = new RuleResponse();
        response.setId(1L);
        response.setAttribute("country");
        response.setOperator("equals");
        response.setValue("ES");

        when(repo.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        RuleResponse result = service.getById(1L);

        assertEquals(response, result);
    }

    @Test
    void getById_shouldThrow_whenNotFound() {
        when(repo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.getById(1L));
    }

    // ---------------------------------------------------------
    // GET ALL
    // ---------------------------------------------------------
    @Test
    void getAll_shouldReturnListOfResponses() {
        Rule r1 = new Rule();
        r1.setId(1L);
        r1.setAttribute("country");
        r1.setOperator("equals");
        r1.setValue("ES");

        RuleResponse dto1 = new RuleResponse();
        dto1.setId(1L);
        dto1.setAttribute("country");
        dto1.setOperator("equals");
        dto1.setValue("ES");

        when(repo.findAll()).thenReturn(List.of(r1));
        when(mapper.toResponse(r1)).thenReturn(dto1);

        List<RuleResponse> result = service.getAll();

        assertEquals(1, result.size());
        assertEquals(dto1, result.get(0));
    }

    // ---------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------
    @Test
    void delete_shouldRemoveRuleAndPublishEvent() {
        Rule entity = new Rule();
        entity.setId(1L);

        when(repo.findById(1L)).thenReturn(Optional.of(entity));

        service.delete(1L);

        verify(repo).delete(entity);
        verify(ws).publishRuleUpdate("deleted:1");
    }

    @Test
    void delete_shouldThrow_whenNotFound() {
        when(repo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.delete(1L));
    }
}
