package com.example.flagsentinelapi.rules;

import com.example.flagsentinelapi.model.FeatureFlag;
import com.example.flagsentinelapi.model.Rule;

import java.util.List;
import java.util.Map;

public class FlagEvaluator {

    private final RuleEvaluator ruleEvaluator = new RuleEvaluator();

    public boolean isEnabledFor(FeatureFlag flag, Map<String, Object> context) {

        if (flag == null) {
            return false;
        }

        // Si el flag está desactivado globalmente, no hay nada que evaluar
        if (!flag.isEnabled()) {
            return false;
        }

        List<Rule> rules = flag.getRules();

        // Si no hay reglas, el flag está activado para todos
        if (rules == null || rules.isEmpty()) {
            return true;
        }

        // Todas las reglas deben cumplirse
        for (Rule rule : rules) {
            if (!ruleEvaluator.evaluate(rule, context)) {
                return false;
            }
        }

        return true;
    }
}
