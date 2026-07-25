package com.example.hw4.util;

import com.example.hw4.selenium.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.SkipException;

/**
 * Converts missing-browser startup failures into TestNG skips with clear output.
 */
public final class TestDriverSupport {

    private TestDriverSupport() {
        // Utility class; prevent instantiation.
    }

    public static WebDriver createOrSkip(String browser) {
        try {
            return WebDriverFactory.create(browser);
        } catch (IllegalArgumentException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new SkipException(
                    "Skipping " + browser + " test because the browser could not start: "
                            + firstLine(exception.getMessage()),
                    exception
            );
        }
    }

    private static String firstLine(String message) {
        if (message == null || message.trim().isEmpty()) {
            return "unknown WebDriver error";
        }
        return message.split("\\R", 2)[0];
    }
}
