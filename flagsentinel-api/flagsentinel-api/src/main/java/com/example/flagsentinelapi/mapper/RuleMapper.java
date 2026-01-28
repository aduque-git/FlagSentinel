package com.example.flagsentinelapi.mapper;

import com.example.flagsentinelapi.dto.CreateRuleRequest;
import com.example.flagsentinelapi.dto.RuleDTO;
import com.example.flagsentinelapi.dto.RuleResponse;
import com.example.flagsentinelapi.dto.UpdateRuleRequest;
import com.example.flagsentinelapi.model.Rule;

public class RuleMapper {

    public static Rule toEntity(CreateRuleRequest dto) {
        if (dto == null) return null;

        Rule rule = new Rule();
        rule.setAttribute(dto.getAttribute());
        rule.setOperator(dto.getOperator());
        rule.setValue(dto.getValue());
        return rule;
    }

    public static Rule toEntity(UpdateRuleRequest dto) {
        if (dto == null) return null;

        Rule rule = new Rule();
        rule.setId(dto.getId());
        rule.setAttribute(dto.getAttribute());
        rule.setOperator(dto.getOperator());
        rule.setValue(dto.getValue());
        return rule;
    }

    public static Rule toEntity(RuleDTO dto) {
        if (dto == null) return null;

        Rule rule = new Rule();
        rule.setId(dto.getId());
        rule.setAttribute(dto.getAttribute());
        rule.setOperator(dto.getOperator());
        rule.setValue(dto.getValue());
        return rule;
    }

    public static RuleResponse toResponse(Rule rule) {
        if (rule == null) return null;

        return new RuleResponse(
                rule.getId(),
                rule.getAttribute(),
                rule.getOperator(),
                rule.getValue()
        );
    }
}
