package org.example.cache;

import org.example.dto.BootstrapFeatureFlagDTO;
import org.example.dto.BootstrapRuleResponse;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheProvider {

    // Cache de flags (flagCode → flag)
    private final Map<String, BootstrapFeatureFlagDTO> flags = new ConcurrentHashMap<>();

    // Cache de reglas (ruleId → rule)
    private final Map<Long, BootstrapRuleResponse> rules = new ConcurrentHashMap<>();


    // -----------------------------
    // FLAGS
    // -----------------------------
    public void putFlag(BootstrapFeatureFlagDTO flag) {
        flags.put(flag.getFlagCode(), flag);
    }

    public void putAllFlags(List<BootstrapFeatureFlagDTO> list) {
        for (BootstrapFeatureFlagDTO f : list) {
            flags.put(f.getFlagCode(), f);
        }
    }

    public BootstrapFeatureFlagDTO getFlag(String flagCode) {
        return flags.get(flagCode);
    }

    public Map<String, BootstrapFeatureFlagDTO> getAllFlags() {
        return flags;
    }


    // -----------------------------
    // RULES
    // -----------------------------
    public void putRule(BootstrapRuleResponse rule) {
        rules.put(rule.getId(), rule);
    }

    public void putAllRules(List<BootstrapRuleResponse> list) {
        for (BootstrapRuleResponse r : list) {
            rules.put(r.getId(), r);
        }
    }

    public BootstrapRuleResponse getRule(Long ruleId) {
        return rules.get(ruleId);
    }

    public Map<Long, BootstrapRuleResponse> getAllRules() {
        return rules;
    }


    // -----------------------------
    // CLEAR
    // -----------------------------
    public void clear() {
        flags.clear();
        rules.clear();
    }
}

