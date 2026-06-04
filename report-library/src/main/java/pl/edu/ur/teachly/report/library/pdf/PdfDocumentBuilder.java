package pl.edu.ur.teachly.report.library.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
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
        String[] sysPaths =
                bold
                        ? new String[] {
                            "/usr/share/fonts/ttf-dejavu/DejaVuSans-Bold.ttf",
                            "C:/Windows/Fonts/arialbd.ttf",
                            "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf"
                        }
                        : new String[] {
                            "/usr/share/fonts/ttf-dejavu/DejaVuSans.ttf",
                            "C:/Windows/Fonts/arial.ttf",
                            "C:/Windows/Fonts/tahoma.ttf",
                            "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf"
                        };

        try (java.io.InputStream is = PdfDocumentBuilder.class.getResourceAsStream(resourcePath)) {
            if (is != null) {
                byte[] fontBytes = is.readAllBytes();
                return BaseFont.createFont(
                        resourcePath,
                        BaseFont.IDENTITY_H,
                        BaseFont.EMBEDDED,
                        true,
                        fontBytes,
                        null);
            }
        } catch (Exception ignored) {
        }

        for (String path : sysPaths) {
            try {
                return BaseFont.createFont(path, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            } catch (Exception ignored) {
            }
        }

        for (String path : sysPaths) {
            try {
                return BaseFont.createFont(path, BaseFont.CP1250, BaseFont.EMBEDDED);
            } catch (Exception ignored) {
            }
        }

        try {
            return BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1250, BaseFont.NOT_EMBEDDED);
        } catch (Exception e) {
            throw new RuntimeException("Could not load any font for PDF generation", e);
        }
    }

    private static final Font TITLE_FONT = new Font(BASE_FONT_BOLD, 20, Font.BOLD, new Color(15, 23, 42)); // Slate-900
    private static final Font SUBTITLE_FONT = new Font(BASE_FONT, 12, Font.NORMAL, new Color(71, 85, 105)); // Slate-600
    private static final Font NORMAL_FONT = new Font(BASE_FONT, 10, Font.NORMAL, new Color(51, 65, 85)); // Slate-700

    public PdfDocumentBuilder() {
        this.document = new Document(PageSize.A4, 36, 36, 36, 36); // standard margins
        this.outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);
        document.open();
    }

    public PdfDocumentBuilder addTitle(String title) {
        Paragraph p = new Paragraph(title, TITLE_FONT);
        p.setAlignment(Element.ALIGN_CENTER);
        p.setSpacingAfter(4f);
        p.setSpacingBefore(10f);
        document.add(p);
        return this;
    }

    public PdfDocumentBuilder addSubtitle(String subtitle) {
        Paragraph p = new Paragraph(subtitle, SUBTITLE_FONT);
        p.setAlignment(Element.ALIGN_CENTER);
        p.setSpacingAfter(18f);
        document.add(p);
        return this;
    }

    public PdfDocumentBuilder addParagraph(String text) {
        return addParagraph(text, NORMAL_FONT);
    }

    public PdfDocumentBuilder addParagraph(String text, Font font) {
        Paragraph p = new Paragraph(text, font);
        p.setSpacingAfter(5f);
        document.add(p);
        return this;
    }

    public PdfDocumentBuilder addImage(byte[] imageBytes) {
        try {
            Image img = Image.getInstance(imageBytes);
            img.setAlignment(Element.ALIGN_CENTER);
            img.setSpacingBefore(12f);
            img.setSpacingAfter(12f);
            // Scale down to fit standard page nicely (max width ~ 520)
            if (img.getWidth() > 520) {
                img.scaleToFit(520, 300);
            } else {
                img.scalePercent(80f);
            }
            document.add(img);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
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
