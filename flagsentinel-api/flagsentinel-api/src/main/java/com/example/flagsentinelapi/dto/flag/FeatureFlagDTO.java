package com.example.flagsentinelapi.dto.flag;

import com.example.flagsentinelapi.dto.rule.RuleDTO;
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
    private String flagCode;
    private boolean enabled;
    private List<RuleDTO> rules;
}
