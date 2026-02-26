package org.example.service;

import lombok.Getter;
import lombok.Setter;
import org.example.cache.CacheProvider;
import org.example.dto.*;
import org.example.http.CustomHttpClient;
import org.example.interfaces.FlagClientListener;
import org.example.security.AuthClient;
import org.example.security.TokenManager;
import org.example.util.LocalEvaluationEngine;
import org.example.websocket.SdkWebSocketClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlagClient {

    private final String endpoint;
    private final String username;
    private final String password;
    private final boolean enableWebSocket;

    private final TokenManager tokenManager = new TokenManager();
    @Getter
    private final CacheProvider cache = new CacheProvider();
    private final LocalEvaluationEngine localEngine = new LocalEvaluationEngine();

    private AuthClient authClient;
    private CustomHttpClient httpClient;
    private SdkWebSocketClient wsClient;

    // NUEVO: listener global profesional
    @Setter
    private FlagClientListener listener;

    public FlagClient(String endpoint, String username, String password, boolean enableWebSocket) {
        this.endpoint = endpoint;
        this.username = username;
        this.password = password;
        this.enableWebSocket = enableWebSocket;
    }

    // ---------------------------------------------------------
    // INIT
    // ---------------------------------------------------------
    public boolean initialize() {

        // 1. Login
        this.authClient = new AuthClient(endpoint);
        String token = authClient.login(username, password);

        if (token == null) {
            System.out.println("❌ Login failed");
            return false;
        }

        tokenManager.setToken(token);

        // 2. Crear HttpClient
        this.httpClient = new CustomHttpClient(endpoint, tokenManager);

        // 3. Bootstrap FLAGS
        BootstrapFeatureFlagResponse flagsResponse = httpClient.bootstrapFlags();
        if (flagsResponse == null) {
            System.out.println("❌ Bootstrap flags failed");
            return false;
        }

        cache.putAllFlags(flagsResponse.getFlags());

        // 4. Bootstrap RULES
        List<BootstrapRuleResponse> rulesResponse = httpClient.bootstrapRules();
        if (rulesResponse == null) {
            System.out.println("❌ Bootstrap rules failed");
            return false;
        }

        cache.putAllRules(rulesResponse);

        // 5. WebSocket opcional
        if (enableWebSocket) {
            connectWebSocket();
        }

        System.out.println("✔ FlagClient initialized");
        return true;
    }

    // ---------------------------------------------------------
    // WEBSOCKET
    // ---------------------------------------------------------
    private void connectWebSocket() {
        wsClient = new SdkWebSocketClient(endpoint, tokenManager.getToken(), cache);

        // Listener global profesional
        wsClient.setOnConnected(() -> {
            if (listener != null) listener.onConnected();
        });

        wsClient.setOnDisconnected(() -> {
            if (listener != null) listener.onDisconnected();
        });

        wsClient.setOnEvent(msg -> {
            if (listener != null) listener.onEvent(msg);
        });

        wsClient.setOnFlagUpdate(flag -> {
            cache.putFlag(flag);
            if (listener != null) listener.onFlagUpdated(flag);
        });

        wsClient.setOnRuleUpdate(rule -> {
            cache.putRule(rule);
            if (listener != null) listener.onRuleUpdated(rule);
        });

        wsClient.connect();
    }

    // ---------------------------------------------------------
    // EVALUATION
    // ---------------------------------------------------------
    public boolean isEnabled(String flagCode, Map<String, Object> context) {

        // 1. Buscar flag en cache
        BootstrapFeatureFlagDTO flag = cache.getFlag(flagCode);

        if (flag != null) {
            // Evaluación local
            return localEngine.evaluate(flag, cache, context);
        }

        // 2. Si no está en cache → evaluación remota
        Map<String, String> attributes = new HashMap<>();
        for (Map.Entry<String, Object> entry : context.entrySet()) {
            attributes.put(entry.getKey(), entry.getValue().toString());
        }

        FlagEvaluationRequest req = new FlagEvaluationRequest(flagCode, attributes);
        FlagEvaluationResponse res = httpClient.evaluate(req);

        if (res == null) {
            return false;
        }

        return res.isEnabled();
    }

    // ---------------------------------------------------------
    // STATE
    // ---------------------------------------------------------
    public boolean isConnected() {
        return wsClient != null && wsClient.isOpen();
    }
}
