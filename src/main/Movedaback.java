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
        x = 0; // Starting x for background 1
        x2 = 1920; // Starting x for the second background image (to create a seamless loop)
        y = 0;

        speed = 6; // Speed of scrolling
    }

    public void update() {
        int resetwp = 0;
        if(resetwp == 0){
            if (x <= -1920) {
                x = 0;
            }

            if (x2 <= 0) {
                x2 = 1920;
            }
            resetwp = 1;
        }
        else{
            if (x <= -0) {
                x = 1920;
            }

            if (x2 <= 1920) {
                x2 = 0;
                resetwp = 0;
            }
        }
        // Move both images across the screen
        x -= speed;
        x2 -= speed;

        // If an image has completely gone off the screen, reset its position to create a looping effect
    }

    public void draw(Graphics2D g2) {
        // Draw both backgrounds to create the scrolling effect
        g2.drawImage(backgroundImage, x, y, null);
        g2.drawImage(backgroundImage, x2, y, null);
    }
}
