package org.example.flagsentinelpanel.ui.rules.service;

import org.example.flagsentinelpanel.dto.CreateRuleRequest;
import org.example.flagsentinelpanel.dto.PageResponse;
import org.example.flagsentinelpanel.dto.RuleResponse;
import org.example.flagsentinelpanel.dto.UpdateRuleRequest;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RulesService {

    private final RulesClient client;

    public RulesService(RulesClient client) {
        this.client = client;
    }

    public List<RuleResponse> findAll() {
        return client.getAllRules();
    }

    public RuleResponse create(CreateRuleRequest request) {
        return client.createRule(request);
    }

    public RuleResponse update(Long id, UpdateRuleRequest request) {
        return client.updateRule(id, request);
    }

    public void delete(Long id) {
        client.deleteRule(id);
    }

    public PageResponse<RuleResponse> findPaged(int page, int size) {return client.findPaged(page, size); }
}

