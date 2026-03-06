package com.example.flagsentinelapi.util;

import com.example.flagsentinelapi.dto.flag.FeatureFlagDTO;
import com.example.flagsentinelapi.dto.rule.RuleDTO;
import com.example.flagsentinelapi.logging.ApiLogMessages;
import com.example.flagsentinelapi.logging.LogPropertiesKeys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FlagEvaluator {

    private static final Logger log = LoggerFactory.getLogger(FlagEvaluator.class);

    private final RuleEvaluator ruleEvaluator;

    public boolean isEnabledFor(FeatureFlagDTO flag, Map<String, String> context) {

        if (flag == null) {
            log.warn(ApiLogMessages.get(LogPropertiesKeys.FLAG_EVAL_NULL_FLAG));
            return false;
        }

        String code = flag.getFlagCode();

        log.debug(ApiLogMessages.get(LogPropertiesKeys.FLAG_EVAL_START, code));

        if (!flag.isEnabled()) {
            log.debug(ApiLogMessages.get(LogPropertiesKeys.FLAG_EVAL_DISABLED, code));
            return false;
        }

        List<RuleDTO> rules = flag.getRules();

        if (rules == null || rules.isEmpty()) {
            log.debug(ApiLogMessages.get(LogPropertiesKeys.FLAG_EVAL_NO_RULES, code));
            return true;
        }

        for (RuleDTO rule : rules) {

            boolean result = ruleEvaluator.evaluate(rule, context);

            log.debug(ApiLogMessages.get(
                    LogPropertiesKeys.FLAG_EVAL_RULE_RESULT,
                    code,
                    rule.getAttribute(),
                    result
            ));

            if (!result) {
                log.debug(ApiLogMessages.get(LogPropertiesKeys.FLAG_EVAL_RULE_FAILED, code));
                return false;
            }
        }

        log.debug(ApiLogMessages.get(LogPropertiesKeys.FLAG_EVAL_ALL_RULES_OK, code));
        return true;
    }
}