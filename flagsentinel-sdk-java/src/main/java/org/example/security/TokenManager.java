package org.example.security;

public class TokenManager {

    private String token;

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public boolean hasToken() {
        return token != null && !token.isEmpty();
    }

    public void clear() {
        this.token = null;
    }
}
