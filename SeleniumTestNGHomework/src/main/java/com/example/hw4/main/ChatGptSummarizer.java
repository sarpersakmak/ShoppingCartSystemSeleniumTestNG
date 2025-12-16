package com.example.hw4.main;

import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * ChatGPT summarizer using direct OpenAI HTTP call via OkHttp.
 * No OpenAI Java SDK needed (avoids dependency/version issues).
 *
 * Set API key via:
 *  - environment variable OPENAI_API_KEY
 *  - or JVM: -DopenaiKey=YOUR_KEY
 *
 * Model:
 *  - default: gpt-4o-mini (change by -DopenaiModel=...)
 */
public class ChatGptSummarizer {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client;
    private final String apiKey;
    private final String model;

    public ChatGptSummarizer(String apiKey, String model) {
        this.apiKey = apiKey;
        this.model = model;

        this.client = new OkHttpClient.Builder()
                .callTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    public String summarize(String url, String extractedText) throws IOException {
        String clipped = clip(extractedText, 6000);

        String prompt =
                "Summarize the following web page content in about 150-200 words. " +
                "Focus on key points and avoid fluff.\n\n" +
                "URL: " + url + "\n\nCONTENT:\n" + clipped;

        return chat(prompt);
    }

    public String comparativeSummary(String keyword, String combinedSummaries) throws IOException {
        String clipped = clip(combinedSummaries, 8000);

        String prompt =
                "Keyword: " + keyword + "\n\n" +
                "Given the following summaries, write a comparative conclusion (120-180 words):\n" +
                "- common themes\n" +
                "- key differences\n" +
                "- overall takeaway\n\n" +
                "SUMMARIES:\n" + clipped;

        return chat(prompt);
    }

    private String chat(String prompt) throws IOException {
        if (apiKey == null || apiKey.trim().isEmpty() || apiKey.equals("YOUR_OPENAI_API_KEY")) {
            return "[No OpenAI API key set. Set OPENAI_API_KEY env var or -DopenaiKey=YOUR_KEY]";
        }

        // Minimal JSON body for /v1/chat/completions
        // NOTE: We avoid external JSON libs by building a simple JSON string.
        String jsonBody =
                "{"
                        + "\"model\":\"" + escapeJson(model) + "\","
                        + "\"temperature\":0.2,"
                        + "\"messages\":["
                        + " {\"role\":\"system\",\"content\":\"You write clear, concise summaries in simple academic English.\"},"
                        + " {\"role\":\"user\",\"content\":\"" + escapeJson(prompt) + "\"}"
                        + "]"
                        + "}";

        Request req = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(jsonBody, JSON))
                .build();

        try (Response res = client.newCall(req).execute()) {
            if (!res.isSuccessful()) {
                return "[OpenAI HTTP error " + res.code() + "]: " + (res.body() != null ? res.body().string() : "");
            }
            String body = res.body() != null ? res.body().string() : "";
            // Very small extraction (no JSON lib): find `"content":"..."`
            return extractFirstContent(body);
        }
    }

    private String extractFirstContent(String json) {
        // Extremely small parser: looks for "content":"...".
        // Works for demo; for production use a JSON library.
        String key = "\"content\":\"";
        int idx = json.indexOf(key);
        if (idx < 0) return json; // fallback: return raw
        int start = idx + key.length();
        int end = json.indexOf("\"", start);
        if (end < 0) return json;

        String content = json.substring(start, end);
        return unescapeJson(content).trim();
    }

    private String clip(String text, int maxChars) {
        if (text == null) return "";
        text = text.trim();
        return text.length() <= maxChars ? text : text.substring(0, maxChars);
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String unescapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }
}
