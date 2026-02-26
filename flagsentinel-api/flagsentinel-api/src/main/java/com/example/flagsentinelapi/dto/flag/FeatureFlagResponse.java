package com.example.flagsentinelapi.dto.flag;

import com.example.flagsentinelapi.dto.rule.RuleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeatureFlagResponse {
    private Long id;
    private String flagCode;
    private boolean enabled;
    private List<RuleResponse> rules;
}
