package com.example.flagsentinelapi.service;

import com.example.flagsentinelapi.model.FeatureFlag;
import com.example.flagsentinelapi.rules.FlagEvaluator;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FlagEvaluationService {

    private final FeatureFlagService flagService;
    private final FlagEvaluator evaluator = new FlagEvaluator();

    public FlagEvaluationService(FeatureFlagService flagService) {
        this.flagService = flagService;
    }

    public boolean evaluate(String key, Map<String, Object> context) {
        FeatureFlag flag = flagService.getByKey(key);
        if (flag == null) {
            return false;
        }
        return evaluator.isEnabledFor(flag, context);
    }
}
