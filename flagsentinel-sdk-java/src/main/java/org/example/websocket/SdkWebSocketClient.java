package org.example.websocket;

import lombok.Setter;
import org.example.cache.CacheProvider;
import org.example.dto.BootstrapFeatureFlagDTO;
import org.example.dto.BootstrapRuleResponse;
import org.example.util.JsonUtil;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.function.Consumer;

public class SdkWebSocketClient extends WebSocketClient {

    private final CacheProvider cache;
    private final String token;

    // Estos setters se usan INTERNAMENTE desde FlagClient
    @Setter private Consumer<BootstrapFeatureFlagDTO> onFlagUpdate;
    @Setter private Consumer<BootstrapRuleResponse> onRuleUpdate;

    @Setter private Runnable onConnected;
    @Setter private Runnable onDisconnected;

    @Setter private Consumer<String> onEvent;

    public SdkWebSocketClient(String endpoint, String token, CacheProvider cache) {
        super(URI.create(
                endpoint
                        .replace("https://", "wss://")
                        .replace("http://", "ws://")
                        + "/ws"
        ));
        this.cache = cache;
        this.token = token;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("📡 WebSocket conectado");
        if (onConnected != null) onConnected.run();
        if (onEvent != null) onEvent.accept("WebSocket conectado");

        send(buildConnectFrame());
        send(buildSubscribeFrame("sub-flags", "/topic/flags"));
        send(buildSubscribeFrame("sub-rules", "/topic/rules"));
    }

    @Override
    public void onMessage(String rawFrame) {
        System.out.println("🟩 FRAME RECIBIDO:");
        System.out.println(rawFrame);
        if (onEvent != null) onEvent.accept("FRAME: " + rawFrame);

        String body = extractStompBody(rawFrame);
        if (body == null || body.isBlank()) return;

        try {
            if (body.contains("\"flagCode\"")) {
                BootstrapFeatureFlagDTO flag =
                        JsonUtil.fromJson(body, BootstrapFeatureFlagDTO.class);

                cache.putFlag(flag);

                if (onFlagUpdate != null) onFlagUpdate.accept(flag);
                if (onEvent != null) onEvent.accept("Flag actualizada: " + flag.getFlagCode());
                return;
            }

            if (body.contains("\"attribute\"")) {
                BootstrapRuleResponse rule =
                        JsonUtil.fromJson(body, BootstrapRuleResponse.class);

                cache.putRule(rule);

                if (onRuleUpdate != null) onRuleUpdate.accept(rule);
                if (onEvent != null) onEvent.accept("Rule actualizada: " + rule.getId());
                return;
            }

            if (body.startsWith("Flag_deleted:")) {
                String idStr = body.replace("Flag_deleted:", "").trim();
                try {
                    Long id = Long.parseLong(idStr);

                    cache.getAllFlags().entrySet().removeIf(entry ->
                            entry.getValue().getId().equals(id)
                    );

                    if (onEvent != null) onEvent.accept("Flag eliminada: " + id);

                } catch (NumberFormatException e) {
                    if (onEvent != null) onEvent.accept("Flag_deleted inválido: " + body);
                }
                return;
            }

            if (body.startsWith("Rule_deleted:")) {
                String idStr = body.replace("Rule_deleted:", "").trim();
                try {
                    Long id = Long.parseLong(idStr);

                    cache.getAllRules().remove(id);

                    if (onEvent != null) onEvent.accept("Rule eliminada: " + id);

                } catch (NumberFormatException e) {
                    if (onEvent != null) onEvent.accept("Rule_deleted inválido: " + body);
                }
                return;
            }

        } catch (Exception e) {
            if (onEvent != null) onEvent.accept("Error parseando mensaje: " + e.getMessage());
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("🔌 WebSocket cerrado: " + reason);
        if (onDisconnected != null) onDisconnected.run();
        if (onEvent != null) onEvent.accept("WebSocket cerrado: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("⚠️ WebSocket error: " + ex.getMessage());
        if (onEvent != null) onEvent.accept("WebSocket error: " + ex.getMessage());
    }

    // ---------------------------------------------------------
    // HELPERS
    // ---------------------------------------------------------

    private String buildConnectFrame() {
        return "CONNECT\n" +
                "accept-version:1.2\n" +
                "Authorization: Bearer " + token + "\n" +
                "\n\0";
    }

    private String buildSubscribeFrame(String id, String destination) {
        return "SUBSCRIBE\n" +
                "id:" + id + "\n" +
                "destination:" + destination + "\n" +
                "\n\0";
    }

    private String extractStompBody(String frame) {
        int idx = frame.indexOf("\n\n");
        if (idx == -1) return null;

        String body = frame.substring(idx + 2).trim();
        if (body.endsWith("\0")) {
            body = body.substring(0, body.length() - 1);
        }
        return body;
    }
}
