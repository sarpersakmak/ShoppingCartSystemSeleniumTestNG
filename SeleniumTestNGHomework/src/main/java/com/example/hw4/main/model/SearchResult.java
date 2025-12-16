package com.example.hw4.main.model;

public class SearchResult {
    private final String title;
    private final String url;

    public SearchResult(String title, String url) {
        this.title = title == null ? "" : title;
        this.url = url;
    }

    public String getTitle() { return title; }
    public String getUrl() { return url; }

    @Override
    public String toString() {
        return "SearchResult{title='" + title + "', url='" + url + "'}";
    }
}
