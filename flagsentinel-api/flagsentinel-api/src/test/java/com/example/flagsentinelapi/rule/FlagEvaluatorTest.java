package com.example.flagsentinelapi.rule;

import com.example.flagsentinelapi.dto.FeatureFlagDTO;
import com.example.flagsentinelapi.dto.RuleDTO;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FlagEvaluatorTest {

    private final FlagEvaluator flagEvaluator = new FlagEvaluator();

    @Test
    void isEnabledFor_shouldReturnFalse_whenFlagIsNull() {
        assertFalse(flagEvaluator.isEnabledFor(null, Map.of()));
    }

    @Test
    void isEnabledFor_shouldReturnFalse_whenFlagDisabled() {
        FeatureFlagDTO flag = new FeatureFlagDTO("test", false, List.of());
        assertFalse(flagEvaluator.isEnabledFor(flag, Map.of()));
    }

    @Test
    void isEnabledFor_shouldReturnTrue_whenNoRules() {
        FeatureFlagDTO flag = new FeatureFlagDTO("test", true, List.of());
        assertTrue(flagEvaluator.isEnabledFor(flag, Map.of()));
    }

    @Test
    void isEnabledFor_shouldReturnTrue_whenRulesPass() {
        RuleDTO rule = new RuleDTO(1L, "country", "equals", "ES");
        FeatureFlagDTO flag = new FeatureFlagDTO("test", true, List.of(rule));

        Map<String, String> ctx = Map.of("country", "ES");

        assertTrue(flagEvaluator.isEnabledFor(flag, ctx));
    }

    @Test
    void isEnabledFor_shouldReturnFalse_whenRulesFail() {
        RuleDTO rule = new RuleDTO(1L, "country", "equals", "ES");
        FeatureFlagDTO flag = new FeatureFlagDTO("test", true, List.of(rule));

        Map<String, String> ctx = Map.of("country", "FR");

        assertFalse(flagEvaluator.isEnabledFor(flag, ctx));
    }
}
