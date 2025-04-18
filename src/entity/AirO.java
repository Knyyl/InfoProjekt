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
        this.width = gp.TILE_SIZE;
        this.height = gp.TILE_SIZE;

        setDefaultValues();
    }
    public static void resetSpeeds() {
    }

    public void setDefaultValues() {
        x = rand.nextInt(1930) + 20000;
        y = 500;
        speed = 3;
    }

    public void update() {
        speed = speed * 1.0001;
        speed = Math.min(speed, 25.0); //Stops speed at certain speed, to keep game playable
        x = (int) (x - speed);

        if (x <= 0) {
            x = rand.nextInt(1970) + 20000;
        }
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.white);
        g2.fillRect(x, y, width, height);

    }

    public Rectangle getHitbox() {
        return new Rectangle(x, y, width, height);
    }
}
