package entity;

import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import main.Settings;

import javax.imageio.ImageIO;

public class AirO extends Entity {
    Settings settings;
    BufferedImage[] frames;
    int frameIndex = 0;
    int animationCounter = 0;
    int animationSpeed = 20; // Lower = faster animation
    GamePanel gp;
    Random rand = new Random();

    public int width;
    public int height;

    public AirO(GamePanel gp) {
        this.gp = gp;
        this.width = gp.TILE_SIZE;
        this.height = gp.TILE_SIZE;

        setDefaultValues();
        try {
            BufferedImage sheet = null;
            if (settings.level == 1) {
                sheet = ImageIO.read(new File("res/sprites/dronepolice.png"));
            } else if (settings.level == 2) {
                sheet = ImageIO.read(new File("res/sprites/drone.png"));
            }

            int origW = sheet.getWidth() / 4;   // original frame width
            int origH = sheet.getHeight();      // original frame height
            int scaleFactor = 1;                // Bigger = bigger sprite

            // update your obstacle dimensions
            width = origW * scaleFactor;
            height = origH * scaleFactor;

            frames = new BufferedImage[4];
            for (int i = 0; i < 4; i++) {
                // extract the small frame
                BufferedImage orig = sheet.getSubimage(i * origW, 0, origW, origH);

                // create a new BufferedImage at the scaled size
                BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = scaled.createGraphics();
                // use nearest‑neighbor to keep pixels sharp
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                g.drawImage(orig, 0, 0, width / 2, height / 2, null);
                g.dispose();

                frames[i] = scaled;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void resetSpeeds() {
    }

    public void setDefaultValues() {
        x = rand.nextInt(1930) + 10000;
        y = 400;
        speed = 3;
    }

    public void update() {
        speed = speed * 1.0001;
        speed = Math.min(speed, 25.0); //Stops speed at certain speed, to keep game playable
        x = (int) (x - speed);

        if (x <= -100) {
            x = rand.nextInt(1970) + 3000;
        }
        animationCounter++;
        if (animationCounter >= animationSpeed) {
            frameIndex = (frameIndex + 1) % frames.length;
            animationCounter = 0;
        }
    }

    public void draw(Graphics2D g2) {
        if (frames != null && frames[frameIndex] != null) {
            g2.drawImage(frames[frameIndex], x, y, null);
        } else {
            // Fallback: draw a white box
            g2.setColor(Color.white);
            g2.fillRect(x, y, width, height);

        }
    }

    public Rectangle getHitbox() {

        return new Rectangle(x + 80, y, width / 8, height /3);
    }
}