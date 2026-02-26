package com.example.flagsentinelapi.util;


import com.example.flagsentinelapi.dto.rule.RuleDTO;
import com.example.flagsentinelapi.dto.ruleoperator.RuleOperator;

import java.util.Map;

public class RuleEvaluator {

    public boolean evaluate(RuleDTO rule, Map<String, String> context) {

        if (rule == null || context == null) {
            return false;
        }
        String actual = context.get(rule.getAttribute());
        if (actual == null) {
            return false;
        }
        RuleOperator operator = RuleOperator.fromCode(rule.getOperator());
        return operator.evaluate(actual, rule.getValue());
    }

}
