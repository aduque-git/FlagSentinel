package com.example.flagsentinelapi.dto.flagevaluation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FlagEvaluationRequest {
    private String key;
    private Map<String, String> attributes;
}
