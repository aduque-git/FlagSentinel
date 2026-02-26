package com.example.flagsentinelapi.util;

import com.example.flagsentinelapi.dto.flag.FeatureFlagDTO;
import com.example.flagsentinelapi.dto.rule.RuleDTO;

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
        for(RuleDTO rule: rules){
            System.out.println("REGLA: " + rule.getAttribute());
        }
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
