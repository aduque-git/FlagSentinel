package org.example.security;

import org.example.dto.LoginRequest;
import org.example.dto.LoginResponse;
import org.example.util.JsonUtil;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthClient {

    private final String endpoint;
    private final HttpClient client = HttpClient.newHttpClient();

    public AuthClient(String endpoint) {
        this.endpoint = endpoint;
    }

    public String login(String username, String password) {
        try {
            LoginRequest loginRequest = new LoginRequest(username, password);
            String body = JsonUtil.toJson(loginRequest);

            System.out.println("➡️  Llamando a: " + endpoint + "/api/auth/login");
            System.out.println("➡️  Body enviado: " + body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint + "/api/auth/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("⬅️  STATUS: " + response.statusCode());
            System.out.println("⬅️  BODY: " + response.body());

            if (response.statusCode() != 200) {
                return null;
            }

            LoginResponse loginResponse =
                    JsonUtil.fromJson(response.body(), LoginResponse.class);

            return loginResponse.getToken();

        } catch (Exception e) {
            System.out.println("❌ EXCEPCIÓN EN LOGIN:");
            e.printStackTrace();
            return null;
        }
    }
}
