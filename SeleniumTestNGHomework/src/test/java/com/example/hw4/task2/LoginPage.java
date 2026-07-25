package com.example.hw4.task2;

import com.example.hw4.util.SeleniumUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for the local login page in {@code src/test/resources}.
 */
public final class LoginPage {

    private static final int DEFAULT_TIMEOUT_SECONDS = 5;

    private static final By USERNAME_INPUT = By.id("username");
    private static final By PASSWORD_INPUT = By.id("password");
    private static final By LOGIN_BUTTON = By.id("loginBtn");
    private static final By ERROR_MESSAGE = By.id("error");

    private final WebDriver driver;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public LoginPage open(String pageUrl) {
        driver.get(pageUrl);
        SeleniumUtils.waitUntilVisible(driver, USERNAME_INPUT, DEFAULT_TIMEOUT_SECONDS);
        return this;
    }

    public LoginPage enterUsername(String username) {
        SeleniumUtils.type(driver, USERNAME_INPUT, username, DEFAULT_TIMEOUT_SECONDS);
        return this;
    }

    public LoginPage enterPassword(String password) {
        SeleniumUtils.type(driver, PASSWORD_INPUT, password, DEFAULT_TIMEOUT_SECONDS);
        return this;
    }

    public LoginPage submit() {
        SeleniumUtils.click(driver, LOGIN_BUTTON, DEFAULT_TIMEOUT_SECONDS);
        return this;
    }

    public LoginPage loginAs(String username, String password) {
        return enterUsername(username)
                .enterPassword(password)
                .submit();
    }

    public boolean isErrorVisible() {
        return SeleniumUtils.isDisplayed(driver, ERROR_MESSAGE);
    }

    public String getErrorMessage() {
        return isErrorVisible()
                ? SeleniumUtils.getText(driver, ERROR_MESSAGE, DEFAULT_TIMEOUT_SECONDS)
                : "";
    }
}
