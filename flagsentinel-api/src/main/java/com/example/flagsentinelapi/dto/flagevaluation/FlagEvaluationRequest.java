package com.example.flagsentinelapi.dto.flagevaluation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlagEvaluationRequest {
    private String key;
    private Map<String, String> attributes;
}
