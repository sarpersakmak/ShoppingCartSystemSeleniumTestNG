package com.example.hw4.util;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

/**
 * Simple factory for Chrome/Firefox WebDrivers.
 * Use -Dheadless=true to run headless in CI.
 */
public final class WebDriverFactory {

    private WebDriverFactory() {}

    public static WebDriver create(String browser) {
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
        String b = browser == null ? "chrome" : browser.trim().toLowerCase();

        WebDriver driver;
        switch (b) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions fopts = new FirefoxOptions();
                if (headless) fopts.addArguments("-headless");
                driver = new FirefoxDriver(fopts);
                break;
            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions copts = new ChromeOptions();
                copts.addArguments("--disable-blink-features=AutomationControlled");
                copts.addArguments("--start-maximized");
                if (headless) copts.addArguments("--headless=new");
                driver = new ChromeDriver(copts);
                break;
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        return driver;
    }
}
