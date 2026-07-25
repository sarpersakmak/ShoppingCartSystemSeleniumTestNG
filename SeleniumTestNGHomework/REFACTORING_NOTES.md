# Refactoring Notes

## Main improvements

- Flattened the repository into a standard Maven project root.
- Removed generated TestNG reports and Eclipse-specific project files.
- Moved local login fixtures from main resources to test resources.
- Centralized Chrome and Firefox setup in one `WebDriverFactory`.
- Replaced silent browser fallback with explicit browser validation.
- Added clear TestNG skips when an installed browser cannot start.
- Replaced mixed implicit/explicit waits with explicit waits.
- Expanded login coverage to include all documented invalid credential scenarios.
- Improved Page Object naming, method chaining, constants, and assertions.
- Changed money calculations from `double` to `BigDecimal`.
- Added clearer handling for live websites that change or block automation.
- Replaced unsupported/fragile Google CSS selectors with portable element checks.
- Removed fixed sleeps from Google result collection.
- Added input validation to the parser, fetcher, models, and configuration handling.
- Renamed `ChatGptSummarizer` to `OpenAiSummarizer` for clearer responsibility.
- Replaced the original quote-unsafe JSON response extraction with an escape-aware parser.
- Unified the default OpenAI model configuration.
- Added optional command-line keyword input, result-limit configuration, and PDF export.
- Added deterministic unit tests for HTML extraction and JSON parsing.
- Rewrote the README so its structure, class names, scenarios, commands, and limitations match the actual project.

## Behavior intentionally preserved

- The three homework tasks remain present.
- Chrome and Firefox remain supported.
- The price comparison still uses Apple, Best Buy, and Amazon by default.
- The login fixture still accepts `admin` / `password`.
- TestNG `@Factory` is still demonstrated.
- The research workflow still uses Selenium, OkHttp, Jsoup, OpenAI, and optional iText PDF output.

## Environment-dependent areas

The following areas cannot be guaranteed without the required local software and external access:

- Chrome and Firefox startup
- Driver downloads
- Live retail pages
- Google search results
- Third-party page downloads
- OpenAI API requests

These areas now report clearer failures or skips and are documented in the README.
