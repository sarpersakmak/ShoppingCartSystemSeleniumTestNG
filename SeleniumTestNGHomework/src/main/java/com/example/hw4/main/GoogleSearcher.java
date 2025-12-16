package com.example.hw4.main;

import com.example.hw4.main.model.SearchResult;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.util.*;

/**
 * Uses Selenium to perform a Google search and collect the first N non-ad results.
 *
 * NOTE: Google can show consent screens or bot detection.
 * This implementation tries to accept consent if present and continues best-effort.
 */
public class GoogleSearcher {

    public List<SearchResult> searchTopResults(String keyword, int limit) {
        WebDriverManager.chromedriver().setup();

        ChromeOptions opts = new ChromeOptions();
        opts.addArguments("--disable-blink-features=AutomationControlled");
        opts.addArguments("--start-maximized");
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
        if (headless) opts.addArguments("--headless=new");

        WebDriver driver = new ChromeDriver(opts);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));

        try {
            driver.get("https://www.google.com/ncr");

            acceptConsentIfShown(driver);

            WebElement q = driver.findElement(By.name("q"));
            q.sendKeys(keyword);
            q.sendKeys(Keys.ENTER);

            sleep(1500);

            List<SearchResult> results = new ArrayList<>();
            Set<String> seen = new HashSet<>();

            List<WebElement> links = driver.findElements(By.cssSelector("div#search a:has(h3)"));
            for (WebElement a : links) {
                if (results.size() >= limit) break;

                String href = a.getAttribute("href");
                String title = "";
                try { title = a.findElement(By.cssSelector("h3")).getText(); } catch (Exception ignored) {}

                if (href == null || href.trim().isEmpty()) continue;
                if (!href.startsWith("http")) continue;
                if (href.contains("google.com")) continue;
                if (href.contains("/aclk?")) continue;
                if (!seen.add(href)) continue;

                results.add(new SearchResult(title, href));
            }

            return results;
        } finally {
            driver.quit();
        }
    }

    private void acceptConsentIfShown(WebDriver driver) {
        List<By> candidates = Arrays.asList(
                By.id("L2AGLb"),
                By.cssSelector("button[aria-label='Accept all']"),
                By.cssSelector("button:has(div:contains('Accept all'))")
        );

        for (By by : candidates) {
            try {
                List<WebElement> els = driver.findElements(by);
                if (!els.isEmpty() && els.get(0).isDisplayed()) {
                    els.get(0).click();
                    sleep(800);
                    return;
                }
            } catch (Exception ignored) {}
        }
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
