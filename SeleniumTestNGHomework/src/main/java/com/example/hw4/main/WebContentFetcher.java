package com.example.hw4.main;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;

/**
 * Downloads static HTML content through an HTTP GET request.
 */
public final class WebContentFetcher {

    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                    + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120 Safari/537.36";

    private final OkHttpClient client;

    public WebContentFetcher() {
        this.client = new OkHttpClient.Builder()
                .callTimeout(Duration.ofSeconds(30))
                .followRedirects(true)
                .followSslRedirects(true)
                .build();
    }

    public String fetchHtml(String url) throws IOException {
        validateHttpUrl(url);

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
                .header("Accept", "text/html,application/xhtml+xml")
                .header("Accept-Language", "en-US,en;q=0.9")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("HTTP " + response.code() + " for " + url);
            }
            return response.body() == null ? "" : response.body().string();
        }
    }

    private void validateHttpUrl(String url) {
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            if (scheme == null
                    || !("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))) {
                throw new IllegalArgumentException("Only HTTP and HTTPS URLs are supported: " + url);
            }
        } catch (URISyntaxException exception) {
            throw new IllegalArgumentException("Invalid URL: " + url, exception);
        }
    }
}
