package com.example.flagsentineldemoapp.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.BootstrapFeatureFlagDTO;
import org.example.dto.BootstrapRuleResponse;
import org.example.service.FlagClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SdkService {

    private final FlagClient flagClient;

    public Map<String, BootstrapFeatureFlagDTO> getFlags() {
        return flagClient.getCache().getAllFlags();
    }

    public Map<Long, BootstrapRuleResponse> getRules() {
        return flagClient.getCache().getAllRules();
    }

    public boolean evaluate(String flagCode, Map<String, Object> ctx) {
        return flagClient.isEnabled(flagCode, ctx);
    }
}
