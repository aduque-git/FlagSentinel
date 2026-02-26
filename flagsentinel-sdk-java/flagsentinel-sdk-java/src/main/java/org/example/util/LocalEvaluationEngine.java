package org.example.util;

import org.example.cache.CacheProvider;
import org.example.dto.BootstrapFeatureFlagDTO;
import org.example.dto.BootstrapRuleResponse;

import java.util.Map;

public class LocalEvaluationEngine {

    private final RuleOperatorEngine operatorEngine = new RuleOperatorEngine();

    public boolean evaluate(
            BootstrapFeatureFlagDTO flag,
            CacheProvider cache,
            Map<String, Object> context
    ) {

        // 1. Si el flag está deshabilitado globalmente → false
        if (!flag.isEnabled()) {
            return false;
        }

        // 2. Si no tiene reglas → enabled
        if (flag.getRules() == null || flag.getRules().isEmpty()) {
            return true;
        }

        // 3. Evaluar reglas por ID
        for (Long ruleId : flag.getRules()) {

            BootstrapRuleResponse rule = cache.getRule(ruleId);

            // Si la regla no existe → no coincide
            if (rule == null) {
                return false;
            }

            Object ctxValue = context.get(rule.getAttribute());

            // Si el contexto no tiene el atributo → no coincide
            if (ctxValue == null) {
                return false;
            }

            boolean result = operatorEngine.evaluate(
                    rule.getOperator(),
                    ctxValue.toString(),
                    rule.getValue()
            );

            if (!result) {
                return false;
            }
        }

        // Todas las reglas coinciden
        return true;
    }
}
