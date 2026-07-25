package com.example.hw4.main;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Fast unit tests for HTML text extraction; no browser or network is required.
 */
public final class HtmlContentParserTest {

    private final HtmlContentParser parser = new HtmlContentParser();

    @Test
    public void shouldPreferArticleParagraphsAndRemoveNavigationText() {
        String html = "<html><body>"
                + "<nav><p>This navigation paragraph is intentionally long and should be removed.</p></nav>"
                + "<article>"
                + "<p>This is the first meaningful article paragraph with enough text to be retained.</p>"
                + "<p>This is the second meaningful article paragraph with enough text to be retained.</p>"
                + "</article>"
                + "</body></html>";

        String result = parser.extractMainText(html, 1_000);

        Assert.assertTrue(result.contains("first meaningful article paragraph"));
        Assert.assertTrue(result.contains("second meaningful article paragraph"));
        Assert.assertFalse(result.contains("navigation paragraph"));
    }

    @Test
    public void shouldRespectMaximumCharacterLimit() {
        String html = "<article><p>"
                + "This paragraph is deliberately long enough to pass the minimum-length filter "
                + "and verify that the output is clipped to the configured maximum."
                + "</p></article>";

        String result = parser.extractMainText(html, 50);

        Assert.assertTrue(result.length() <= 50);
    }
}
