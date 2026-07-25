[README.md](https://github.com/user-attachments/files/30376098/README.md)
# Selenium TestNG Homework Project

A refactored Java automation project that demonstrates Selenium WebDriver, TestNG, the Page Object Model, data-driven testing, TestNG factories, live price comparison, HTML parsing, automated web research, AI-assisted summarization, and optional PDF report generation.

The repository contains three homework tasks and one optional research application:

1. **Live price comparison** across Apple, Best Buy, and Amazon.
2. **Multi-browser login testing** against local HTML pages.
3. **TestNG `@Factory` demonstration** with browser-specific test instances.
4. **Automated research workflow** that searches Google, extracts page text, summarizes sources, and optionally exports a PDF report.

> Live websites frequently change their markup and may display CAPTCHA, consent, or regional pages. The project handles inaccessible browsers and websites as clearly as possible, but live-site selectors may require maintenance over time.

## Features

- Chrome and Firefox support through WebDriverManager
- Headless execution for CI or command-line environments
- Shared and validated WebDriver configuration
- Explicit waits instead of mixed implicit and explicit waits
- Page Object Model for the login workflow
- Data-driven valid and invalid credential scenarios
- Graceful TestNG skips when an installed browser cannot start
- TestNG `@Factory` browser instance generation
- `BigDecimal`-based price calculations
- Jsoup-based article text extraction
- OkHttp-based web requests
- OpenAI API key loading from environment variables or JVM properties
- Optional iText PDF report export
- Fast unit tests for HTML parsing and JSON response extraction
- English Javadocs and focused inline comments

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 11 | Project language and runtime target |
| Maven | Dependency management and build automation |
| Selenium WebDriver | Browser automation |
| TestNG | Test execution, data providers, assertions, and factories |
| WebDriverManager | Browser-driver setup |
| Jsoup | HTML parsing and text extraction |
| OkHttp | HTTP requests |
| iText 5 | Optional PDF generation |

## Project Structure

```text
.
├── pom.xml
├── testng.xml
├── README.md
├── REFACTORING_NOTES.md
├── .gitignore
└── src
    ├── main
    │   └── java/com/example/hw4
    │       ├── main
    │       │   ├── GoogleSearcher.java
    │       │   ├── HtmlContentParser.java
    │       │   ├── MainProjectRunner.java
    │       │   ├── OpenAiSummarizer.java
    │       │   ├── PdfReportWriter.java
    │       │   ├── WebContentFetcher.java
    │       │   └── model/SearchResult.java
    │       └── selenium
    │           └── WebDriverFactory.java
    └── test
        ├── java/com/example/hw4
        │   ├── main
        │   │   ├── HtmlContentParserTest.java
        │   │   ├── MainRunner.java
        │   │   └── OpenAiSummarizerTest.java
        │   ├── task1
        │   │   └── PriceComparatorTest.java
        │   ├── task2
        │   │   ├── LoginPage.java
        │   │   ├── LoginTest.java
        │   │   └── SuccessPage.java
        │   ├── task3
        │   │   ├── FactoryBrowserTitleTest.java
        │   │   └── FactoryTest.java
        │   └── util
        │       ├── BrowserProvider.java
        │       ├── SeleniumUtils.java
        │       └── TestDriverSupport.java
        └── resources
            ├── login.html
            └── success.html
```

## Prerequisites

Install the following software before running the project:

- Java Development Kit 11 or later
- Apache Maven 3.6 or later
- Google Chrome
- Mozilla Firefox, when Firefox test coverage is required

WebDriverManager downloads or resolves the compatible driver executables automatically. Browser applications themselves must still be installed.

Verify the installation:

```bash
java -version
mvn -version
```

## Installation

Clone the repository and enter the project directory:

```bash
git clone <repository-url>
cd ShoppingCartSystemSeleniumTestNG
```

Download dependencies and compile the project:

```bash
mvn clean compile
```

No API key is required for the Selenium homework tests.

## Running the Test Suite

### Run all configured tests

```bash
mvn clean test
```

### Run in headless mode

```bash
mvn clean test -Dheadless=true
```

### Run only Chrome scenarios

This is useful when Firefox is not installed:

```bash
mvn clean test -Dheadless=true -Dbrowsers=chrome
```

### Select multiple browsers

```bash
mvn clean test -Dheadless=true -Dbrowsers=chrome,firefox
```

The default browser list is `chrome,firefox`. When a configured browser is not installed or cannot start, its affected cases are marked as **skipped** with a descriptive message instead of producing an unclear setup error.

TestNG and Surefire reports are generated under:

```text
target/surefire-reports/
```

## Task 1: Live Price Comparison

`PriceComparatorTest` opens three retail websites and attempts to find a current advertised price for **iPhone 15**.

The test then calculates:

- Cheapest extracted price
- Most expensive extracted price
- Average extracted price

Money values use `BigDecimal` to avoid floating-point rounding problems.

Default sources:

- Apple
- Best Buy
- Amazon

Custom URLs can be supplied when a product page or selector changes:

```bash
mvn test \
  -Dheadless=true \
  -Dbrowsers=chrome \
  -DappleUrl="https://example.com/apple-product" \
  -DbestBuyUrl="https://example.com/bestbuy-product" \
  -DamazonUrl="https://example.com/amazon-product"
```

### Important limitation

This is a live integration test, not a deterministic unit test. Retail sites may:

- Change CSS selectors
- Redirect based on region
- Require cookie consent
- Show CAPTCHA pages
- Block automated traffic
- Present installment, trade-in, or accessory prices before the main product price

If no source can be read, TestNG skips the price-comparison case and reports the reason.

## Task 2: Login Testing

The login task uses local HTML files from `src/test/resources`, so the application under test does not require a web server or internet connection.

Valid credentials:

```text
Username: admin
Password: password
```

Covered scenarios:

1. Correct username and correct password
2. Wrong username and wrong password
3. Wrong username and correct password
4. Correct username and wrong password
5. Empty username and password

The test runs each scenario on every browser listed in the `browsers` property.

The Page Object Model separates:

- Element locators and page actions in `LoginPage`
- Success-page checks in `SuccessPage`
- Test data and assertions in `LoginTest`

## Task 3: TestNG Factory

`FactoryTest` demonstrates TestNG's `@Factory` annotation.

It creates one `FactoryBrowserTitleTest` instance for each configured browser. Every generated instance opens `https://example.com/` and verifies that the page title contains `Example`.

This task demonstrates how constructor parameters can produce separate test objects before execution.

## Automated Research Application

`MainProjectRunner` is separate from the homework test suite. Its workflow is:

1. Read a keyword from command-line arguments or standard input.
2. Search Google using Chrome and Selenium.
3. Collect unique organic results.
4. Download each page using OkHttp.
5. Extract readable article paragraphs using Jsoup.
6. Request an individual summary for each source.
7. Request a comparative conclusion.
8. Print the report to the console.
9. Optionally export the report to PDF.

### Configure the OpenAI API key

Never commit an API key to the repository.

#### Windows PowerShell

```powershell
$env:OPENAI_API_KEY="your-api-key"
$env:OPENAI_MODEL="gpt-4o-mini"
```

#### macOS or Linux

```bash
export OPENAI_API_KEY="your-api-key"
export OPENAI_MODEL="gpt-4o-mini"
```

The model can also be supplied as a JVM property:

```bash
-DopenaiModel=gpt-4o-mini
```

The code returns a clear placeholder message when no API key is configured, rather than exposing or embedding a secret.

### Run with a command-line keyword

```bash
mvn -q -DskipTests compile exec:java \
  -Dexec.mainClass=com.example.hw4.main.MainProjectRunner \
  -Dexec.args="Artificial Intelligence" \
  -Dheadless=true
```

When no `exec.args` value is provided, the application asks for a keyword interactively.

### Change the number of search results

```bash
-DresultLimit=3
```

The default value is `5`.

### Export a PDF report

```bash
mvn -q -DskipTests compile exec:java \
  -Dexec.mainClass=com.example.hw4.main.MainProjectRunner \
  -Dexec.args="Artificial Intelligence" \
  -Dheadless=true \
  -DreportPdf=reports/artificial-intelligence-report.pdf
```

The output directory is created automatically.

## Optional Presentation Runner

`MainRunner` can execute the TestNG suite and optionally launch the research application afterward.

```bash
mvn -q test-compile exec:java \
  -Dexec.classpathScope=test \
  -Dexec.mainClass=com.example.hw4.main.MainRunner \
  -Dheadless=true
```

Add the following property to launch the research application after the tests:

```bash
-DrunResearch=true
```

## Configuration Reference

| Property or variable | Default | Description |
|---|---:|---|
| `-Dheadless` | `false` | Runs browsers without visible windows |
| `-Dbrowsers` | `chrome,firefox` | Comma-separated browsers used by Tasks 2 and 3 |
| `-DappleUrl` | Apple iPhone 15 page | Overrides the Apple source URL |
| `-DbestBuyUrl` | Best Buy search page | Overrides the Best Buy source URL |
| `-DamazonUrl` | Amazon search page | Overrides the Amazon source URL |
| `OPENAI_API_KEY` | not configured | OpenAI API key environment variable |
| `OPENAI_MODEL` | `gpt-4o-mini` | Model environment variable |
| `-DopenaiKey` | environment value | JVM-level API-key override |
| `-DopenaiModel` | environment/default value | JVM-level model override |
| `-DresultLimit` | `5` | Maximum Google results processed |
| `-DreportPdf` | empty | Optional PDF output path |
| `-DrunResearch` | `false` | Starts research after the presentation runner tests |

## Testing Strategy

The project contains two different test categories:

### Deterministic tests

- Local login page tests
- HTML parser unit tests
- JSON response extraction unit tests

These tests do not depend on a changing third-party webpage.

### External integration tests

- Retail price extraction
- Example.com browser title verification
- Google search in the research application
- Page downloads and OpenAI requests

These depend on browsers, networking, third-party markup, and external services. Failures should be investigated as environment or integration issues before being treated as application logic defects.

## Troubleshooting

### Maven command is not found

Install Apache Maven and ensure its `bin` directory is included in the system `PATH`.

### Chrome or Firefox cannot start

- Confirm that the browser application is installed.
- Run only an available browser with `-Dbrowsers=chrome` or `-Dbrowsers=firefox`.
- Try headless execution with `-Dheadless=true`.
- Review the skip message in the TestNG report.

### A retail price cannot be extracted

The website may have changed its HTML, displayed a regional page, or blocked automation. Inspect the current page and update the candidate locators in `PriceComparatorTest`, or override the source URL with a JVM property.

### Google returns no collected results

Google may have shown a consent dialog, CAPTCHA, or automation challenge. Run with a visible browser, accept any required prompt manually, and review the selectors in `GoogleSearcher`.

### The research report contains an API-key placeholder

Set `OPENAI_API_KEY` or pass `-DopenaiKey`. Do not place the key directly in source code or commit it to Git.

### PDF export fails

Check that the destination is writable and that the report path ends with `.pdf`.

## Security Notes

- API keys are read from the environment or JVM properties.
- `.env`, generated PDFs, IDE settings, build output, and TestNG output are ignored by Git.
- Error messages do not intentionally print the API key.
- Do not commit browser profiles, cookies, access tokens, or generated reports containing sensitive information.

## Known Limitations

- Price extraction is selector-based and may require maintenance.
- The fallback page-wide price search can occasionally select a secondary price.
- Google may block automated searches.
- The small JSON parser is sufficient for the expected API response but is not a general-purpose JSON library.
- The local login page is a demonstration fixture, not a production authentication system.
- PDF output uses iText 5 and simple text formatting.

## Possible Future Improvements

- Move live retailer logic into dedicated Page Object classes.
- Add screenshots and page-source capture when a UI test fails.
- Add Docker or Selenium Grid support.
- Add CI workflows for Chrome headless tests.
- Replace live retailer checks with controlled mock pages for deterministic grading.
- Use structured API response models through a JSON library.
- Add richer PDF formatting and source metadata.

## License

No license has been selected for this repository. Add a license file before distributing or reusing the project publicly.
