package com.example.flagsentinelapi.dto.flagevaluation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FlagEvaluationResponse {
    private String key;
    private boolean enabled;
    private String reason;
}
