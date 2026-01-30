package com.example.flagsentinelapi.mapper;

import com.example.flagsentinelapi.dto.*;
import com.example.flagsentinelapi.model.FeatureFlag;
import com.example.flagsentinelapi.model.Rule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FeatureFlagMapper {

    private final RuleMapper ruleMapper;

    public FeatureFlag toEntity(CreateFeatureFlagRequest request) {
        FeatureFlag flag = new FeatureFlag();
        flag.setKey(request.getKey());
        flag.setEnabled(request.isEnabled());
        if (request.getRules() != null) {
            List<Rule> rules = request.getRules().stream().map(ruleMapper::toEntity).toList();
            flag.setRules(rules);
        }
        return flag;
    }

    public void updateEntity(FeatureFlag flag, UpdateFeatureFlagRequest request) {
        flag.setKey(request.getKey());
        flag.setEnabled(request.isEnabled());
        if (request.getRules() != null) {
            List<Rule> rules = request.getRules().stream().map(ruleMapper::toEntity).toList();
            flag.setRules(rules);
        }
    }

    public FeatureFlagResponse toResponse(FeatureFlag flag) {
        FeatureFlagResponse dto = new FeatureFlagResponse();
        dto.setId(flag.getId());
        dto.setKey(flag.getKey());
        dto.setEnabled(flag.isEnabled());
        if (flag.getRules() != null) {
            dto.setRules(flag.getRules().stream().map(ruleMapper::toResponse).toList());
        }
        return dto;
    }

    public FeatureFlagDTO toDTO(FeatureFlag entity) {
        FeatureFlagDTO dto = new FeatureFlagDTO();
        dto.setKey(entity.getKey());
        dto.setEnabled(entity.isEnabled());
        if (entity.getRules() != null) {
            dto.setRules(
                    entity.getRules()
                            .stream()
                            .map(ruleMapper::toDTO)
                            .toList()
            );
        }
        return dto;
    }
}
