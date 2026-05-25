package pl.edu.ur.teachly.report.library.model;

import java.util.ArrayList;
import java.util.List;

public class ReportData {
    private String title;
    private String subtitle;
    private List<String> summaries = new ArrayList<>();
    private List<String> headers = new ArrayList<>();
    private List<List<String>> rows = new ArrayList<>();
    private List<String> rowStatuses = new ArrayList<>();
    private List<ChartItem> chartItems = new ArrayList<>();
    private String chartTitle;
    private String chartType = "PIE"; // default

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public List<String> getSummaries() {
        return summaries;
    }

    public void setSummaries(List<String> summaries) {
        this.summaries = summaries;
    }

    public void addSummary(String summary) {
        this.summaries.add(summary);
    }

    public List<String> getHeaders() {
        return headers;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public List<List<String>> getRows() {
        return rows;
    }

    public void setRows(List<List<String>> rows) {
        this.rows = rows;
    }

    public void addRow(List<String> row, String status) {
        this.rows.add(row);
        this.rowStatuses.add(status);
    }

    public List<String> getRowStatuses() {
        return rowStatuses;
    }

    public void setRowStatuses(List<String> rowStatuses) {
        this.rowStatuses = rowStatuses;
    }

    public List<ChartItem> getChartItems() {
        return chartItems;
    }

    public void setChartItems(List<ChartItem> chartItems) {
        this.chartItems = chartItems;
    }

    public void addChartItem(String label, double value) {
        this.chartItems.add(new ChartItem(label, value));
    }

    public String getChartTitle() {
        return chartTitle;
    }

    public void setChartTitle(String chartTitle) {
        this.chartTitle = chartTitle;
    }

    public String getChartType() {
        return chartType;
    }

    public void setChartType(String chartType) {
        this.chartType = chartType;
    }
}
