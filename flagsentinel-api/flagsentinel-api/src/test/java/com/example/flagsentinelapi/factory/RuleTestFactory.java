package com.example.flagsentinelapi.factory;

import com.example.flagsentinelapi.model.Rule;

public class RuleTestFactory {

    public static Rule createRule(String attribute, String operator, String value) {
        Rule rule = new Rule();
        rule.setAttribute(attribute);
        rule.setOperator(operator);
        rule.setValue(value);
        return rule;
    }
}
