package com.example.flagsentinelapi.rules;


import com.example.flagsentinelapi.model.Rule;

import java.util.Map;

public class RuleEvaluator {

    public boolean evaluate(Rule rule, Map<String, Object> context) {

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
}
