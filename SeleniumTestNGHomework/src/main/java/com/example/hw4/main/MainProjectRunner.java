package com.example.hw4.main;

import com.example.hw4.main.model.SearchResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Runs the optional automated research workflow:
 * Google search -> page extraction -> AI summaries -> console/PDF report.
 */
public final class MainProjectRunner {

    private static final int DEFAULT_RESULT_LIMIT = 5;
    private static final int MAX_EXTRACTED_CHARACTERS = 6_000;
    private static final String DEFAULT_MODEL = "gpt-4o-mini";

    private MainProjectRunner() {
        // Application entry-point class; prevent instantiation.
    }

    public static void main(String[] args) {
        try {
            String keyword = resolveKeyword(args);
            if (keyword.isEmpty()) {
                System.out.println("No keyword entered. Exiting.");
                return;
            }

            String report = runResearch(keyword);
            System.out.println(report);
            exportPdfIfRequested(keyword, report);
        } catch (RuntimeException exception) {
            System.err.println("[Main] Application failed: " + exception.getMessage());
        }
    }

    static String runResearch(String keyword) {
        int resultLimit = readPositiveIntProperty("resultLimit", DEFAULT_RESULT_LIMIT);

        String apiKey = readConfiguration("openaiKey", "OPENAI_API_KEY", "YOUR_OPENAI_API_KEY");
        String model = readConfiguration("openaiModel", "OPENAI_MODEL", DEFAULT_MODEL);

        GoogleSearcher searcher = new GoogleSearcher();
        WebContentFetcher fetcher = new WebContentFetcher();
        HtmlContentParser parser = new HtmlContentParser();
        OpenAiSummarizer summarizer = new OpenAiSummarizer(apiKey, model);

        System.out.println("\n[Main] Searching Google for: " + keyword);
        List<SearchResult> results = searcher.searchTopResults(keyword, resultLimit);
        if (results.isEmpty()) {
            return buildReport(keyword, new ArrayList<>(),
                    "No search results were collected. Google may have displayed a consent or automation page.");
        }

        List<String> sourceSections = new ArrayList<>();
        List<String> summaries = new ArrayList<>();

        for (int index = 0; index < results.size(); index++) {
            SearchResult result = results.get(index);
            int displayIndex = index + 1;
            System.out.printf("%n[Main] (%d/%d) Fetching: %s%n",
                    displayIndex, results.size(), result.getUrl());

            String summary = summarizeResult(result, fetcher, parser, summarizer);
            sourceSections.add(formatSourceSection(displayIndex, result, summary));
            summaries.add("Source " + displayIndex + " (" + result.getUrl() + "):\n" + summary);
        }

        String conclusion = createComparativeConclusion(keyword, summaries, summarizer);
        return buildReport(keyword, sourceSections, conclusion);
    }

    private static String summarizeResult(
            SearchResult result,
            WebContentFetcher fetcher,
            HtmlContentParser parser,
            OpenAiSummarizer summarizer
    ) {
        try {
            String html = fetcher.fetchHtml(result.getUrl());
            String text = parser.extractMainText(html, MAX_EXTRACTED_CHARACTERS);
            if (text.isEmpty()) {
                return "[No readable paragraph text was extracted.]";
            }

            System.out.println("[Main] Generating summary...");
            return summarizer.summarize(result.getUrl(), text);
        } catch (Exception exception) {
            return "[Source processing failed: " + safeMessage(exception) + "]";
        }
    }

    private static String createComparativeConclusion(
            String keyword,
            List<String> summaries,
            OpenAiSummarizer summarizer
    ) {
        if (summaries.isEmpty()) {
            return "No summaries were available for comparison.";
        }

        System.out.println("\n[Main] Generating comparative conclusion...");
        try {
            return summarizer.comparativeSummary(keyword, String.join("\n\n", summaries));
        } catch (Exception exception) {
            return "[Comparative summary failed: " + safeMessage(exception) + "]";
        }
    }

    private static String formatSourceSection(int index, SearchResult result, String summary) {
        String title = result.getTitle().isEmpty() ? "Untitled result" : result.getTitle();
        return "---- Source " + index + " ----\n"
                + "Title: " + title + "\n"
                + "URL: " + result.getUrl() + "\n"
                + "Summary:\n" + summary + "\n";
    }

    private static String buildReport(
            String keyword,
            List<String> sourceSections,
            String conclusion
    ) {
        StringBuilder report = new StringBuilder();
        report.append("\n================ RESEARCH REPORT ================\n")
                .append("Keyword: ").append(keyword).append("\n\n");

        for (String section : sourceSections) {
            report.append(section).append('\n');
        }

        report.append("---- Comparative Conclusion ----\n")
                .append(conclusion)
                .append("\n=================================================\n");
        return report.toString();
    }

    private static void exportPdfIfRequested(String keyword, String report) {
        String pdfPath = System.getProperty("reportPdf", "").trim();
        if (pdfPath.isEmpty()) {
            return;
        }

        try {
            new PdfReportWriter().write(pdfPath, "Research Report: " + keyword, report);
            System.out.println("[Main] PDF report written to: " + pdfPath);
        } catch (Exception exception) {
            System.err.println("[Main] PDF export failed: " + safeMessage(exception));
        }
    }

    private static String resolveKeyword(String[] args) {
        if (args != null && args.length > 0) {
            return String.join(" ", args).trim();
        }

        System.out.print("Enter a keyword to search on Google: ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine().trim();
    }

    private static String readConfiguration(
            String systemProperty,
            String environmentVariable,
            String defaultValue
    ) {
        String propertyValue = System.getProperty(systemProperty);
        if (propertyValue != null && !propertyValue.trim().isEmpty()) {
            return propertyValue.trim();
        }

        String environmentValue = System.getenv(environmentVariable);
        if (environmentValue != null && !environmentValue.trim().isEmpty()) {
            return environmentValue.trim();
        }

        return defaultValue;
    }

    private static int readPositiveIntProperty(String propertyName, int defaultValue) {
        String rawValue = System.getProperty(propertyName);
        if (rawValue == null || rawValue.trim().isEmpty()) {
            return defaultValue;
        }

        try {
            int parsedValue = Integer.parseInt(rawValue.trim());
            if (parsedValue <= 0) {
                throw new IllegalArgumentException(propertyName + " must be greater than zero");
            }
            return parsedValue;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(propertyName + " must be an integer", exception);
        }
    }

    private static String safeMessage(Exception exception) {
        String message = exception.getMessage();
        return message == null || message.trim().isEmpty()
                ? exception.getClass().getSimpleName()
                : message;
    }
}
