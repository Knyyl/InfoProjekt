package main;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MenuButton {
    public Rectangle bounds;
    public String id;
    public String text;
    public Image icon;

    public MenuButton(int x, int y, int width, int height, String id, Image icon) {
        this.bounds = new Rectangle(x, y, width, height);
        this.id = id;
        this.icon = icon;
    }

    public void draw(Graphics2D g2) {
        // Enable anti-aliasing for better quality
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        if (icon != null) {
            // Draw the icon with transparency
            g2.drawImage(icon, bounds.x, bounds.y, bounds.width, bounds.height, null);

            // Optional: Add a subtle highlight when mouse is over the button
            // (You would need to track mouse position separately)
        } else {
            // Fallback to text button if no icon is available
            g2.setColor(new Color(50, 50, 50, 200)); // Semi-transparent dark background
            g2.fill(bounds);
            g2.setColor(Color.WHITE);
            g2.draw(bounds);

            if (text != null) {
                g2.setFont(new Font("Arial", Font.BOLD, 20));
                FontMetrics fm = g2.getFontMetrics();
                int textX = bounds.x + (bounds.width - fm.stringWidth(text)) / 2;
                int textY = bounds.y + (bounds.height - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(text, textX, textY);
            }
        }
    }

    public boolean isClicked(int mx, int my) {

        return bounds.contains(mx, my);
    }
}