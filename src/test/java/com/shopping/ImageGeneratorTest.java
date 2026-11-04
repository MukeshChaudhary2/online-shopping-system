package com.shopping;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageGeneratorTest {

    @Test
    public void generateSampleProductImages() throws IOException {
        String[] imageNames = {
                "default-product.png", "headphones.png", "smartwatch.png", "camera.png",
                "keyboard.png", "shirt.png", "dress.png", "wallet.png",
                "kettle.png", "cookware.png", "mugs.png", "java_book.png",
                "clean_code.png", "yogamat.png", "dumbbells.png"
        };

        Color[] bgColors = {
                new Color(226, 232, 240), new Color(219, 234, 254), new Color(220, 252, 231),
                new Color(254, 243, 199), new Color(254, 226, 226), new Color(243, 232, 255),
                new Color(255, 237, 213), new Color(224, 242, 254), new Color(241, 245, 249),
                new Color(230, 240, 255), new Color(245, 245, 245), new Color(220, 230, 242),
                new Color(235, 245, 235), new Color(250, 235, 235), new Color(230, 230, 245)
        };

        File dir = new File("src/main/webapp/uploads");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        for (int i = 0; i < imageNames.length; i++) {
            String name = imageNames[i];
            Color bg = bgColors[i % bgColors.length];

            BufferedImage img = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = img.createGraphics();

            // Smooth rendering
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Fill background
            g2.setColor(bg);
            g2.fillRect(0, 0, 400, 400);

            // Border
            g2.setColor(new Color(203, 213, 225));
            g2.setStroke(new BasicStroke(4));
            g2.drawRect(2, 2, 396, 396);

            // Draw placeholder icon and title
            g2.setColor(new Color(51, 65, 85));
            g2.setFont(new Font("SansSerif", Font.BOLD, 22));

            String title = name.replace(".png", "").replace("_", " ").toUpperCase();
            FontMetrics fm = g2.getFontMetrics();
            int x = (400 - fm.stringWidth(title)) / 2;
            int y = 200;
            g2.drawString(title, x, y);

            g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
            g2.setColor(new Color(100, 116, 139));
            String sub = "Online Shopping System";
            fm = g2.getFontMetrics();
            g2.drawString(sub, (400 - fm.stringWidth(sub)) / 2, y + 35);

            g2.dispose();

            File outFile = new File(dir, name);
            ImageIO.write(img, "png", outFile);
        }
        System.out.println("Generated 15 sample product PNG images successfully.");
    }
}
