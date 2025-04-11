package entity;

import main.GamePanel;
import java.util.Random;
import java.awt.*;

public class Obstacle extends Entity {
    public static double speed;
    GamePanel gp;
    Random rand = new Random();

    public int width;
    public int height;

    public Obstacle(GamePanel gp) {
        this.gp = gp;
        this.width = gp.tileSize;
        this.height = gp.tileSize;

        setDefaultValues();
    }

    public void setDefaultValues() {
        x = rand.nextInt(1930) + 2000;
        y = 600;
        speed = 0.5;
    }

    public void update() {
        speed = speed * 1.0001;
        x = (int) (x - speed);

        if (x <= 0) {
            x = rand.nextInt(1920) + 2000;
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
