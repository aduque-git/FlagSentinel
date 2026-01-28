package com.example.flagsentinelapi.mapper;

import com.example.flagsentinelapi.dto.*;
import com.example.flagsentinelapi.model.FeatureFlag;
import com.example.flagsentinelapi.model.Rule;
import java.util.ArrayList;
import java.util.List;

public class FeatureFlagMapper {

    public static FeatureFlag toEntity(CreateFeatureFlagRequest dto) {
        if (dto == null) return null;

        FeatureFlag flag = new FeatureFlag();
        flag.setKey(dto.getKey());
        flag.setEnabled(dto.isEnabled());

        if (dto.getRules() != null) {
            List<Rule> rules = new ArrayList<>();
            for (RuleDTO ruleDTO : dto.getRules()) {
                Rule rule = RuleMapper.toEntity(ruleDTO);
                rules.add(rule);
            }
            flag.setRules(rules);
        }

        return flag;
    }

    public static FeatureFlag toEntity(UpdateFeatureFlagRequest dto, FeatureFlag existing) {
        if (dto == null || existing == null) return existing;

        existing.setKey(dto.getKey());
        existing.setEnabled(dto.isEnabled());

        List<Rule> rules = new ArrayList<>();
        if (dto.getRules() != null) {
            for (RuleDTO ruleDTO : dto.getRules()) {
                Rule rule = RuleMapper.toEntity(ruleDTO);
                rules.add(rule);
            }
        }

        existing.setRules(rules);
        return existing;
    }

    public static FeatureFlagResponse toResponse(FeatureFlag flag) {
        if (flag == null) return null;

        List<RuleResponse> ruleResponses = new ArrayList<>();
        if (flag.getRules() != null) {
            for (Rule rule : flag.getRules()) {
                ruleResponses.add(RuleMapper.toResponse(rule));
            }
        }

        return new FeatureFlagResponse(
                flag.getId(),
                flag.getKey(),
                flag.isEnabled(),
                ruleResponses
        );
    }
}
