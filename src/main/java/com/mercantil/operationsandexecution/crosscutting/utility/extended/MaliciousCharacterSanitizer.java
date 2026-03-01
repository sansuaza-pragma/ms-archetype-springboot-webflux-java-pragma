package com.mercantil.operationsandexecution.crosscutting.utility.extended;

import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.Map;

/**
 * Small utility to sanitize potentially malicious or log-breaking characters from strings
 * and maps of strings before logging or telemetry emission.
 * <p>
 * Current strategy replaces literal sequence tokens {@code \n}, {@code \r}, and {@code \t}
 * with a dash ({@code -}). It does <strong>not</strong> interpret escape sequences, it
 * performs straightforward string replacement.
 *
 * @since 1.0
 */
@UtilityClass
public class MaliciousCharacterSanitizer {

    /**
     * Sanitizes a single string by replacing {@code \n}, {@code \r}, and {@code \t} with {@code -}.
     *
     * @param input the original string (nullable)
     * @return sanitized string, or {@code null} if input is {@code null}
     */
    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        return input.replace("\\n", "-")
                .replace("\\r", "-")
                .replace("\\t", "-");
    }

    /**
     * Sanitizes all values of the provided map in-place using {@link #sanitize(String)}.
     * <p>
     * Note this method mutates the passed-in map via {@link java.util.Map#replaceAll}; it also returns
     * the same instance for convenience. If {@code input} is {@code null}, an empty map is returned.
     *
     * @param input map to sanitize (may be {@code null})
     * @return the same map instance with sanitized values, or {@link java.util.Collections#emptyMap()} if input is {@code null}
     */
    public static Map<String, String> sanitize(Map<String, String> input) {
        if (input == null) return Collections.emptyMap();
        input.replaceAll((k, v) -> sanitize(v));
        return input;
    }
}
