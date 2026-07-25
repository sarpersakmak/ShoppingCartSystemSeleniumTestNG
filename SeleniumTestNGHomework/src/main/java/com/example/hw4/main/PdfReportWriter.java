package com.example.hw4.main;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Writes the generated research report to an A4 PDF file.
 */
public final class PdfReportWriter {

    public void write(String filePath, String title, String body)
            throws IOException, DocumentException {
        Path outputPath = Paths.get(requireNonBlank(filePath, "filePath"))
                .toAbsolutePath()
                .normalize();

        Path parent = outputPath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        try (FileOutputStream outputStream = new FileOutputStream(outputPath.toFile())) {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font headingFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Font bodyFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);

            document.add(new Paragraph(requireNonBlank(title, "title"), headingFont));
            document.add(new Paragraph(" ", bodyFont));
            document.add(new Paragraph(body == null ? "" : body, bodyFont));
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }

    private String requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
