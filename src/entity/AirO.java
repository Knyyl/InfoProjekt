package entity;

import main.GamePanel;

import java.awt.*;
import java.util.Random;

public class AirO extends Entity {
    GamePanel gp;
    Random rand = new Random();

    public int width;
    public int height;

    public AirO(GamePanel gp) {
        this.gp = gp;
        this.width = gp.tileSize;
        this.height = gp.tileSize;

        setDefaultValues();
    }
    public static void resetSpeeds() {
    }

    public void setDefaultValues() {
        x = rand.nextInt(1930) + 20000;
        y = 500;
        speed = 0.6;
    }

    public void update() {
        speed = speed * 1.0001;
        x = (int) (x - speed);

        if (x <= 0) {
            x = rand.nextInt(1970) + 20000;
        }
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.white);
        g2.fillRect(x, y, width, height);

        // Optional: Draw hitbox for debugging
        g2.setColor(Color.red);
        g2.drawRect(x, y, width, height);
    }

    public Rectangle getHitbox() {
        return new Rectangle(x, y, width, height);
    }
}
