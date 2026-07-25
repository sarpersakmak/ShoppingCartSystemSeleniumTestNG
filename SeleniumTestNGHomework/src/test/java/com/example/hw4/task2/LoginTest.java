package com.example.hw4.task2;

import com.example.hw4.util.BrowserProvider;
import com.example.hw4.util.TestDriverSupport;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Verifies successful and unsuccessful login scenarios on each configured browser.
 */
public final class LoginTest {

    private static final String VALID_USERNAME = "admin";
    private static final String VALID_PASSWORD = "password";
    private static final String EXPECTED_ERROR = "Invalid username or password";

    @DataProvider(name = "browsers")
    public Object[][] browsers() {
        String[] browsers = BrowserProvider.configuredBrowsers();
        Object[][] data = new Object[browsers.length][1];
        for (int index = 0; index < browsers.length; index++) {
            data[index][0] = browsers[index];
        }
        return data;
    }

    @DataProvider(name = "invalidCredentials")
    public Object[][] invalidCredentials() {
        Object[][] scenarios = {
                {"wrong-user", "wrong-password", "both credentials are wrong"},
                {"wrong-user", VALID_PASSWORD, "username is wrong"},
                {VALID_USERNAME, "wrong-password", "password is wrong"},
                {"", "", "both fields are empty"}
        };

        List<Object[]> cases = new ArrayList<>();
        for (String browser : BrowserProvider.configuredBrowsers()) {
            for (Object[] scenario : scenarios) {
                cases.add(new Object[]{browser, scenario[0], scenario[1], scenario[2]});
            }
        }
        return cases.toArray(new Object[0][]);
    }

    @Test(dataProvider = "browsers")
    public void loginShouldSucceedWithValidCredentials(String browser) {
        WebDriver driver = TestDriverSupport.createOrSkip(browser);
        try {
            new LoginPage(driver)
                    .open(resourceUrl("login.html"))
                    .loginAs(VALID_USERNAME, VALID_PASSWORD);

            SuccessPage successPage = new SuccessPage(driver);
            Assert.assertTrue(successPage.isLoaded(),
                    "The success page should load for valid credentials on " + browser);
            Assert.assertTrue(successPage.getWelcomeMessage().contains(VALID_USERNAME),
                    "The welcome message should contain the username on " + browser);
        } finally {
            driver.quit();
        }
    }

    @Test(dataProvider = "invalidCredentials")
    public void loginShouldFailWithInvalidCredentials(
            String browser,
            String username,
            String password,
            String scenario
    ) {
        WebDriver driver = TestDriverSupport.createOrSkip(browser);
        try {
            LoginPage loginPage = new LoginPage(driver)
                    .open(resourceUrl("login.html"))
                    .loginAs(username, password);

            Assert.assertTrue(driver.getTitle().contains("Login"),
                    "The browser should remain on the login page when " + scenario);
            Assert.assertTrue(loginPage.isErrorVisible(),
                    "An error should be visible when " + scenario);
            Assert.assertEquals(loginPage.getErrorMessage(), EXPECTED_ERROR,
                    "The error message should be clear and consistent");
        } finally {
            driver.quit();
        }
    }

    private String resourceUrl(String resourceName) {
        URL resource = Thread.currentThread()
                .getContextClassLoader()
                .getResource(resourceName);
        if (resource == null) {
            throw new IllegalStateException("Test resource not found: " + resourceName);
        }
        return resource.toExternalForm();
    }
}
