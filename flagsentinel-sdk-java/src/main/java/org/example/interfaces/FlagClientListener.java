package org.example.interfaces;

import org.example.dto.BootstrapFeatureFlagDTO;
import org.example.dto.BootstrapRuleResponse;

public interface FlagClientListener {
    void onConnected();
    void onDisconnected();
    void onEvent(String message);
    void onFlagUpdated(BootstrapFeatureFlagDTO flag);
    void onRuleUpdated(BootstrapRuleResponse rule);
}
