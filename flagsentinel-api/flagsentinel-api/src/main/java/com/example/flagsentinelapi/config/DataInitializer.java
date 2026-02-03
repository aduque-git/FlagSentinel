package com.example.flagsentinelapi.config;

import com.example.flagsentinelapi.model.FeatureFlag;
import com.example.flagsentinelapi.model.Rule;
import com.example.flagsentinelapi.repository.FeatureFlagRepository;
import com.example.flagsentinelapi.repository.RuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RuleRepository ruleRepo;
    private final FeatureFlagRepository flagRepo;

    @Override
    public void run(String... args) {

        // Evitar duplicados si ya hay datos
        if (ruleRepo.count() > 0 || flagRepo.count() > 0) {
            return;
        }

        // 1. Crear una regla por defecto
        Rule rule = new Rule();
        rule.setAttribute("country");
        rule.setOperator("equals");
        rule.setValue("ES");

        Rule savedRule = ruleRepo.save(rule);

        // 2. Crear una flag por defecto
        FeatureFlag flag = new FeatureFlag();
        flag.setFlagCode("DEFAULT_FLAG");
        flag.setEnabled(true);

        // 3. Asignar la regla a la flag (Many-to-Many)
        flag.setRules(List.of(savedRule));

        FeatureFlag savedFlag = flagRepo.save(flag);

        System.out.println("Initialized default rule and feature flag:");
        System.out.println("Rule ID: " + savedRule.getId());
        System.out.println("Flag ID: " + savedFlag.getId());
    }
}
