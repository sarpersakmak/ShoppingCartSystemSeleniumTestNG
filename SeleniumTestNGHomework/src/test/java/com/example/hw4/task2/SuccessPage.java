package com.example.hw4.task2;

import com.example.hw4.util.SeleniumUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for the local successful-login page.
 */
public final class SuccessPage {

    private static final int DEFAULT_TIMEOUT_SECONDS = 5;
    private static final By WELCOME_MESSAGE = By.id("welcome");

    private final WebDriver driver;

    public SuccessPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isLoaded() {
        try {
            SeleniumUtils.waitUntilVisible(driver, WELCOME_MESSAGE, DEFAULT_TIMEOUT_SECONDS);
            return true;
        } catch (TimeoutException ignored) {
            return false;
        }
    }

    public String getWelcomeMessage() {
        return SeleniumUtils.getText(driver, WELCOME_MESSAGE, DEFAULT_TIMEOUT_SECONDS);
    }
}
