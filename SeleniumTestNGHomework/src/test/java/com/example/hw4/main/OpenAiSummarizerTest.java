package com.example.hw4.task1;

import com.example.hw4.util.TestDriverSupport;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Compares the advertised price of the same product across several live stores.
 *
 * <p>Retail websites frequently change their markup or display CAPTCHA/consent
 * pages. A source that cannot be read is reported and ignored. The test is
 * skipped when no source is accessible, because that is an environment issue
 * rather than a deterministic application failure.</p>
 */
public final class PriceComparatorTest {

    private static final String PRODUCT_NAME = "iPhone 15";
    private static final Duration ELEMENT_TIMEOUT = Duration.ofSeconds(8);
    private static final Pattern US_DOLLAR_PATTERN = Pattern.compile(
            "(?:\\$|USD\\s*)(\\d{1,3}(?:,\\d{3})*(?:\\.\\d{1,2})?)",
            Pattern.CASE_INSENSITIVE
    );

    @Test(groups = "live")
    public void comparePricesAcrossRetailSites() {
        WebDriver driver = TestDriverSupport.createOrSkip("chrome");
        try {
            List<PriceQuote> quotes = new ArrayList<>();
            addQuoteIfAvailable(quotes, "Apple", fetchApplePrice(driver));
            addQuoteIfAvailable(quotes, "Best Buy", fetchBestBuyPrice(driver));
            addQuoteIfAvailable(quotes, "Amazon", fetchAmazonPrice(driver));

            if (quotes.isEmpty()) {
                throw new SkipException(
                        "No live store price could be extracted. The sites may have changed "
                                + "their markup or blocked automated access."
                );
            }

            PriceStatistics statistics = PriceStatistics.from(quotes);
            printReport(quotes, statistics);

            for (PriceQuote quote : quotes) {
                Assert.assertTrue(quote.getPrice().compareTo(BigDecimal.ZERO) > 0,
                        "The extracted price should be positive for " + quote.getStoreName());
            }
            Assert.assertTrue(statistics.getMinimum().compareTo(statistics.getMaximum()) <= 0,
                    "The minimum price must not exceed the maximum price");
        } finally {
            driver.quit();
        }
    }

    private Optional<BigDecimal> fetchApplePrice(WebDriver driver) {
        String url = System.getProperty("appleUrl", "https://www.apple.com/iphone-15/");
        return fetchPrice(
                driver,
                "Apple",
                url,
                Arrays.asList(
                        By.cssSelector("[data-autom='full-price']"),
                        By.cssSelector(".rc-prices-fullprice"),
                        By.cssSelector("main")
                )
        );
    }

    private Optional<BigDecimal> fetchBestBuyPrice(WebDriver driver) {
        String url = System.getProperty(
                "bestBuyUrl",
                "https://www.bestbuy.com/site/searchpage.jsp?st=iphone+15"
        );
        return fetchPrice(
                driver,
                "Best Buy",
                url,
                Arrays.asList(
                        By.cssSelector("[data-testid='customer-price']"),
                        By.cssSelector(".sku-item:first-of-type .priceView-customer-price"),
                        By.cssSelector(".sku-item:first-of-type .priceView-hero-price")
                )
        );
    }

    private Optional<BigDecimal> fetchAmazonPrice(WebDriver driver) {
        String url = System.getProperty("amazonUrl", "https://www.amazon.com/s?k=iphone+15");
        return fetchPrice(
                driver,
                "Amazon",
                url,
                Arrays.asList(
                        By.cssSelector(
                                "div.s-main-slot div[data-component-type='s-search-result'] "
                                        + "span.a-price span.a-offscreen"
                        ),
                        By.cssSelector("span.a-price span.a-offscreen")
                )
        );
    }

    private Optional<BigDecimal> fetchPrice(
            WebDriver driver,
            String storeName,
            String url,
            List<By> candidateLocators
    ) {
        try {
            driver.get(url);
            new WebDriverWait(driver, ELEMENT_TIMEOUT)
                    .until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

            dismissIfPresent(driver, By.cssSelector("button[aria-label='Close']"));

            for (By locator : candidateLocators) {
                Optional<BigDecimal> price = priceFromElements(driver, locator);
                if (price.isPresent()) {
                    return price;
                }
            }

            // Last-resort fallback for stores whose price container changed.
            return findFirstPrice(driver.getPageSource());
        } catch (WebDriverException exception) {
            System.out.println("[Task 1] " + storeName + " could not be read: "
                    + firstLine(exception.getMessage()));
            return Optional.empty();
        }
    }

    private Optional<BigDecimal> priceFromElements(WebDriver driver, By locator) {
        for (WebElement element : driver.findElements(locator)) {
            Optional<BigDecimal> price = findFirstPrice(element.getText());
            if (price.isPresent()) {
                return price;
            }
        }
        return Optional.empty();
    }

    private void dismissIfPresent(WebDriver driver, By locator) {
        List<WebElement> elements = driver.findElements(locator);
        if (!elements.isEmpty() && elements.get(0).isDisplayed()) {
            try {
                elements.get(0).click();
            } catch (WebDriverException ignored) {
                // The modal is optional; price extraction can continue without closing it.
            }
        }
    }

    private Optional<BigDecimal> findFirstPrice(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Optional.empty();
        }

        Matcher matcher = US_DOLLAR_PATTERN.matcher(text);
        while (matcher.find()) {
            BigDecimal price = new BigDecimal(matcher.group(1).replace(",", ""));
            if (price.compareTo(BigDecimal.ZERO) > 0) {
                return Optional.of(price);
            }
        }
        return Optional.empty();
    }

    private void addQuoteIfAvailable(
            List<PriceQuote> quotes,
            String storeName,
            Optional<BigDecimal> price
    ) {
        if (price.isPresent()) {
            quotes.add(new PriceQuote(storeName, price.get()));
        } else {
            System.out.println("[Task 1] No price was extracted from " + storeName + '.');
        }
    }

    private void printReport(List<PriceQuote> quotes, PriceStatistics statistics) {
        NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.US);

        System.out.println("\n================ TASK 1 PRICE REPORT ================");
        System.out.println("Product: " + PRODUCT_NAME);
        for (PriceQuote quote : quotes) {
            System.out.printf(" - %s: %s%n",
                    quote.getStoreName(), currency.format(quote.getPrice()));
        }
        System.out.printf("Cheapest: %s (%s)%n",
                currency.format(statistics.getMinimum()), statistics.getMinimumStore());
        System.out.printf("Average : %s%n", currency.format(statistics.getAverage()));
        System.out.printf("Highest : %s (%s)%n",
                currency.format(statistics.getMaximum()), statistics.getMaximumStore());
        System.out.println("=====================================================\n");
    }

    private String firstLine(String message) {
        if (message == null || message.trim().isEmpty()) {
            return "unknown WebDriver error";
        }
        return message.split("\\R", 2)[0];
    }

    private static final class PriceQuote {
        private final String storeName;
        private final BigDecimal price;

        private PriceQuote(String storeName, BigDecimal price) {
            this.storeName = storeName;
            this.price = price;
        }

        private String getStoreName() {
            return storeName;
        }

        private BigDecimal getPrice() {
            return price;
        }
    }

    private static final class PriceStatistics {
        private final PriceQuote minimumQuote;
        private final PriceQuote maximumQuote;
        private final BigDecimal average;

        private PriceStatistics(
                PriceQuote minimumQuote,
                PriceQuote maximumQuote,
                BigDecimal average
        ) {
            this.minimumQuote = minimumQuote;
            this.maximumQuote = maximumQuote;
            this.average = average;
        }

        private static PriceStatistics from(List<PriceQuote> quotes) {
            if (quotes == null || quotes.isEmpty()) {
                throw new IllegalArgumentException("At least one price quote is required");
            }

            PriceQuote minimum = quotes.stream()
                    .min(Comparator.comparing(PriceQuote::getPrice))
                    .orElseThrow(IllegalStateException::new);
            PriceQuote maximum = quotes.stream()
                    .max(Comparator.comparing(PriceQuote::getPrice))
                    .orElseThrow(IllegalStateException::new);

            BigDecimal total = quotes.stream()
                    .map(PriceQuote::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal average = total.divide(
                    BigDecimal.valueOf(quotes.size()),
                    2,
                    RoundingMode.HALF_UP
            );

            return new PriceStatistics(minimum, maximum, average);
        }

        private BigDecimal getMinimum() {
            return minimumQuote.getPrice();
        }

        private String getMinimumStore() {
            return minimumQuote.getStoreName();
        }

        private BigDecimal getMaximum() {
            return maximumQuote.getPrice();
        }

        private String getMaximumStore() {
            return maximumQuote.getStoreName();
        }

        private BigDecimal getAverage() {
            return average;
        }
    }
}
