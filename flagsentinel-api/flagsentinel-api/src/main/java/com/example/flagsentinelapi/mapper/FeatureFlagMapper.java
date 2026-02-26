package com.example.flagsentinelapi.mapper;

import com.example.flagsentinelapi.dto.bootstrap.BootstrapFeatureFlagDTO;
import com.example.flagsentinelapi.dto.flag.CreateFeatureFlagRequest;
import com.example.flagsentinelapi.dto.flag.FeatureFlagDTO;
import com.example.flagsentinelapi.dto.flag.FeatureFlagResponse;
import com.example.flagsentinelapi.dto.flag.UpdateFeatureFlagRequest;
import com.example.flagsentinelapi.model.FeatureFlag;
import com.example.flagsentinelapi.model.Rule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeatureFlagMapper {

    private final RuleMapper ruleMapper;

    public FeatureFlag toEntity(CreateFeatureFlagRequest request) {
        FeatureFlag flag = new FeatureFlag();
        flag.setFlagCode(request.getFlagCode());
        flag.setEnabled(request.isEnabled());
        return flag;
    }

    public void updateEntity(FeatureFlag flag, UpdateFeatureFlagRequest request) {
        flag.setFlagCode(request.getFlagCode());
        flag.setEnabled(request.isEnabled());
    }

    public FeatureFlagResponse toResponse(FeatureFlag flag) {
        FeatureFlagResponse dto = new FeatureFlagResponse();
        dto.setId(flag.getId());
        dto.setFlagCode(flag.getFlagCode());
        dto.setEnabled(flag.isEnabled());

        if (flag.getRules() != null) {
            dto.setRules(
                    flag.getRules().stream()
                            .map(ruleMapper::toResponse)
                            .toList()
            );
        }

        return dto;
    }

    public FeatureFlagDTO toDTO(FeatureFlag entity) {
        FeatureFlagDTO dto = new FeatureFlagDTO();
        dto.setFlagCode(entity.getFlagCode());
        dto.setEnabled(entity.isEnabled());

        if (entity.getRules() != null) {
            dto.setRules(
                    entity.getRules().stream()
                            .map(ruleMapper::toDTO)
                            .toList()
            );
        }

        return dto;
    }

    public BootstrapFeatureFlagDTO toBootstrapDTO(FeatureFlag entity) {
        BootstrapFeatureFlagDTO dto = new BootstrapFeatureFlagDTO();
        dto.setId(entity.getId());
        dto.setFlagCode(entity.getFlagCode());
        dto.setEnabled(entity.isEnabled());

        if (entity.getRules() != null) {
            dto.setRules(
                    entity.getRules().stream()
                            .map(Rule::getId)   // solo IDs
                            .toList()
            );
        }

        return dto;
    }

}

