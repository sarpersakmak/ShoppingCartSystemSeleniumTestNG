package com.example.hw4.task3;

import com.example.hw4.util.TestDriverSupport;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

/**
 * Browser-parameterized title test instantiated by {@link FactoryTest}.
 */
public final class FactoryBrowserTitleTest {

    private static final String TARGET_URL = "https://example.com/";
    private static final String EXPECTED_TITLE_TEXT = "example";

    private final String browser;

    public FactoryBrowserTitleTest(String browser) {
        this.browser = browser;
    }

    @Test
    public void pageTitleShouldContainExpectedText() {
        WebDriver driver = TestDriverSupport.createOrSkip(browser);
        try {
            try {
                driver.get(TARGET_URL);
            } catch (WebDriverException exception) {
                throw new SkipException(
                        "Skipping title verification because the external page could not be loaded: "
                                + firstLine(exception.getMessage()),
                        exception
                );
            }

            String actualTitle = driver.getTitle();
            Assert.assertTrue(actualTitle.toLowerCase().contains(EXPECTED_TITLE_TEXT),
                    "The page title should contain 'Example' on " + browser
                            + ", but was: " + actualTitle);
            System.out.println("[Task 3] " + browser + " title: " + actualTitle);
        } finally {
            driver.quit();
        }
    }

    private String firstLine(String message) {
        if (message == null || message.trim().isEmpty()) {
            return "unknown WebDriver error";
        }
        return message.split("\\R", 2)[0];
    }

    @Override
    public String toString() {
        return "FactoryBrowserTitleTest{browser='" + browser + "'}";
    }
}
