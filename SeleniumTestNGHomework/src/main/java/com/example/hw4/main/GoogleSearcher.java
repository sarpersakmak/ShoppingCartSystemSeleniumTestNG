package com.example.hw4.main;

import com.example.hw4.main.model.SearchResult;
import com.example.hw4.selenium.WebDriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Performs a Google search with Selenium and collects unique organic results.
 *
 * <p>Search pages can display consent dialogs or automation challenges. The
 * implementation therefore uses best-effort handling and returns the results
 * that were successfully collected.</p>
 */
public final class GoogleSearcher {

    private static final String GOOGLE_URL = "https://www.google.com/ncr";
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);

    public List<SearchResult> searchTopResults(String keyword, int limit) {
        String normalizedKeyword = requireNonBlank(keyword, "keyword");
        if (limit <= 0) {
            throw new IllegalArgumentException("limit must be greater than zero");
        }

        WebDriver driver = WebDriverFactory.create("chrome");
        try {
            driver.get(GOOGLE_URL);
            acceptConsentIfPresent(driver);

            WebElement searchBox = new WebDriverWait(driver, WAIT_TIMEOUT)
                    .until(ExpectedConditions.elementToBeClickable(By.name("q")));
            searchBox.sendKeys(normalizedKeyword);
            searchBox.sendKeys(Keys.ENTER);

            new WebDriverWait(driver, WAIT_TIMEOUT)
                    .until(ExpectedConditions.presenceOfElementLocated(By.id("search")));

            return collectOrganicResults(driver, limit);
        } finally {
            driver.quit();
        }
    }

    private List<SearchResult> collectOrganicResults(WebDriver driver, int limit) {
        List<SearchResult> results = new ArrayList<>();
        Set<String> visitedUrls = new HashSet<>();

        // Selecting all anchors and checking for a nested h3 avoids browser-specific :has() support.
        List<WebElement> anchors = driver.findElements(By.cssSelector("div#search a"));
        for (WebElement anchor : anchors) {
            if (results.size() >= limit) {
                break;
            }

            SearchResult result = toSearchResult(anchor);
            if (result == null || !isExternalOrganicUrl(result.getUrl())) {
                continue;
            }

            if (visitedUrls.add(result.getUrl())) {
                results.add(result);
            }
        }

        return results;
    }

    private SearchResult toSearchResult(WebElement anchor) {
        try {
            WebElement heading = anchor.findElement(By.tagName("h3"));
            String url = anchor.getAttribute("href");
            String title = heading.getText();

            if (url == null || url.trim().isEmpty() || title == null || title.trim().isEmpty()) {
                return null;
            }
            return new SearchResult(title, url);
        } catch (NoSuchElementException ignored) {
            return null;
        }
    }

    private boolean isExternalOrganicUrl(String url) {
        String normalized = url.toLowerCase(Locale.ROOT);
        return normalized.startsWith("http")
                && !normalized.contains("google.com")
                && !normalized.contains("/aclk?");
    }

    private void acceptConsentIfPresent(WebDriver driver) {
        List<By> consentButtons = Arrays.asList(
                By.id("L2AGLb"),
                By.cssSelector("button[aria-label='Accept all']"),
                By.xpath("//button[.//*[contains(normalize-space(), 'Accept all')]]"),
                By.xpath("//button[contains(normalize-space(), 'Accept all')]")
        );

        for (By locator : consentButtons) {
            try {
                List<WebElement> matches = driver.findElements(locator);
                if (!matches.isEmpty() && matches.get(0).isDisplayed()) {
                    matches.get(0).click();
                    return;
                }
            } catch (RuntimeException ignored) {
                // Continue with the next known consent selector.
            }
        }
    }

    private String requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
