package com.example.flagsentinelapi.util;

import com.example.flagsentinelapi.dto.flag.FeatureFlagDTO;
import com.example.flagsentinelapi.dto.rule.RuleDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class FlagEvaluatorTest {

    @Mock
    private RuleEvaluator ruleEvaluator;

    @InjectMocks
    private FlagEvaluator evaluator;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnFalseWhenFlagIsNull() {
        boolean result = evaluator.isEnabledFor(null, Map.of());
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenFlagIsDisabled() {
        FeatureFlagDTO flag = new FeatureFlagDTO();
        flag.setFlagCode("test");
        flag.setEnabled(false);

        boolean result = evaluator.isEnabledFor(flag, Map.of());
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrueWhenFlagHasNoRules() {
        FeatureFlagDTO flag = new FeatureFlagDTO();
        flag.setFlagCode("test");
        flag.setEnabled(true);
        flag.setRules(List.of());

        boolean result = evaluator.isEnabledFor(flag, Map.of());
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnTrueWhenAllRulesPass() {
        RuleDTO rule = new RuleDTO();
        rule.setAttribute("country");

        FeatureFlagDTO flag = new FeatureFlagDTO();
        flag.setFlagCode("test");
        flag.setEnabled(true);
        flag.setRules(List.of(rule));

        // Mock para simular que la regla pasa
        when(ruleEvaluator.evaluate(rule, Map.of("country", "ES"))).thenReturn(true);

        boolean result = evaluator.isEnabledFor(flag, Map.of("country", "ES"));
        assertThat(result).isTrue();

        verify(ruleEvaluator).evaluate(rule, Map.of("country", "ES"));
    }

    @Test
    void shouldReturnFalseWhenAnyRuleFails() {
        RuleDTO rule = new RuleDTO();
        rule.setAttribute("country");

        FeatureFlagDTO flag = new FeatureFlagDTO();
        flag.setFlagCode("test");
        flag.setEnabled(true);
        flag.setRules(List.of(rule));

        // Mock para simular que la regla falla
        when(ruleEvaluator.evaluate(rule, Map.of("country", "FR"))).thenReturn(false);

        boolean result = evaluator.isEnabledFor(flag, Map.of("country", "FR"));
        assertThat(result).isFalse();

        verify(ruleEvaluator).evaluate(rule, Map.of("country", "FR"));
    }
}