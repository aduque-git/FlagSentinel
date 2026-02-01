package com.example.flagsentinelapi.mapper;

import com.example.flagsentinelapi.dto.CreateRuleRequest;
import com.example.flagsentinelapi.dto.RuleDTO;
import com.example.flagsentinelapi.dto.RuleResponse;
import com.example.flagsentinelapi.dto.UpdateRuleRequest;
import com.example.flagsentinelapi.model.Rule;
import org.springframework.stereotype.Component;

@Component
public class RuleMapper {

    public Rule toEntity(CreateRuleRequest request) {
        Rule rule = new Rule();
        rule.setAttribute(request.getAttribute());
        rule.setOperator(request.getOperator());
        rule.setValue(request.getValue());
        return rule;
    }

    public void updateEntity(Rule rule, UpdateRuleRequest request) {
        rule.setAttribute(request.getAttribute());
        rule.setOperator(request.getOperator());
        rule.setValue(request.getValue());
    }

    public RuleResponse toResponse(Rule rule) {
        RuleResponse dto = new RuleResponse();
        dto.setId(rule.getId());
        dto.setAttribute(rule.getAttribute());
        dto.setOperator(rule.getOperator());
        dto.setValue(rule.getValue());
        return dto;
    }

    // Para FeatureFlag
    public Rule toEntity(RuleDTO dto) {
        Rule rule = new Rule();
        rule.setId(dto.getId());
        rule.setAttribute(dto.getAttribute());
        rule.setOperator(dto.getOperator());
        rule.setValue(dto.getValue());
        return rule;
    }

    public RuleDTO toDTO(Rule rule) {
        RuleDTO dto = new RuleDTO();
        dto.setId(rule.getId());
        dto.setAttribute(rule.getAttribute());
        dto.setOperator(rule.getOperator());
        dto.setValue(rule.getValue());
        return dto;
    }
}
