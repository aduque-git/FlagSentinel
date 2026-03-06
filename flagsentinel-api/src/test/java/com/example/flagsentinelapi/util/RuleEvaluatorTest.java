package com.example.flagsentinelapi.util;

import com.example.flagsentinelapi.dto.rule.RuleDTO;
import com.example.flagsentinelapi.dto.ruleoperator.RuleOperator;
import com.example.flagsentinelapi.logging.LogPropertiesKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RuleEvaluatorTest {

    private RuleEvaluator evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new RuleEvaluator();
    }

    @Test
    void shouldThrowWhenRuleIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> evaluator.evaluate(null, Map.of()));
        assertThat(ex).hasMessageContaining("Rule cannot be null");
    }

    @Test
    void shouldThrowWhenContextIsNull() {
        RuleDTO rule = new RuleDTO();
        rule.setAttribute("country");
        rule.setOperator("EQ");
        rule.setValue("ES");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> evaluator.evaluate(rule, null));
        assertThat(ex).hasMessageContaining("Context cannot be null");
    }

    @Test
    void shouldReturnFalseWhenAttributeMissingInContext() {
        RuleDTO rule = new RuleDTO();
        rule.setAttribute("country");
        rule.setOperator("EQ");
        rule.setValue("ES");

        Map<String, String> context = new HashMap<>();
        context.put("other", "value");

        boolean result = evaluator.evaluate(rule, context);
        assertThat(result).isFalse();
    }

    @Test
    void shouldThrowWhenOperatorUnsupported() {
        RuleDTO rule = new RuleDTO();
        rule.setAttribute("country");
        rule.setOperator("UNSUPPORTED_OP"); // operador no válido
        rule.setValue("ES");

        Map<String, String> context = Map.of("country", "ES");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> evaluator.evaluate(rule, context));
        assertThat(ex).hasMessageContaining("Unknown operator");
    }

    @Test
    void shouldEvaluateSuccessfully() {
        RuleDTO rule = new RuleDTO();
        rule.setAttribute("country");
        rule.setOperator(RuleOperator.EQUALS.getCode());
        rule.setValue("ES");

        Map<String, String> context = Map.of("country", "ES");

        boolean result = evaluator.evaluate(rule, context);
        assertThat(result).isTrue();
    }

    @Test
    void shouldEvaluateToFalseWhenValueMismatch() {
        RuleDTO rule = new RuleDTO();
        rule.setAttribute("country");
        rule.setOperator(RuleOperator.EQUALS.getCode());
        rule.setValue("ES");

        Map<String, String> context = Map.of("country", "FR");

        boolean result = evaluator.evaluate(rule, context);
        assertThat(result).isFalse();
    }
}