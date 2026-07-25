package com.example.hw4.main;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Extracts readable paragraph text from an HTML document.
 */
public final class HtmlContentParser {

    private static final String REMOVABLE_ELEMENTS =
            "script, style, nav, header, footer, aside, noscript, svg, form, iframe, "
                    + ".ads, .advertisement, [role='navigation']";
    private static final int MINIMUM_PARAGRAPH_LENGTH = 40;

    public String extractMainText(String html, int maxCharacters) {
        if (maxCharacters <= 0) {
            throw new IllegalArgumentException("maxCharacters must be greater than zero");
        }
        if (html == null || html.trim().isEmpty()) {
            return "";
        }

        Document document = Jsoup.parse(html);
        document.select(REMOVABLE_ELEMENTS).remove();

        Elements paragraphs = selectBestParagraphSet(document);
        List<String> normalizedParagraphs = new ArrayList<>();

        for (Element paragraph : paragraphs) {
            String text = normalizeWhitespace(paragraph.text());
            if (text.length() >= MINIMUM_PARAGRAPH_LENGTH) {
                normalizedParagraphs.add(text);
            }
        }

        return clip(String.join("\n\n", normalizedParagraphs), maxCharacters);
    }

    private Elements selectBestParagraphSet(Document document) {
        Element article = document.selectFirst("article");
        if (article != null && !article.select("p").isEmpty()) {
            return article.select("p");
        }

        Element main = document.selectFirst("main");
        if (main != null && !main.select("p").isEmpty()) {
            return main.select("p");
        }

        return document.select("p");
    }

    private String normalizeWhitespace(String text) {
        return text == null ? "" : text.replaceAll("\\s+", " ").trim();
    }

    private String clip(String text, int maxCharacters) {
        return text.length() <= maxCharacters ? text : text.substring(0, maxCharacters).trim();
    }
}
