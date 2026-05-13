package pl.edu.ur.teachly.report.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.ByteArrayOutputStream;

public class PdfDocumentBuilder {
    private final Document document;
    private final ByteArrayOutputStream outputStream;

    // Shared BaseFont (loaded once, DejaVuSans supports full Unicode incl. Polish)
    public static final BaseFont BASE_FONT = loadBaseFont();
    public static final BaseFont BASE_FONT_BOLD = loadBaseFontBold();

    private static BaseFont loadBaseFont() {
        return loadSpecificFont(false);
    }

    private static BaseFont loadBaseFontBold() {
        return loadSpecificFont(true);
    }

    private static BaseFont loadSpecificFont(boolean bold) {
        String resourcePath = bold ? "/fonts/ArialBold.ttf" : "/fonts/Arial.ttf";
        String[] sysPaths = bold ? new String[]{
            "/usr/share/fonts/ttf-dejavu/DejaVuSans-Bold.ttf",
            "C:/Windows/Fonts/arialbd.ttf",
            "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf"
        } : new String[]{
            "/usr/share/fonts/ttf-dejavu/DejaVuSans.ttf",
            "C:/Windows/Fonts/arial.ttf",
            "C:/Windows/Fonts/tahoma.ttf",
            "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf"
        };

        // 1. Try loading from bundled resources (added manually to project)
        try (java.io.InputStream is = PdfDocumentBuilder.class.getResourceAsStream(resourcePath)) {
            if (is != null) {
                byte[] fontBytes = is.readAllBytes();
                return BaseFont.createFont(resourcePath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, fontBytes, null);
            }
        } catch (Exception ignored) {}

        // 2. Try system TTF files with IDENTITY_H
        for (String path : sysPaths) {
            try {
                return BaseFont.createFont(path, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            } catch (Exception ignored) {}
        }

        // 3. Try system TTF files with CP1250
        for (String path : sysPaths) {
            try {
                return BaseFont.createFont(path, BaseFont.CP1250, BaseFont.EMBEDDED);
            } catch (Exception ignored) {}
        }

        // 4. Absolute last resort: Standard Helvetica with CP1250
        try {
            return BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1250, BaseFont.NOT_EMBEDDED);
        } catch (Exception e) {
            throw new RuntimeException("Could not load any font for PDF generation", e);
        }
    }

    private static final Font TITLE_FONT = new Font(BASE_FONT_BOLD, 22, Font.BOLD);
    private static final Font SUBTITLE_FONT =
            new Font(BASE_FONT, 13, Font.NORMAL, new Color(80, 80, 80));
    private static final Font NORMAL_FONT = new Font(BASE_FONT, 11, Font.NORMAL);

    public PdfDocumentBuilder() {
        this.document = new Document(PageSize.A4);
        this.outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);
        document.open();
    }

    public PdfDocumentBuilder addTitle(String title) {
        Paragraph p = new Paragraph(title, TITLE_FONT);
        p.setAlignment(Element.ALIGN_CENTER);
        p.setSpacingAfter(6f);
        document.add(p);
        return this;
    }

    public PdfDocumentBuilder addSubtitle(String subtitle) {
        Paragraph p = new Paragraph(subtitle, SUBTITLE_FONT);
        p.setAlignment(Element.ALIGN_CENTER);
        p.setSpacingAfter(16f);
        document.add(p);
        return this;
    }

    public PdfDocumentBuilder addParagraph(String text) {
        return addParagraph(text, NORMAL_FONT);
    }

    public PdfDocumentBuilder addParagraph(String text, Font font) {
        Paragraph p = new Paragraph(text, font);
        p.setSpacingAfter(4f);
        document.add(p);
        return this;
    }

    public PdfDocumentBuilder addTable(PdfPTable table) {
        document.add(table);
        return this;
    }

    public byte[] build() {
        if (document.isOpen()) {
            document.close();
        }
        return outputStream.toByteArray();
    }
}
