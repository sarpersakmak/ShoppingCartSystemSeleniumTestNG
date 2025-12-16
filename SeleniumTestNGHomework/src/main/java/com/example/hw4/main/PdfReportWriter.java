package com.example.hw4.main;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;

/**
 * Optional PDF export using iText 5.
 */
public class PdfReportWriter {

    public void write(String filePath, String title, String body) throws Exception {
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        Font h1 = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
        Font normal = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);

        document.add(new Paragraph(title, h1));
        document.add(new Paragraph(" ", normal));
        document.add(new Paragraph(body, normal));

        document.close();
    }
}
