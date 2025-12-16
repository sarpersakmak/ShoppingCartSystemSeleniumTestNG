package com.example.hw4.main;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.time.Duration;

/**
 * Fetches static HTML content using OkHttp (requests equivalent in Java).
 */
public class WebContentFetcher {

    private final OkHttpClient client;

    public WebContentFetcher() {
        this.client = new OkHttpClient.Builder()
                .callTimeout(Duration.ofSeconds(30))
                .followRedirects(true)
                .followSslRedirects(true)
                .build();
    }

    public String fetchHtml(String url) throws IOException {
        Request req = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120 Safari/537.36")
                .header("Accept-Language", "en-US,en;q=0.9")
                .build();

        try (Response res = client.newCall(req).execute()) {
            if (!res.isSuccessful()) {
                throw new IOException("HTTP " + res.code() + " for " + url);
            }
            if (res.body() == null) return "";
            return res.body().string();
        }
    }
}
