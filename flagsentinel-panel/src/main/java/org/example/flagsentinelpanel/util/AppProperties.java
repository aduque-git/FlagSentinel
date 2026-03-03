package org.example.flagsentinelpanel.util;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class AppProperties {

    private final Environment env;

    public AppProperties(Environment env) {
        this.env = env;
    }

    public String get(String key) {
        return env.getProperty(key);
    }

    public int getInt(String key) {
        String value = env.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Property not found: " + key);
        }
        return Integer.parseInt(value);
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(env.getProperty(key));
    }
}

