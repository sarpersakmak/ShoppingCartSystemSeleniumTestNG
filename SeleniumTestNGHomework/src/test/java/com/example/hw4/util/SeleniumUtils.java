package com.example.hw4.util;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public final class SeleniumUtils {

    private SeleniumUtils() {}

    public static WebElement waitVisible(WebDriver driver, By by, int seconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(seconds))
                .until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    public static boolean exists(WebDriver driver, By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public static void safeClick(WebDriver driver, By by, int seconds) {
        WebElement el = new WebDriverWait(driver, Duration.ofSeconds(seconds))
                .until(ExpectedConditions.elementToBeClickable(by));
        el.click();
    }

    public static void safeType(WebDriver driver, By by, String text, int seconds) {
        WebElement el = waitVisible(driver, by, seconds);
        el.clear();
        el.sendKeys(text);
    }

    public static String safeGetText(WebElement el) {
        try {
            return el.getText();
        } catch (StaleElementReferenceException e) {
            return "";
        }
    }
}
