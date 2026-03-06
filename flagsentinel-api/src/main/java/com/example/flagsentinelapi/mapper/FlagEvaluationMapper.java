package com.example.flagsentinelapi.mapper;

import com.example.flagsentinelapi.dto.flagevaluation.FlagEvaluationResponse;
import com.example.flagsentinelapi.logging.LogPropertiesKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FlagEvaluationMapper {

    private static final Logger log = LoggerFactory.getLogger(FlagEvaluationMapper.class);

    public FlagEvaluationResponse toResponse(String key, boolean enabled, String reason) {
        FlagEvaluationResponse dto = new FlagEvaluationResponse();
        dto.setKey(key);
        dto.setEnabled(enabled);
        dto.setReason(reason);
        return dto;
    }
}