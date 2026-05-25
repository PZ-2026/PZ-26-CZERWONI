package pl.edu.ur.teachly.report.library.chart;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;
import javax.imageio.ImageIO;
import pl.edu.ur.teachly.report.library.model.ChartItem;

public class PdfChartGenerator {

    // Curated high-end color palette (Sleek HSL modern values)
    private static final Color[] COLORS = {
        new Color(79, 70, 229),   // Modern Indigo
        new Color(13, 148, 136),  // Modern Teal
        new Color(217, 70, 239),  // Sleek Fuchsia
        new Color(249, 115, 22),  // Premium Orange
        new Color(16, 185, 129),  // Emerald Green
        new Color(59, 130, 246),  // Royal Blue
        new Color(244, 63, 94),   // Rose Red
        new Color(234, 179, 8),   // Soft Yellow
        new Color(107, 114, 128)  // Neutral Gray
    };

    public static byte[] generatePieChart(String title, List<ChartItem> items) throws Exception {
        int logicalWidth = 560;
        int logicalHeight = 300;
        double scale = 2.0;
        int width = (int) (logicalWidth * scale);
        int height = (int) (logicalHeight * scale);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        g.scale(scale, scale);

        // High-quality rendering settings
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // White background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // Sleek soft border
        g.setColor(new Color(241, 245, 249)); // slate-100
        g.drawRect(0, 0, width - 1, height - 1);

        // Chart Title
        g.setColor(new Color(15, 23, 42)); // slate-900
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString(title, 20, 30);

        if (items == null || items.isEmpty()) {
            g.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g.setColor(new Color(100, 116, 139));
            g.drawString("Brak danych do wyświetlenia wykresu.", 180, 150);
            g.dispose();
            return toByteArray(image);
        }

        // Calculate Sum
        double total = 0;
        for (ChartItem item : items) {
            total += item.getValue();
        }

        int pieX = 30;
        int pieY = 60;
        int pieW = 200;
        int pieH = 200;

        double startAngle = 90; // Start at top
        int colorIdx = 0;

        int legendX = 260;
        int legendY = 70;
        g.setFont(new Font("SansSerif", Font.PLAIN, 11));

        for (ChartItem item : items) {
            if (total == 0) continue;
            double value = item.getValue();
            double angle = (value / total) * 360.0;

            Color color = COLORS[colorIdx % COLORS.length];

            // 1. Draw Slice
            g.setColor(color);
            g.fill(new Arc2D.Double(pieX, pieY, pieW, pieH, startAngle, -angle, Arc2D.PIE));

            // 2. Draw white elegant slice separator lines (only if multiple items exist)
            if (items.size() > 1) {
                g.setColor(Color.WHITE);
                g.draw(new Arc2D.Double(pieX, pieY, pieW, pieH, startAngle, -angle, Arc2D.PIE));
            }

            // 3. Draw Legend Box
            g.setColor(color);
            g.fillRoundRect(legendX, legendY, 12, 12, 3, 3);

            // 4. Draw Legend Label
            g.setColor(new Color(51, 65, 85)); // slate-700
            String percentLabel = String.format("%.1f%%", (value / total) * 100);
            String displayVal = String.format("%.1f", value);
            String fullLabel = item.getLabel() + " - " + displayVal + " (" + percentLabel + ")";

            // Prevent text overflow inside legend
            if (fullLabel.length() > 38) {
                fullLabel = fullLabel.substring(0, 35) + "...";
            }
            g.drawString(fullLabel, legendX + 20, legendY + 10);

            startAngle -= angle;
            colorIdx++;
            legendY += 24;

            // Overflow legend to second column if many entries
            if (legendY > height - 30) {
                legendY = 70;
                legendX += 150;
            }
        }

        g.dispose();
        return toByteArray(image);
    }

    public static byte[] generateBarChart(String title, List<ChartItem> items) throws Exception {
        int logicalWidth = 560;
        int logicalHeight = 300;
        double scale = 2.0;
        int width = (int) (logicalWidth * scale);
        int height = (int) (logicalHeight * scale);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        g.scale(scale, scale);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        g.setColor(new Color(241, 245, 249));
        g.drawRect(0, 0, width - 1, height - 1);

        g.setColor(new Color(15, 23, 42));
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString(title, 20, 30);

        if (items == null || items.isEmpty()) {
            g.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g.setColor(new Color(100, 116, 139));
            g.drawString("Brak danych do wyświetlenia wykresu.", 180, 150);
            g.dispose();
            return toByteArray(image);
        }

        double maxVal = 0;
        for (ChartItem item : items) {
            if (item.getValue() > maxVal) {
                maxVal = item.getValue();
            }
        }
        if (maxVal == 0) maxVal = 1.0;

        int chartX = 50;
        int chartY = 60;
        int chartW = 480;
        int chartH = 160;

        // Draw slate X and Y axes
        g.setColor(new Color(203, 213, 225)); // slate-300
        g.drawLine(chartX, chartY, chartX, chartY + chartH); // Y axis
        g.drawLine(chartX, chartY + chartH, chartX + chartW, chartY + chartH); // X axis

        int numBars = items.size();
        int gap = 15;
        int barW = (chartW - (gap * (numBars + 1))) / numBars;
        if (barW < 8) barW = 8;

        int curX = chartX + gap;
        int colorIdx = 0;

        g.setFont(new Font("SansSerif", Font.PLAIN, 9));

        for (ChartItem item : items) {
            double val = item.getValue();
            int barH = (int) ((val / maxVal) * (chartH - 20)); // leave space above bar for value text

            Color color = COLORS[colorIdx % COLORS.length];

            // 1. Draw rounded/smooth bar
            g.setColor(color);
            g.fillRoundRect(curX, chartY + chartH - barH, barW, barH, 4, 4);

            // 2. Draw text value above bar
            g.setColor(new Color(71, 85, 105)); // slate-600
            String valStr = String.format("%.1f", val);
            int strW = g.getFontMetrics().stringWidth(valStr);
            g.drawString(valStr, curX + (barW - strW) / 2, chartY + chartH - barH - 4);

            // 3. Draw vertical label below bar
            g.translate(curX + barW / 2.0, chartY + chartH + 6);
            g.rotate(Math.toRadians(90)); // Rotate 90 degrees (vertical downwards)

            String label = item.getLabel();
            if (label.length() > 18) {
                label = label.substring(0, 15) + "...";
            }

            int labelAscent = g.getFontMetrics().getAscent();
            g.drawString(label, 0.0f, (float) (labelAscent / 2.0 - 3.0));

            // Restore transform
            g.rotate(-Math.toRadians(90));
            g.translate(-(curX + barW / 2.0), -(chartY + chartH + 6));

            curX += barW + gap;
            colorIdx++;
        }

        g.dispose();
        return toByteArray(image);
    }

    private static byte[] toByteArray(BufferedImage image) throws Exception {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        }
    }
}
