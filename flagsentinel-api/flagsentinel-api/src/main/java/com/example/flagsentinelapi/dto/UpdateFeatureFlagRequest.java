package com.example.flagsentinelapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFeatureFlagRequest {

    private String key;
    private boolean enabled;
    private List<RuleDTO> rules;
}
