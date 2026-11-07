package textanalysis;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;

final class Plot {
    private static final Color[] COLORS = {
            new Color(31, 119, 180), new Color(214, 39, 40), new Color(44, 160, 44)
    };

    private Plot() {
    }

    static void save(String title, String xLabel, String yLabel, double[] xs,
                     Map<String, double[]> series, Path out) throws IOException {
        int w = 900;
        int h = 560;
        int left = 100;
        int right = 40;
        int top = 60;
        int bottom = 90;
        int pw = w - left - right;
        int ph = h - top - bottom;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);

        double xMin = xs[0];
        double xMax = xs[xs.length - 1];
        double yMax = 0;
        for (double[] values : series.values()) {
            for (double v : values) {
                yMax = Math.max(yMax, v);
            }
        }
        if (yMax <= 0) {
            yMax = 1;
        }
        yMax *= 1.1;

        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        g.drawString(title, left, 35);
        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g.drawLine(left, top, left, top + ph);
        g.drawLine(left, top + ph, left + pw, top + ph);

        for (int i = 0; i <= 5; i++) {
            double value = yMax * i / 5.0;
            int y = top + ph - (int) (ph * i / 5.0);
            g.setColor(new Color(225, 225, 225));
            g.drawLine(left, y, left + pw, y);
            g.setColor(Color.BLACK);
            g.drawString(String.format(Locale.ROOT, "%.3g", value), 10, y + 5);
        }
        for (double x : xs) {
            int px = left + (int) (pw * (x - xMin) / (xMax - xMin));
            g.drawLine(px, top + ph, px, top + ph + 5);
            g.drawString(String.valueOf((long) x), px - 18, top + ph + 22);
        }
        g.drawString(xLabel, left + pw / 2 - 40, h - 30);
        g.rotate(-Math.PI / 2);
        g.drawString(yLabel, -(top + ph / 2) - 40, 25);
        g.rotate(Math.PI / 2);

        int idx = 0;
        for (Map.Entry<String, double[]> entry : series.entrySet()) {
            Color color = COLORS[idx % COLORS.length];
            g.setColor(color);
            g.setStroke(new BasicStroke(2.5f));
            double[] values = entry.getValue();
            int prevX = 0;
            int prevY = 0;
            for (int i = 0; i < xs.length; i++) {
                int px = left + (int) (pw * (xs[i] - xMin) / (xMax - xMin));
                int py = top + ph - (int) (ph * values[i] / yMax);
                if (i > 0) {
                    g.drawLine(prevX, prevY, px, py);
                }
                g.fillOval(px - 4, py - 4, 8, 8);
                prevX = px;
                prevY = py;
            }
            int ly = top + 20 + idx * 22;
            g.fillRect(left + pw - 160, ly - 11, 14, 14);
            g.setColor(Color.BLACK);
            g.drawString(entry.getKey(), left + pw - 140, ly);
            idx++;
        }
        g.dispose();
        ImageIO.write(img, "png", out.toFile());
    }
}