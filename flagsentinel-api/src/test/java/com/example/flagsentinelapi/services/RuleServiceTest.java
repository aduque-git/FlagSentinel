package com.example.flagsentinelapi.services;


import com.example.flagsentinelapi.dto.bootstrap.BootstrapRuleResponse;
import com.example.flagsentinelapi.dto.rule.CreateRuleRequest;
import com.example.flagsentinelapi.dto.rule.RuleResponse;
import com.example.flagsentinelapi.dto.rule.UpdateRuleRequest;
import com.example.flagsentinelapi.exception.NotFoundException;
import com.example.flagsentinelapi.mapper.RuleMapper;
import com.example.flagsentinelapi.model.Rule;
import com.example.flagsentinelapi.repository.RuleRepository;
import com.example.flagsentinelapi.service.RuleService;
import com.example.flagsentinelapi.websocket.WebSocketEventPublisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class RuleServiceTest {

    @Mock
    private RuleRepository repo;

    @Mock
    private RuleMapper mapper;

    @Mock
    private WebSocketEventPublisher ws;

    @InjectMocks
    private RuleService service;

    // ---------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------

    @Test
    @DisplayName("Should create rule successfully")
    void shouldCreateRule() {

        // Given
        CreateRuleRequest request = new CreateRuleRequest();
        request.setAttribute("country");
        request.setOperator("equals");

        Rule rule = new Rule();
        rule.setAttribute("country");

        Rule saved = new Rule();
        saved.setId(1L);
        saved.setAttribute("country");
        saved.setOperator("equals");

        RuleResponse response = new RuleResponse();
        BootstrapRuleResponse bootstrap = new BootstrapRuleResponse();

        given(mapper.toEntity(request)).willReturn(rule);
        given(repo.save(rule)).willReturn(saved);
        given(mapper.toBootstrapDto(saved)).willReturn(bootstrap);
        given(mapper.toResponse(saved)).willReturn(response);

        // When
        RuleResponse result = service.create(request);

        // Then
        assertThat(result).isEqualTo(response);

        then(repo).should().save(rule);
        then(ws).should().publishRuleUpdate(bootstrap);
    }

    // ---------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------

    @Test
    @DisplayName("Should update rule successfully")
    void shouldUpdateRule() {

        // Given
        Long id = 1L;

        UpdateRuleRequest request = new UpdateRuleRequest();

        Rule rule = new Rule();
        rule.setId(id);

        Rule saved = new Rule();
        saved.setId(id);

        RuleResponse response = new RuleResponse();
        BootstrapRuleResponse bootstrap = new BootstrapRuleResponse();

        given(repo.findById(id)).willReturn(Optional.of(rule));
        given(repo.save(rule)).willReturn(saved);
        given(mapper.toBootstrapDto(saved)).willReturn(bootstrap);
        given(mapper.toResponse(saved)).willReturn(response);

        // When
        RuleResponse result = service.update(id, request);

        // Then
        assertThat(result).isEqualTo(response);

        then(mapper).should().updateEntity(rule, request);
        then(ws).should().publishRuleUpdate(bootstrap);
    }

    @Test
    @DisplayName("Should throw when updating non existing rule")
    void shouldThrowWhenUpdatingMissingRule() {

        // Given
        Long id = 99L;

        given(repo.findById(id)).willReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> service.update(id, new UpdateRuleRequest()))
                .isInstanceOf(NotFoundException.class);

        then(repo).should().findById(id);
    }

    // ---------------------------------------------------------
    // GET BY ID
    // ---------------------------------------------------------

    @Test
    @DisplayName("Should return rule by id")
    void shouldGetById() {

        // Given
        Long id = 1L;

        Rule rule = new Rule();
        RuleResponse response = new RuleResponse();

        given(repo.findById(id)).willReturn(Optional.of(rule));
        given(mapper.toResponse(rule)).willReturn(response);

        // When
        RuleResponse result = service.getById(id);

        // Then
        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("Should throw when rule not found by id")
    void shouldThrowWhenRuleNotFound() {

        Long id = 1L;

        given(repo.findById(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(id))
                .isInstanceOf(NotFoundException.class);
    }

    // ---------------------------------------------------------
    // GET ALL
    // ---------------------------------------------------------

    @Test
    @DisplayName("Should return all rules")
    void shouldReturnAllRules() {

        // Given
        Rule rule = new Rule();
        RuleResponse response = new RuleResponse();

        given(repo.findAll()).willReturn(List.of(rule));
        given(mapper.toResponse(rule)).willReturn(response);

        // When
        List<RuleResponse> result = service.getAll();

        // Then
        assertThat(result).hasSize(1);
    }

    // ---------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------

    @Test
    @DisplayName("Should delete rule")
    void shouldDeleteRule() {

        // Given
        Long id = 1L;

        Rule rule = new Rule();
        rule.setId(id);
        rule.setAttribute("country");

        given(repo.findById(id)).willReturn(Optional.of(rule));

        // When
        service.delete(id);

        // Then
        then(repo).should().delete(rule);
        then(ws).should().publishRuleUpdate("Rule_deleted:" + id);
    }

    // ---------------------------------------------------------
    // PAGINATION
    // ---------------------------------------------------------

    @Test
    @DisplayName("Should return paged rules")
    void shouldReturnPagedRules() {

        // Given
        Pageable pageable = PageRequest.of(0, 10);

        Rule rule = new Rule();
        RuleResponse response = new RuleResponse();

        Page<Rule> page = new PageImpl<>(List.of(rule));

        given(repo.findAll(pageable)).willReturn(page);
        given(mapper.toResponse(rule)).willReturn(response);

        // When
        Page<RuleResponse> result = service.findAllPaged(pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
    }
}