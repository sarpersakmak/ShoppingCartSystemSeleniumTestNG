package com.example.hw4.task3;

import com.example.hw4.util.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * A simple browser-parameterized test class (used by @Factory).
 */
public class FactoryBrowserTitleTest {

    private final String browser;

    public FactoryBrowserTitleTest(String browser) {
        this.browser = browser;
    }

    @Test
    public void shouldOpenExampleDotComAndVerifyTitle() {
        WebDriver driver = WebDriverFactory.create(browser);
        try {
            driver.get("https://example.com/");
            Assert.assertTrue(driver.getTitle().toLowerCase().contains("example"),
                    "Title should contain 'Example' on " + browser);
            System.out.println("[Factory] " + browser + " -> title: " + driver.getTitle());
        } finally {
            driver.quit();
        }
    }
}
