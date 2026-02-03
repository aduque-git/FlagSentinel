package com.example.flagsentinelapi.rule;

import com.example.flagsentinelapi.dto.FeatureFlagDTO;
import com.example.flagsentinelapi.dto.RuleDTO;
import com.example.flagsentinelapi.mapper.FeatureFlagMapper;
import com.example.flagsentinelapi.model.FeatureFlag;
import com.example.flagsentinelapi.model.Rule;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

public class FlagEvaluator {

    private final RuleEvaluator ruleEvaluator = new RuleEvaluator();

    public boolean isEnabledFor(FeatureFlagDTO flag, Map<String, String> context) {

        if (flag == null) {
            return false;
        }
        // Flag deshabilitado globalmente
        if (!flag.isEnabled()) {
            return false;
        }
        List<RuleDTO> rules = flag.getRules();
        // Sin reglas -> habilitado para todos
        if (rules == null || rules.isEmpty()) {
            return true;
        }
        // Todas las reglas deben cumplirse
        for (RuleDTO rule : rules) {
            if (!ruleEvaluator.evaluate(rule, context)) {
                return false;
            }
        }
        return true;
    }
}
