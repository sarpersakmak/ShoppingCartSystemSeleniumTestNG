package com.example.hw4.task2;

import com.example.hw4.util.SeleniumUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for login.html (static page in resources).
 */
public class LoginPage {

    private final WebDriver driver;

    private final By username = By.id("username");
    private final By password = By.id("password");
    private final By loginBtn = By.id("loginBtn");
    private final By error = By.id("error");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public LoginPage open(String url) {
        driver.get(url);
        return this;
    }

    public LoginPage login(String user, String pass) {
        SeleniumUtils.safeType(driver, username, user, 5);
        SeleniumUtils.safeType(driver, password, pass, 5);
        SeleniumUtils.safeClick(driver, loginBtn, 5);
        return this;
    }

    public boolean isErrorVisible() {
        return SeleniumUtils.exists(driver, error) && driver.findElement(error).isDisplayed();
    }
}
