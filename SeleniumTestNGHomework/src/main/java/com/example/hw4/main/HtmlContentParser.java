package com.example.hw4.main;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.stream.Collectors;

/**
 * Extracts meaningful text from HTML using Jsoup.
 */
public class HtmlContentParser {

    public String extractMainText(String html, int maxChars) {
        if (html == null || html.trim().isEmpty()) return "";

        Document doc = Jsoup.parse(html);

        doc.select("script, style, nav, header, footer, aside, noscript, svg, form, iframe, ads, .ads, .advertisement").remove();

        Elements paragraphs = new Elements();

        Element article = doc.selectFirst("article");
        if (article != null) paragraphs.addAll(article.select("p"));

        if (paragraphs.isEmpty()) {
            Element main = doc.selectFirst("main");
            if (main != null) paragraphs.addAll(main.select("p"));
        }

        if (paragraphs.isEmpty()) {
            paragraphs = doc.select("p");
        }

        String text = paragraphs.stream()
                .map(Element::text)
                .filter(t -> t != null && t.trim().length() > 40)
                .collect(Collectors.joining("\n"));

        text = text.replaceAll("\\s+", " ").trim();

        if (text.length() > maxChars) {
            text = text.substring(0, maxChars);
        }
        return text;
    }
}
