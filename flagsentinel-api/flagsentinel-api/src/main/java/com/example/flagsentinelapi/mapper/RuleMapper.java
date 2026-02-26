package com.example.flagsentinelapi.mapper;

import com.example.flagsentinelapi.dto.bootstrap.BootstrapRuleResponse;
import com.example.flagsentinelapi.dto.rule.CreateRuleRequest;
import com.example.flagsentinelapi.dto.rule.RuleDTO;
import com.example.flagsentinelapi.dto.rule.RuleResponse;
import com.example.flagsentinelapi.dto.rule.UpdateRuleRequest;
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
        return new RuleResponse(
                rule.getId(),
                rule.getAttribute(),
                rule.getOperator(),
                rule.getValue()
        );
    }

    public RuleDTO toDTO(Rule rule) {
        return new RuleDTO(
                rule.getId(),
                rule.getAttribute(),
                rule.getOperator(),
                rule.getValue()
        );
    }

    public BootstrapRuleResponse toBootstrapDto(Rule rule) {
        return new BootstrapRuleResponse(
                rule.getId(),
                rule.getAttribute(),
                rule.getOperator(),
                rule.getValue()
        );
    }
}

