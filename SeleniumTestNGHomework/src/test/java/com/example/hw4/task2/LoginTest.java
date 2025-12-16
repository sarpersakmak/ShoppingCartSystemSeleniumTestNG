package com.example.hw4.task2;

import com.example.hw4.util.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.*;

import java.net.URL;

/**
 * Task 2:
 * - Static login page test on TWO browsers (Chrome + Firefox)
 * - Valid credentials: admin/password should succeed
 * - Invalid credentials should show error and stay on login page
 *
 * Loads src/main/resources/login.html via file:// URL
 */
public class LoginTest {

    @DataProvider(name = "browsers")
    public Object[][] browsers() {
        return new Object[][] {
                {"chrome"},
                {"firefox"}
        };
    }

    private String getResourceFileUrl(String resourceName) throws Exception {
        URL res = Thread.currentThread().getContextClassLoader().getResource(resourceName);
        if (res == null) throw new IllegalStateException("Resource not found: " + resourceName);
        return res.toExternalForm();
    }

    @Test(dataProvider = "browsers")
    public void loginShouldSucceedWithCorrectCredentials(String browser) throws Exception {
        WebDriver driver = WebDriverFactory.create(browser);
        try {
            String loginUrl = getResourceFileUrl("login.html");
            new LoginPage(driver)
                    .open(loginUrl)
                    .login("admin", "password");

            SuccessPage successPage = new SuccessPage(driver);
            Assert.assertTrue(successPage.isLoaded(), "Success page should load for correct credentials");
            Assert.assertTrue(successPage.getWelcomeText().contains("admin"), "Welcome should include username");
        } finally {
            driver.quit();
        }
    }

    @Test(dataProvider = "browsers")
    public void loginShouldFailWithWrongCredentials(String browser) throws Exception {
        WebDriver driver = WebDriverFactory.create(browser);
        try {
            String loginUrl = getResourceFileUrl("login.html");
            LoginPage loginPage = new LoginPage(driver)
                    .open(loginUrl)
                    .login("admin", "wrong");

            Assert.assertTrue(driver.getTitle().contains("Login"), "Should remain on Login page");
            Assert.assertTrue(loginPage.isErrorVisible(), "Error message should be visible for wrong credentials");
        } finally {
            driver.quit();
        }
    }
}
