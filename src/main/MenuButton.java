package main;

import java.awt.*;

public class MenuButton {
    public Rectangle bounds;
    public String text;

    public MenuButton(int x, int y, int width, int height, String text) {
        this.bounds = new Rectangle(x, y, width, height);
        this.text = text;
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.DARK_GRAY);
        g2.fill(bounds);
        g2.setColor(Color.WHITE);
        g2.draw(bounds);
        g2.setFont(new Font("Arial", Font.BOLD, 20));

        // Center text
        FontMetrics fm = g2.getFontMetrics();
        int textX = bounds.x + (bounds.width - fm.stringWidth(text)) / 2;
        int textY = bounds.y + (bounds.height - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text, textX, textY);
    }

    public boolean isClicked(int mx, int my) {
        return bounds.contains(mx, my);
    }
}
