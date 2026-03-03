package org.example.util;

public class RuleOperatorEngine {

    public boolean evaluate(String operator, String attributeValue, String ruleValue) {

        if (attributeValue == null) return false;

        switch (operator.toLowerCase()) {

            case "equals":
                return attributeValue.equals(ruleValue);

            case "not_equals":
                return !attributeValue.equals(ruleValue);

            case "contains":
                return attributeValue.contains(ruleValue);

            case "greater_than":
                try {
                    return Double.parseDouble(attributeValue) > Double.parseDouble(ruleValue);
                } catch (Exception e) {
                    return false;
                }

            case "less_than":
                try {
                    return Double.parseDouble(attributeValue) < Double.parseDouble(ruleValue);
                } catch (Exception e) {
                    return false;
                }

            case "starts_with":
                return attributeValue.startsWith(ruleValue);

            case "ends_with":
                return attributeValue.endsWith(ruleValue);

            default:
                return false;
        }
    }
}
