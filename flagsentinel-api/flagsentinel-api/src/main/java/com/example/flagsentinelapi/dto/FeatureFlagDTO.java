package com.example.flagsentinelapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeatureFlagDTO {
    private String key;
    private boolean enabled;
    private List<RuleDTO> rules;
}
