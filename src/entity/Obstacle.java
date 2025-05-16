package entity;

import main.GamePanel;
import main.GamePlayManager;
import main.Settings;

import java.util.Random;
import java.awt.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Obstacle extends Entity {
    Settings settings;
    BufferedImage[] frames;
    int frameIndex = 0;
    int animationCounter = 0;
    int animationSpeed = 20; // Lower = faster animation

    public static double speed;
    GamePanel gp;
    GamePlayManager gpm;
    Random rand = new Random();

    public int width;
    public int height;

    public Obstacle(GamePanel gp, GamePlayManager gpm) {
        this.gp = gp;
        this.gpm = gpm;
        setDefaultValues();

        //Sprite loading & pre‑scaling
        try {
            BufferedImage sheet = null;
            if(settings.level == 1){
                sheet = ImageIO.read(new File("res/sprites/ACAB.png"));
            }
            else if(settings.level == 2){
                sheet = ImageIO.read(new File("res/sprites/lambo.png"));
            }

            int origW = sheet.getWidth()  / 4;   // original frame width
            int origH = sheet.getHeight();      // original frame height
            int scaleFactor = 2;                // Bigger = bigger sprite

            // update your obstacle dimensions
            width  = origW * scaleFactor;
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
                g.drawImage(orig, 0, 0, width, height, null);
                g.dispose();

                frames[i] = scaled;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDefaultValues() {
        x = rand.nextInt(1930) + 2000;
        y = gpm.player.y - 50;

        speed = 6;
    }

    public void update() {
        speed = speed * 1.0001;
        speed = Math.min(speed, 25.0); //Stops speed at certain speed, to keep game playable
        x = (int) (x - speed);

        if (x <= -100) {
            x = rand.nextInt(1920) + 2000;
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
        // 80% of full width and height
        int scaledWidth = (int)(width * 0.8);
        int scaledHeight = (int)(height * 0.8 * 0.5); // 80% * 50% = 40% of original height

        // Center the hitbox horizontally and place it near the bottom vertically
        int hitboxX = x + (width - scaledWidth) / 2;
        int hitboxY = y + (height - scaledHeight); // Bottom-aligned

        return new Rectangle(hitboxX, hitboxY, scaledWidth, scaledHeight);
    }

}