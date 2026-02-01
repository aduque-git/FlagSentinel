package com.example.flagsentinelapi.rule;

import com.example.flagsentinelapi.dto.RuleDTO;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RuleEvaluatorTest {

    private final RuleEvaluator evaluator = new RuleEvaluator();

    @Test
    void evaluate_equals_shouldReturnTrue() {
        RuleDTO rule = new RuleDTO(1L, "country", "equals", "ES");
        Map<String, String> ctx = Map.of("country", "ES");

        assertTrue(evaluator.evaluate(rule, ctx));
    }

    @Test
    void evaluate_notEquals_shouldReturnTrue() {
        RuleDTO rule = new RuleDTO(1L, "country", "not_equals", "FR");
        Map<String, String> ctx = Map.of("country", "ES");

        assertTrue(evaluator.evaluate(rule, ctx));
    }

    @Test
    void evaluate_contains_shouldReturnTrue() {
        RuleDTO rule = new RuleDTO(1L, "email", "contains", "@gmail.com");
        Map<String, String> ctx = Map.of("email", "aaron@gmail.com");

        assertTrue(evaluator.evaluate(rule, ctx));
    }

    @Test
    void evaluate_greaterThan_shouldReturnTrue() {
        RuleDTO rule = new RuleDTO(1L, "age", "greater_than", "18");
        Map<String, String> ctx = Map.of("age", "25");

        assertTrue(evaluator.evaluate(rule, ctx));
    }

    @Test
    void evaluate_lessThan_shouldReturnTrue() {
        RuleDTO rule = new RuleDTO(1L, "age", "less_than", "30");
        Map<String, String> ctx = Map.of("age", "20");

        assertTrue(evaluator.evaluate(rule, ctx));
    }

    @Test
    void evaluate_shouldReturnFalse_whenAttributeMissing() {
        RuleDTO rule = new RuleDTO(1L, "country", "equals", "ES");
        Map<String, String> ctx = Map.of();

        assertFalse(evaluator.evaluate(rule, ctx));
    }

    @Test
    void evaluateRules_shouldReturnFalse_whenAnyRuleFails() {
        RuleDTO r1 = new RuleDTO(1L, "country", "equals", "ES");
        RuleDTO r2 = new RuleDTO(2L, "age", "greater_than", "30");

        Map<String, String> ctx = Map.of("country", "ES", "age", "20");

        assertFalse(evaluator.evaluateRules(java.util.List.of(r1, r2), ctx));
    }
}
