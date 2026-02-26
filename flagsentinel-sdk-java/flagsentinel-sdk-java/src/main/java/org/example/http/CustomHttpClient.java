package org.example.http;

import org.example.dto.BootstrapFeatureFlagResponse;
import org.example.dto.BootstrapRuleResponse;
import org.example.dto.FlagEvaluationRequest;
import org.example.dto.FlagEvaluationResponse;
import org.example.security.TokenManager;
import org.example.util.JsonUtil;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class CustomHttpClient {

    private final String endpoint;
    private final TokenManager tokenManager;
    private final HttpClient client;

    public CustomHttpClient(String endpoint, TokenManager tokenManager) {
        this.endpoint = endpoint;
        this.tokenManager = tokenManager;
        this.client = HttpClient.newHttpClient();
    }

    private HttpRequest.Builder authorizedRequest(String path) {
        return HttpRequest.newBuilder()
                .uri(URI.create(endpoint + path))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + tokenManager.getToken());
    }

    // ---------------------------------------------------------
    // BOOTSTRAP FLAGS
    // ---------------------------------------------------------
    public BootstrapFeatureFlagResponse bootstrapFlags() {
        try {
            HttpRequest request = authorizedRequest("/api/bootstrap/flags")
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return null;
            }

            return JsonUtil.fromJson(response.body(), BootstrapFeatureFlagResponse.class);

        } catch (Exception e) {
            return null;
        }
    }

    // ---------------------------------------------------------
    // BOOTSTRAP RULES
    // ---------------------------------------------------------
    public List<BootstrapRuleResponse> bootstrapRules() {
        try {
            HttpRequest request = authorizedRequest("/api/bootstrap/rules")
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return List.of();
            }

            // Lista directa
            return JsonUtil.fromJsonList(response.body(), BootstrapRuleResponse.class);

        } catch (Exception e) {
            return List.of();
        }
    }

    // ---------------------------------------------------------
    // EVALUATE FLAG (esto no cambia)
    // ---------------------------------------------------------
    public FlagEvaluationResponse evaluate(FlagEvaluationRequest req) {
        try {
            String body = JsonUtil.toJson(req);

            HttpRequest request = authorizedRequest("/api/flags/evaluate")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return null;
            }

            return JsonUtil.fromJson(response.body(), FlagEvaluationResponse.class);

        } catch (Exception e) {
            return null;
        }
    }
}
