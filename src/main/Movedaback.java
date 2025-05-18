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
    BufferedImage bridgeImage;
    public static double speed;
    GamePanel gp;
    GamePlayManager gpm;


    // Wallpaper + bridge effect variables
    private int x2;
    private int b2;
    private int x;
    private int b;
    private int bspeed;

    public Movedaback(GamePanel gp, GamePlayManager gpm) {
        this.gp = gp;
        this.gpm = gpm;
        setDefaultValues();

        // ——— Load the background image ———
        try {
            backgroundImage = ImageIO.read(new File("res/mainmenuwp/WPlvlonebb.png"));
            bridgeImage = ImageIO.read(new File("res/mainmenuwp/bridge.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDefaultValues() {
        x = 0;
        x2 = backgroundWidth;

        b = 0;
        b2 = backgroundWidth;

        y = 0;
        speed = 3;
        bspeed = (int) (speed * 2);
    }



    public void update() {
        // Move all images left
        x -= speed;
        x2 -= speed;
        b -= bspeed;
        b2 -= bspeed;

        // Reset positions when one image fully scrolls out of view
        if (b <= -backgroundWidth) {
            b = b2 + backgroundWidth;
        }

        if (b2 <= -backgroundWidth) {
            b2 = b + backgroundWidth;
        }
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
        g2.drawImage(bridgeImage, b, y - 100, null);
        g2.drawImage(bridgeImage, b2, y - 100, null);
    }
}