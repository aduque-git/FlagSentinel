package com.example.flagsentinelapi.factory;

import com.example.flagsentinelapi.model.FeatureFlag;
import com.example.flagsentinelapi.model.Rule;

public class FeatureFlagTestFactory {

    public static FeatureFlag createEnabledFlag(String key) {
        FeatureFlag flag = new FeatureFlag();
        flag.setKey(key);
        flag.setEnabled(true);
        return flag;
    }

    public static FeatureFlag createDisabledFlag(String key) {
        FeatureFlag flag = new FeatureFlag();
        flag.setKey(key);
        flag.setEnabled(false);
        return flag;
    }

    public static FeatureFlag createFlagWithRule(String key, Rule rule) {
        FeatureFlag flag = createEnabledFlag(key);
        flag.addRule(rule);
        return flag;
    }
}
