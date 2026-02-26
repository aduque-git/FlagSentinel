package com.example.flagsentinelapi.mapper;

import com.example.flagsentinelapi.dto.flagevaluation.FlagEvaluationResponse;
import org.springframework.stereotype.Component;

@Component
public class FlagEvaluationMapper {

    private RuleMapper mapper;

    public FlagEvaluationResponse toResponse(String key, boolean enabled, String reason) {
        FlagEvaluationResponse dto = new FlagEvaluationResponse();
        dto.setKey(key);
        dto.setEnabled(enabled);
        dto.setReason(reason);
        return dto;
    }
}