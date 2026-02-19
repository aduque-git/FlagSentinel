package com.example.flagsentinelapi.service;

import com.example.flagsentinelapi.dto.CreateRuleRequest;
import com.example.flagsentinelapi.dto.RuleResponse;
import com.example.flagsentinelapi.dto.UpdateRuleRequest;
import com.example.flagsentinelapi.exception.NotFoundException;
import com.example.flagsentinelapi.mapper.RuleMapper;
import com.example.flagsentinelapi.model.Rule;
import com.example.flagsentinelapi.repository.RuleRepository;
import com.example.flagsentinelapi.websocket.WebSocketEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RuleService {

    private final RuleRepository repo;
    private final RuleMapper mapper;
    private final WebSocketEventPublisher ws;

    // ---------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------
    public RuleResponse create(CreateRuleRequest request) {

        Rule rule = mapper.toEntity(request);
        Rule saved = repo.save(rule);

        RuleResponse response = mapper.toResponse(saved);
        ws.publishRuleUpdate(response);

        return response;
    }

    // ---------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------
    public RuleResponse update(Long id, UpdateRuleRequest request) {

        Rule rule = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Rule not found"));

        mapper.updateEntity(rule, request);

        Rule saved = repo.save(rule);
        RuleResponse response = mapper.toResponse(saved);

        ws.publishRuleUpdate(response);
        return response;
    }

    // ---------------------------------------------------------
    // GET BY ID
    // ---------------------------------------------------------
    public RuleResponse getById(Long id) {

        Rule rule = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Rule not found"));

        return mapper.toResponse(rule);
    }

    // ---------------------------------------------------------
    // GET ALL
    // ---------------------------------------------------------
    public List<RuleResponse> getAll() {
        return repo.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    // ---------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------
    public void delete(Long id) {

        Rule rule = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Rule not found"));

        repo.delete(rule);

        ws.publishRuleUpdate("deleted:" + id);
    }

    public Page<RuleResponse> findAllPaged(Pageable pageable) {
        return repo.findAll(pageable)
                .map(mapper::toResponse);
    }

}
