package main;
import entity.Entity;
import main.GamePanel;
import main.GamePlayManager;

import java.awt.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Movedaback extends Entity {
    int backgroundWidth = 3840;
    BufferedImage backgroundImage;
    public static double speed;
    GamePanel gp;
    GamePlayManager gpm;

    public int width;
    public int height;

    // Starting position of background
    private int x2;

    public Movedaback(GamePanel gp, GamePlayManager gpm) {
        this.gp = gp;
        this.gpm = gpm;
        setDefaultValues();

        // ——— Load the background image ———
        try {
            backgroundImage = ImageIO.read(new File("res/mainmenuwp/WPlvlone.png"));
            width = backgroundImage.getWidth();
            height = backgroundImage.getHeight();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDefaultValues() {
        x = 0;
        x2 = backgroundWidth; // Not hardcoded to 1920 anymore
        y = 0;
        speed = 6;
    }


    public void update() {
        // Move both images left
        x -= speed;
        x2 -= speed;

        // Reset positions when one image fully scrolls out of view
        if (x <= -backgroundWidth) {
            x = x2 + backgroundWidth;
        }

        if (x2 <= -backgroundWidth) {
            x2 = x + backgroundWidth;
        }
    }


    public void draw(Graphics2D g2) {
        // Draw both backgrounds to create the scrolling effect
        g2.drawImage(backgroundImage, x, y, null);
        g2.drawImage(backgroundImage, x2, y, null);
    }
}
