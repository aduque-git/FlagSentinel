package com.example.flagsentinelapi.rule;


import com.example.flagsentinelapi.dto.RuleDTO;
import com.example.flagsentinelapi.model.Rule;

import java.util.List;
import java.util.Map;

public class RuleEvaluator {

    public boolean evaluate(RuleDTO rule, Map<String, String> context) {

        if (rule == null || context == null) {
            return false;
        }

        Object value = context.get(rule.getAttribute());
        if (value == null) {
            return false;
        }

        String operator = rule.getOperator();
        String expected = rule.getValue();
        String actual = value.toString();

        switch (operator) {
            case "equals":
                return actual.equals(expected);

            case "not_equals":
                return !actual.equals(expected);

            case "contains":
                return actual.contains(expected);

            case "greater_than":
                try {
                    return Double.parseDouble(actual) > Double.parseDouble(expected);
                } catch (NumberFormatException e) {
                    return false;
                }

            case "less_than":
                try {
                    return Double.parseDouble(actual) < Double.parseDouble(expected);
                } catch (NumberFormatException e) {
                    return false;
                }

            default:
                return false;
        }
    }
    public boolean evaluateRules(List<RuleDTO> rules, Map<String, String> attributes) {

        for (RuleDTO rule : rules) {
            if (!evaluateRule(rule, attributes)) {
                return false;
            }
        }

        return true;
    }

    private boolean evaluateRule(RuleDTO rule, Map<String, String> attributes) {

        String attrValue = attributes.get(rule.getAttribute());

        if (attrValue == null) {
            return false;
        }

        return switch (rule.getOperator()) {
            case "EQUALS" -> attrValue.equals(rule.getValue());
            case "NOT_EQUALS" -> !attrValue.equals(rule.getValue());
            case "CONTAINS" -> attrValue.contains(rule.getValue());
            case "GREATER_THAN" -> compareNumbers(attrValue, rule.getValue()) > 0;
            case "LESS_THAN" -> compareNumbers(attrValue, rule.getValue()) < 0;
            default -> false;
        };
    }

    private int compareNumbers(String a, String b) {
        try {
            return Double.compare(Double.parseDouble(a), Double.parseDouble(b));
        } catch (Exception e) {
            return -1;
        }
    }
}
