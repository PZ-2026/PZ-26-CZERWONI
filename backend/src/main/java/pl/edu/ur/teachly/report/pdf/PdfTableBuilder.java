package pl.edu.ur.teachly.report.pdf;

import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import java.awt.Color;

public class PdfTableBuilder {
    private final PdfPTable table;

    // Status colours (rows)
    static final Color COLOR_COMPLETED = new Color(198, 239, 206); // green
    static final Color COLOR_CANCELLED = new Color(255, 199, 206); // red
    static final Color COLOR_CONFIRMED = new Color(189, 215, 238); // blue
    static final Color COLOR_PENDING = new Color(255, 255, 220);   // light yellow
    static final Color COLOR_ROW_ALT = new Color(245, 245, 245);
    static final Color COLOR_ROW = Color.WHITE;
    static final Color COLOR_HEADER = new Color(68, 114, 196); // header blue

    // Stat colours (text)
    public static final Color COLOR_TEXT_COMPLETED = new Color(0, 128, 0); // dark green
    public static final Color COLOR_TEXT_CANCELLED = new Color(192, 0, 0); // dark red
    public static final Color COLOR_TEXT_CONFIRMED = new Color(0, 70, 180); // dark blue
    public static final Color COLOR_TEXT_PENDING = new Color(153, 102, 0); // dark yellow

    private static final Font HEADER_FONT =
            new Font(PdfDocumentBuilder.BASE_FONT_BOLD, 10, Font.BOLD, Color.WHITE);
    private static final Font ROW_FONT = new Font(PdfDocumentBuilder.BASE_FONT, 9, Font.NORMAL);

    private int rowIndex = 0;

    public PdfTableBuilder(int numColumns) {
        this.table = new PdfPTable(numColumns);
        this.table.setWidthPercentage(100);
        this.table.setSpacingBefore(10f);
        this.table.setSpacingAfter(10f);
    }

    public PdfTableBuilder(float[] columnWidths) {
        this.table = new PdfPTable(columnWidths);
        this.table.setWidthPercentage(100);
        this.table.setSpacingBefore(10f);
        this.table.setSpacingAfter(10f);
    }

    public PdfTableBuilder addHeaders(String... headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, HEADER_FONT));
            cell.setBackgroundColor(COLOR_HEADER);
            cell.setPadding(8f);
            cell.setBorderColor(new Color(40, 80, 150));
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
            this.table.addCell(cell);
        }
        rowIndex++;
        return this;
    }

    public PdfPTable build() {
        return table;
    }
}
