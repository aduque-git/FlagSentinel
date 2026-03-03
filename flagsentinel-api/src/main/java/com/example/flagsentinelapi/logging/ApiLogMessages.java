package com.example.flagsentinelapi.logging;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ApiLogMessages {

    private static final Properties PROPS = new Properties();
    private static final String FILE = "/logging/messages.properties";

    private ApiLogMessages() {}

    static {
        load();
    }

    private static void load() {
        try (InputStream in = ApiLogMessages.class.getResourceAsStream(FILE)) {

            if (in == null) {
                throw new IllegalStateException("Logging messages file not found: " + FILE);
            }

            PROPS.load(in);

        } catch (IOException e) {
            throw new IllegalStateException("Failed to load logging messages", e);
        }
    }

    // =========================================================
    // BASIC GET
    // =========================================================
    public static String get(String key) {
        return PROPS.getProperty(key, "log.message.missing:" + key);
    }

    // =========================================================
    // PARAMETRIZED GET (SLF4J style)
    // =========================================================
    public static String get(String key, Object... params) {

        String template = get(key);

        if (params == null || params.length == 0) {
            return template;
        }

        return format(template, params);
    }

    // =========================================================
    // FAST {} FORMATTER (NO REGEX, NO MESSAGEFORMAT)
    // =========================================================
    private static String format(String template, Object... args) {

        StringBuilder result = new StringBuilder();
        int argIndex = 0;
        int i = 0;

        while (i < template.length()) {

            if (i + 1 < template.length()
                    && template.charAt(i) == '{'
                    && template.charAt(i + 1) == '}'
                    && argIndex < args.length) {

                result.append(args[argIndex++]);
                i += 2;
                continue;
            }

            result.append(template.charAt(i));
            i++;
        }

        return result.toString();
    }

    // =========================================================
    public static boolean hasKey(String key) {
        return key != null && PROPS.containsKey(key);
    }
}