package com.example.flagsentinelapi.service;

import com.example.flagsentinelapi.dto.CreateRuleRequest;
import com.example.flagsentinelapi.dto.RuleResponse;
import com.example.flagsentinelapi.dto.UpdateRuleRequest;
import com.example.flagsentinelapi.mapper.RuleMapper;
import com.example.flagsentinelapi.model.Rule;

import com.example.flagsentinelapi.repository.RuleRepository;
import com.example.flagsentinelapi.websocket.WebSocketEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RuleService {

    private final RuleRepository repo;
    private final RuleMapper mapper;
    private final WebSocketEventPublisher ws;

    public RuleResponse create(CreateRuleRequest request) {
        Rule rule = mapper.toEntity(request);
        Rule saved = repo.save(rule);
        RuleResponse response = mapper.toResponse(saved);
        ws.publishRuleUpdate(response);
        return response;
    }

    public RuleResponse update(Long id, UpdateRuleRequest request) {
        Rule rule = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Rule not found"));

        mapper.updateEntity(rule, request);

        Rule saved = repo.save(rule);
        RuleResponse response = mapper.toResponse(saved);
        ws.publishRuleUpdate(response);
        return response;
    }

    public RuleResponse getById(Long id) {
        Rule rule = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Rule not found"));
        return mapper.toResponse(rule);
    }

    public List<RuleResponse> getAll() {
        return repo.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    public void delete(Long id) {
        Rule rule = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Rule not found"));
        repo.delete(rule);
        ws.publishRuleUpdate("deleted:" + id);
    }
}
