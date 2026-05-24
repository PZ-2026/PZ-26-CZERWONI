package pl.edu.ur.teachly.report.library;

import java.util.List;
import pl.edu.ur.teachly.report.library.chart.PdfChartGenerator;
import pl.edu.ur.teachly.report.library.model.ReportData;
import pl.edu.ur.teachly.report.library.pdf.PdfDocumentBuilder;
import pl.edu.ur.teachly.report.library.pdf.PdfTableBuilder;

public class ReportGenerator {

    public static byte[] generate(ReportData data) throws Exception {
        PdfDocumentBuilder docBuilder = new PdfDocumentBuilder()
                .addTitle(data.getTitle())
                .addSubtitle(data.getSubtitle());

        // 1. Add summaries/paragraphs
        if (data.getSummaries() != null) {
            for (String pText : data.getSummaries()) {
                docBuilder.addParagraph(pText);
            }
        }

        // 2. Add chart if present
        if (data.getChartItems() != null && !data.getChartItems().isEmpty()) {
            byte[] chartBytes;
            if ("BAR".equalsIgnoreCase(data.getChartType())) {
                chartBytes = PdfChartGenerator.generateBarChart(data.getChartTitle(), data.getChartItems());
            } else {
                chartBytes = PdfChartGenerator.generatePieChart(data.getChartTitle(), data.getChartItems());
            }
            docBuilder.addImage(chartBytes);
        }

        // 3. Add table if present
        if (data.getHeaders() != null && !data.getHeaders().isEmpty() && data.getRows() != null && !data.getRows().isEmpty()) {
            int numCols = data.getHeaders().size();
            PdfTableBuilder tableBuilder = new PdfTableBuilder(numCols);
            tableBuilder.addHeaders(data.getHeaders().toArray(new String[0]));

            List<List<String>> rows = data.getRows();
            List<String> statuses = data.getRowStatuses();

            for (int i = 0; i < rows.size(); i++) {
                List<String> row = rows.get(i);
                String status = (statuses != null && i < statuses.size()) ? statuses.get(i) : "";
                tableBuilder.addRowWithStatus(status, row.toArray(new String[0]));
            }

            docBuilder.addTable(tableBuilder.build());
        }

        return docBuilder.build();
    }
}
