package org.example.service;

public class FlagSDK {

    private String endpoint;
    private String username;
    private String password;
    private boolean enableWebSocket = true;

    public static FlagSDK init() {
        return new FlagSDK();
    }

    public FlagSDK endpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public FlagSDK username(String username) {
        this.username = username;
        return this;
    }

    public FlagSDK password(String password) {
        this.password = password;
        return this;
    }

    public FlagSDK connectWebSocket(boolean enable) {
        this.enableWebSocket = enable;
        return this;
    }

    public FlagClient build() {
        FlagClient client = new FlagClient(endpoint, username, password, enableWebSocket);

        boolean ok = client.initialize();
        if (!ok) {
            throw new RuntimeException("❌ FlagClient initialization failed");
        }

        return client;
    }
}
