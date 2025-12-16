package com.example.hw4.task1;

import com.example.hw4.util.WebDriverFactory;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Task 1:
 * Connect to 3 different web sites and obtain the price of the same product (iPhone 15),
 * then print cheapest / average / most expensive.
 *
 * IMPORTANT REAL-WORLD NOTE:
 * Retail sites frequently change HTML and may show consent/CAPTCHA pages.
 * This test is written to be robust-ish, and it SKIPS a site if a price can't be found.
 * For presentation, you can re-run and/or adjust the CSS/XPath selectors if needed.
 *
 * You may override target URLs:
 *  -DamazonUrl=...
 *  -DbestBuyUrl=...
 *  -DappleUrl=...
 */
public class PriceComparatorTest {

    private static final String PRODUCT = "iPhone 15";

    private static final Pattern MONEY = Pattern.compile("(\\$|USD\\s?)(\\d{1,3}(?:,\\d{3})*(?:\\.\\d{2})?)");

    @Test
    public void comparePricesAcrossSites() {
        WebDriver driver = WebDriverFactory.create("chrome");
        try {
            Map<String, Double> prices = new LinkedHashMap<>();

            putIfFound(prices, "Apple", fetchApplePrice(driver));
            putIfFound(prices, "BestBuy", fetchBestBuyPrice(driver));
            putIfFound(prices, "Amazon", fetchAmazonPrice(driver));

            Assert.assertTrue(prices.size() >= 1,
                    "At least one site price must be retrieved (others may be blocked/changed)");

            report(prices);

            // basic assertions
            for (Map.Entry<String, Double> e : prices.entrySet()) {
                Assert.assertTrue(e.getValue() > 0.0, "Price should be positive for " + e.getKey());
            }
        } finally {
            driver.quit();
        }
    }

    private void putIfFound(Map<String, Double> map, String site, Optional<Double> price) {
        if (price.isPresent()) {
            map.put(site, price.get());
        } else {
            System.out.println("[Task1] Could not extract price from " + site + " (page changed or blocked).");
        }
    }

    private void report(Map<String, Double> prices) {
        double min = Double.MAX_VALUE, max = -1, sum = 0;
        String minSite = "", maxSite = "";
        for (Map.Entry<String, Double> e : prices.entrySet()) {
            double p = e.getValue();
            sum += p;
            if (p < min) { min = p; minSite = e.getKey(); }
            if (p > max) { max = p; maxSite = e.getKey(); }
        }
        double avg = sum / prices.size();

        System.out.println("\n================ TASK 1 PRICE REPORT ================");
        System.out.println("Product: " + PRODUCT);
        for (Map.Entry<String, Double> e : prices.entrySet()) {
            System.out.printf(" - %s: $%.2f%n", e.getKey(), e.getValue());
        }
        System.out.printf("Cheapest: $%.2f (%s)%n", min, minSite);
        System.out.printf("Average : $%.2f%n", avg);
        System.out.printf("Highest : $%.2f (%s)%n", max, maxSite);
        System.out.println("=====================================================\n");
    }

    private Optional<Double> fetchApplePrice(WebDriver driver) {
        String url = System.getProperty("appleUrl", "https://www.apple.com/iphone-15/");
        try {
            driver.get(url);

            // Apple often shows "From $xxx" somewhere on page
            String html = driver.getPageSource();
            return findFirstMoney(html).map(this::normalizeMoney);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Optional<Double> fetchBestBuyPrice(WebDriver driver) {
        String url = System.getProperty("bestBuyUrl", "https://www.bestbuy.com/site/searchpage.jsp?st=iphone+15");
        try {
            driver.get(url);

            // close occasional modal
            dismissIfPresent(driver, By.cssSelector("button[aria-label='Close']"));

            // First customer price on results
            List<By> candidates = Arrays.asList(
                    By.cssSelector("[data-testid='customer-price'] span[aria-hidden='true']"),
                    By.cssSelector(".sku-item:first-of-type .priceView-customer-price span"),
                    By.cssSelector(".sku-item:first-of-type [data-test='customer-price'] span"),
                    By.cssSelector(".sku-item:first-of-type .priceView-hero-price span")
            );

            for (By by : candidates) {
                Optional<Double> p = textAsPrice(driver, by);
                if (p.isPresent()) return p;
            }

            return findFirstMoney(driver.getPageSource()).map(this::normalizeMoney);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Optional<Double> fetchAmazonPrice(WebDriver driver) {
        String url = System.getProperty("amazonUrl", "https://www.amazon.com/s?k=iphone+15");
        try {
            driver.get(url);

            // Amazon can show consent/captcha; best effort only.
            // Try common price selectors in search results.
            List<By> candidates = Arrays.asList(
                    By.cssSelector("div.s-main-slot div[data-component-type='s-search-result']:first-of-type span.a-price span.a-offscreen"),
                    By.cssSelector("span.a-price span.a-offscreen")
            );

            for (By by : candidates) {
                Optional<Double> p = textAsPrice(driver, by);
                if (p.isPresent()) return p;
            }

            return findFirstMoney(driver.getPageSource()).map(this::normalizeMoney);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Optional<Double> textAsPrice(WebDriver driver, By by) {
        try {
            WebElement el = driver.findElement(by);
            String txt = el.getText();
            Optional<String> money = findFirstMoney(txt);
            return money.map(this::normalizeMoney);
        } catch (NoSuchElementException e) {
            return Optional.empty();
        }
    }

    private void dismissIfPresent(WebDriver driver, By by) {
        try {
            driver.findElement(by).click();
        } catch (Exception ignored) {}
    }

    private Optional<String> findFirstMoney(String text) {
        if (text == null) return Optional.empty();
        Matcher m = MONEY.matcher(text);
        if (m.find()) return Optional.of(m.group(2));
        return Optional.empty();
    }

    private double normalizeMoney(String amount) {
        String a = amount.replace(",", "");
        return Double.parseDouble(a);
    }
}
