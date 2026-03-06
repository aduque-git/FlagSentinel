package com.example.flagsentinelapi.util;

import com.example.flagsentinelapi.logging.ApiLogMessages;
import com.example.flagsentinelapi.logging.LogPropertiesKeys;
import com.example.flagsentinelapi.model.FeatureFlag;
import com.example.flagsentinelapi.model.Rule;
import com.example.flagsentinelapi.repository.FeatureFlagRepository;
import com.example.flagsentinelapi.repository.RuleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RuleRepository ruleRepo;
    private final FeatureFlagRepository flagRepo;

    @Override
    public void run(String... args) {

        long ruleCount = ruleRepo.count();
        long flagCount = flagRepo.count();

        if (ruleCount > 0 || flagCount > 0) {
            log.debug(ApiLogMessages.get(
                    LogPropertiesKeys.DATA_INIT_SKIPPED,
                    ruleCount,
                    flagCount
            ));
            return;
        }

        log.info(ApiLogMessages.get(LogPropertiesKeys.DATA_INIT_START));

        Rule rule = new Rule();
        rule.setAttribute("country");
        rule.setOperator("equals");
        rule.setValue("ES");

        Rule savedRule = ruleRepo.save(rule);

        FeatureFlag flag = new FeatureFlag();
        flag.setFlagCode("DEFAULT_FLAG");
        flag.setEnabled(true);
        flag.setRules(List.of(savedRule));

        FeatureFlag savedFlag = flagRepo.save(flag);

        log.info(ApiLogMessages.get(
                LogPropertiesKeys.DATA_INIT_COMPLETED,
                savedRule.getId(),
                savedFlag.getId()
        ));
    }
}