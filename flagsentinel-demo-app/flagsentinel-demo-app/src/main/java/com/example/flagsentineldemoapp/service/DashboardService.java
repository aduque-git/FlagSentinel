package com.example.flagsentineldemoapp.service;

import com.example.flagsentineldemoapp.model.DashboardEvent;
import lombok.Getter;
import org.example.dto.BootstrapFeatureFlagDTO;
import org.example.dto.BootstrapRuleResponse;
import org.example.interfaces.FlagClientListener;
import org.example.service.FlagClient;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService implements FlagClientListener {

    private final FlagClient flagClient;

    @Getter
    private volatile boolean connected = false;

    private final Deque<DashboardEvent> events = new ArrayDeque<>();

    public DashboardService(FlagClient flagClient) {
        this.flagClient = flagClient;

        // REGISTRO PROFESIONAL DEL LISTENER
        flagClient.setListener(this);
    }

    // ---------------------------------------------------------
    // LISTENER DEL SDK
    // ---------------------------------------------------------

    @Override
    public void onConnected() {
        connected = true;
        addEvent("INFO", "WebSocket conectado");
    }

    @Override
    public void onDisconnected() {
        connected = false;
        addEvent("WARN", "WebSocket desconectado");
    }

    @Override
    public void onEvent(String message) {
        addEvent("INFO", message);
    }

    @Override
    public void onFlagUpdated(BootstrapFeatureFlagDTO flag) {
        addEvent("INFO", "Flag actualizada: " + flag.getFlagCode());
    }

    @Override
    public void onRuleUpdated(BootstrapRuleResponse rule) {
        addEvent("INFO", "Rule actualizada: " + rule.getId());
    }

    // ---------------------------------------------------------
    // MÉTODOS PARA EL CONTROLLER
    // ---------------------------------------------------------

    public Map<String, BootstrapFeatureFlagDTO> getFlags() {
        return flagClient.getCache().getAllFlags();
    }

    public Map<Long, BootstrapRuleResponse> getRules() {
        return flagClient.getCache().getAllRules();
    }

    public List<DashboardEvent> getLastEvents(int limit) {
        return events.stream().limit(limit).toList();
    }

    // ---------------------------------------------------------
    // HELPERS
    // ---------------------------------------------------------

    private void addEvent(String type, String message) {
        events.addFirst(new DashboardEvent(System.currentTimeMillis(), type, message));
        while (events.size() > 100) {
            events.removeLast();
        }
    }
}
