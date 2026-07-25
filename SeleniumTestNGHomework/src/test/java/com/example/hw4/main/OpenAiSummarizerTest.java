package com.example.hw4.main;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit tests for the small JSON string extractor used by OpenAiSummarizer.
 */
public final class OpenAiSummarizerTest {

    @Test
    public void shouldExtractEscapedAssistantContent() {
        String json = "{\"choices\":[{\"message\":{\"role\":\"assistant\","
                + "\"content\":\"Line one\\nLine \\\"two\\\"\"}}]}";

        String content = OpenAiSummarizer.extractFirstJsonStringValue(json, "content");

        Assert.assertEquals(content, "Line one\nLine \"two\"");
    }

    @Test
    public void shouldReturnNullWhenKeyIsMissing() {
        Assert.assertNull(OpenAiSummarizer.extractFirstJsonStringValue("{\"value\":1}", "content"));
    }
}
