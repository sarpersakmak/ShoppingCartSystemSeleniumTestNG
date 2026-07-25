package com.example.hw4.util;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Reads the browser list from {@code -Dbrowsers=chrome,firefox}.
 */
public final class BrowserProvider {

    private static final String DEFAULT_BROWSERS = "chrome,firefox";

    private BrowserProvider() {
        // Utility class; prevent instantiation.
    }

    public static String[] configuredBrowsers() {
        String property = System.getProperty("browsers", DEFAULT_BROWSERS);
        Set<String> browsers = new LinkedHashSet<>();

        Arrays.stream(property.split(","))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .map(value -> value.toLowerCase(Locale.ROOT))
                .forEach(browsers::add);

        if (browsers.isEmpty()) {
            throw new IllegalArgumentException(
                    "No browsers configured. Example: -Dbrowsers=chrome,firefox"
            );
        }

        return browsers.toArray(new String[0]);
    }
}
