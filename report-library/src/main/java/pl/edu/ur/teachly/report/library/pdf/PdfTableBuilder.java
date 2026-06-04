package pl.edu.ur.teachly.report.library.pdf;

import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import java.awt.Color;

public class PdfTableBuilder {
    private final PdfPTable table;

    // Status colors (rows)
    static final Color COLOR_COMPLETED = new Color(220, 252, 231); // light green
    static final Color COLOR_CANCELLED = new Color(254, 226, 226); // light red
    static final Color COLOR_CONFIRMED = new Color(219, 234, 254); // light blue
    static final Color COLOR_PENDING = new Color(254, 249, 195);   // light yellow
    static final Color COLOR_ROW_ALT = new Color(248, 250, 252);   // slate-50
    static final Color COLOR_ROW = Color.WHITE;
    static final Color COLOR_HEADER = new Color(79, 70, 229);      // Indigo-600

    public static final Color COLOR_TEXT_COMPLETED = new Color(22, 101, 52); // dark green
    public static final Color COLOR_TEXT_CANCELLED = new Color(153, 27, 27); // dark red
    public static final Color COLOR_TEXT_CONFIRMED = new Color(30, 64, 175); // dark blue
    public static final Color COLOR_TEXT_PENDING = new Color(133, 77, 14);   // dark yellow

    private static final Font HEADER_FONT =
            new Font(PdfDocumentBuilder.BASE_FONT_BOLD, 9, Font.BOLD, Color.WHITE);
    private static final Font ROW_FONT = new Font(PdfDocumentBuilder.BASE_FONT, 8, Font.NORMAL, new Color(51, 65, 85));

    private int rowIndex = 0;

    public PdfTableBuilder(int numColumns) {
        this.table = new PdfPTable(numColumns);
        this.table.setWidthPercentage(100);
        this.table.setSpacingBefore(12f);
        this.table.setSpacingAfter(12f);
    }

    public PdfTableBuilder(float[] columnWidths) {
        this.table = new PdfPTable(columnWidths);
        this.table.setWidthPercentage(100);
        this.table.setSpacingBefore(12f);
        this.table.setSpacingAfter(12f);
    }

    public PdfTableBuilder addHeaders(String... headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, HEADER_FONT));
            cell.setBackgroundColor(COLOR_HEADER);
            cell.setPadding(6f);
            cell.setBorderColor(new Color(199, 210, 254)); // Indigo-200
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            this.table.addCell(cell);
        }
        return this;
    }

    public PdfTableBuilder addRow(String... rowData) {
        Color bgColor = (rowIndex % 2 == 0) ? COLOR_ROW : COLOR_ROW_ALT;
        for (String data : rowData) {
            PdfPCell cell = new PdfPCell(new Phrase(data, ROW_FONT));
            cell.setBackgroundColor(bgColor);
            cell.setPadding(5f);
            cell.setBorderColor(new Color(226, 232, 240)); // slate-200
            this.table.addCell(cell);
        }
        rowIndex++;
        return this;
    }

    public PdfTableBuilder addRowWithStatus(String status, String... rowData) {
        Color bgColor;
        switch (status.toUpperCase()) {
            case "COMPLETED" -> bgColor = COLOR_COMPLETED;
            case "CANCELLED" -> bgColor = COLOR_CANCELLED;
            case "CONFIRMED" -> bgColor = COLOR_CONFIRMED;
            case "PENDING" -> bgColor = COLOR_PENDING;
            default -> bgColor = (rowIndex % 2 == 0) ? COLOR_ROW : COLOR_ROW_ALT;
        }
        for (String data : rowData) {
            PdfPCell cell = new PdfPCell(new Phrase(data, ROW_FONT));
            cell.setBackgroundColor(bgColor);
            cell.setPadding(5f);
            cell.setBorderColor(new Color(226, 232, 240)); // slate-200
            this.table.addCell(cell);
        }
        rowIndex++;
        return this;
    }

    public PdfPTable build() {
        return table;
    }
}
