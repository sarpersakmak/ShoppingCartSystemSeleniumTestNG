package com.example.hw4.main;

import com.example.hw4.main.model.SearchResult;

import java.util.*;
import java.util.stream.Collectors;

public class MainProjectRunner {

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter a keyword to search on Google (e.g., Artificial Intelligence): ");
        String keyword = sc.nextLine().trim();
        if (keyword.isEmpty()) {
            System.out.println("No keyword entered. Exiting.");
            return;
        }

        String apiKey = System.getProperty("openaiKey",
                System.getenv().getOrDefault("OPENAI_API_KEY", "YOUR_OPENAI_API_KEY"));
        String model = System.getProperty("openaiModel", "gpt-3.5-turbo");

        GoogleSearcher searcher = new GoogleSearcher();
        WebContentFetcher fetcher = new WebContentFetcher();
        HtmlContentParser parser = new HtmlContentParser();
        ChatGptSummarizer gpt = new ChatGptSummarizer(apiKey, model);

        System.out.println("\n[Main] Searching Google for: " + keyword);
        List<SearchResult> results = searcher.searchTopResults(keyword, 5);

        if (results.isEmpty()) {
            System.out.println("[Main] No results collected (Google may have blocked automation).");
            return;
        }

        List<String> siteSections = new ArrayList<>();
        List<String> summariesOnly = new ArrayList<>();

        int i = 1;
        for (SearchResult r : results) {
            System.out.println("\n[Main] (" + i + "/5) Fetching: " + r.getUrl());
            String html;
            try {
                html = fetcher.fetchHtml(r.getUrl());
            } catch (Exception e) {
                String msg = "[Fetch failed: " + e.getMessage() + "]";
                siteSections.add(section(i, r.getUrl(), msg));
                summariesOnly.add(msg);
                i++;
                continue;
            }

            String text = parser.extractMainText(html, 6000);
            if (text.isEmpty()) {
                String msg = "[No readable text extracted]";
                siteSections.add(section(i, r.getUrl(), msg));
                summariesOnly.add(msg);
                i++;
                continue;
            }

            System.out.println("[Main] Summarizing with ChatGPT...");
            String summary;
            try {
                summary = gpt.summarize(r.getUrl(), text);
            } catch (Exception e) {
                summary = "[OpenAI call failed: " + e.getMessage() + "]";
            }

            siteSections.add(section(i, r.getUrl(), summary));
            summariesOnly.add("Site " + i + " (" + r.getUrl() + "):\n" + summary);
            i++;
        }

        String combined = summariesOnly.stream().collect(Collectors.joining("\n\n"));

        System.out.println("\n[Main] Generating comparative conclusion...");
        String conclusion;
        try {
            conclusion = gpt.comparativeSummary(keyword, combined);
        } catch (Exception e) {
            conclusion = "[OpenAI call failed: " + e.getMessage() + "]";
        }

        System.out.println(buildReport(keyword, siteSections, conclusion));
    }

    private static String section(int idx, String url, String summary) {
        return "---- Source " + idx + " ----\nURL: " + url + "\nSummary:\n" + summary + "\n";
    }

    private static String buildReport(String keyword, List<String> perSite, String conclusion) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n================ MAIN PROJECT REPORT ================\n");
        sb.append("Keyword: ").append(keyword).append("\n\n");
        for (String s : perSite) sb.append(s).append("\n");
        sb.append("---- Comparative Conclusion ----\n").append(conclusion).append("\n");
        sb.append("=====================================================\n");
        return sb.toString();
    }
}
