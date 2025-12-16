package com.example.hw4.task2;

import com.example.hw4.util.SeleniumUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for success.html.
 */
public class SuccessPage {

    private final WebDriver driver;
    private final By welcome = By.id("welcome");

    public SuccessPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isLoaded() {
        return SeleniumUtils.exists(driver, welcome);
    }

    public String getWelcomeText() {
        if (!isLoaded()) return "";
        return driver.findElement(welcome).getText();
    }
}
