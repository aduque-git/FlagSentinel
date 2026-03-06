package com.example.flagsentinelapi.util;

import com.example.flagsentinelapi.dto.rule.RuleDTO;
import com.example.flagsentinelapi.dto.ruleoperator.RuleOperator;
import com.example.flagsentinelapi.logging.ApiLogMessages;
import com.example.flagsentinelapi.logging.LogPropertiesKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RuleEvaluator {

    private static final Logger log = LoggerFactory.getLogger(RuleEvaluator.class);

    public boolean evaluate(RuleDTO rule, Map<String, String> context) {

        if (rule == null) {
            log.error(ApiLogMessages.get(LogPropertiesKeys.RULE_NULL));
            throw new IllegalArgumentException("Rule cannot be null");
        }

        if (context == null) {
            log.error(ApiLogMessages.get(LogPropertiesKeys.CONTEXT_NULL));
            throw new IllegalArgumentException("Context cannot be null");
        }

        String attribute = rule.getAttribute();
        String actual = context.get(attribute);

        if (actual == null) {
            log.debug(ApiLogMessages.get(
                    LogPropertiesKeys.RULE_ATTRIBUTE_MISSING,
                    attribute
            ));
            return false;
        }

        RuleOperator operator = RuleOperator.fromCode(rule.getOperator());

        if (operator == null) {
            log.error(ApiLogMessages.get(
                    LogPropertiesKeys.RULE_OPERATOR_UNSUPPORTED,
                    rule.getOperator()
            ));
            throw new IllegalStateException("Unknown operator: " + rule.getOperator());
        }

        boolean result = operator.evaluate(actual, rule.getValue());

        log.debug(ApiLogMessages.get(
                LogPropertiesKeys.RULE_EVALUATED,
                attribute,
                result
        ));

        return result;
    }
}