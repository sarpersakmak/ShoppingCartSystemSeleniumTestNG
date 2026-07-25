package com.example.hw4.util;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Shared explicit-wait helpers used by the page objects and UI tests.
 */
public final class SeleniumUtils {

    private SeleniumUtils() {
        // Utility class; prevent instantiation.
    }

    public static WebElement waitUntilVisible(WebDriver driver, By locator, int timeoutSeconds) {
        return new WebDriverWait(driver, duration(timeoutSeconds))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static void click(WebDriver driver, By locator, int timeoutSeconds) {
        WebElement element = new WebDriverWait(driver, duration(timeoutSeconds))
                .until(ExpectedConditions.elementToBeClickable(locator));
        element.click();
    }

    public static void type(WebDriver driver, By locator, String text, int timeoutSeconds) {
        WebElement element = waitUntilVisible(driver, locator, timeoutSeconds);
        element.clear();
        element.sendKeys(text == null ? "" : text);
    }

    public static boolean isDisplayed(WebDriver driver, By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException ignored) {
            return false;
        }
    }

    public static String getText(WebDriver driver, By locator, int timeoutSeconds) {
        return waitUntilVisible(driver, locator, timeoutSeconds).getText();
    }

    private static Duration duration(int timeoutSeconds) {
        if (timeoutSeconds <= 0) {
            throw new IllegalArgumentException("timeoutSeconds must be greater than zero");
        }
        return Duration.ofSeconds(timeoutSeconds);
    }
}
