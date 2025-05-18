package entity;

import main.GamePanel;
import main.KeyHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Player extends Entity {
    BufferedImage[] frames;
    int frameIndex = 0;
    int animationCounter = 0;
    int animationSpeed = 10; // Lower = faster animation
    public static double speed;
    private final GamePanel gp;
    public final KeyHandler keyH;

    public int width;
    public int height;

    // Jumping and physics variables
    private double velocityY = 0;
    private double gravity = 1.5;
    private final double jumpForce = -30;
    private final int groundY = 540;
    private boolean onGround = true;
    private boolean jumpsprite;
    BufferedImage groundSheet;
    BufferedImage currentSheet;
    BufferedImage jumpSheet;
    // Main constructor
    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        this.width = gp.TILE_SIZE;
        this.height = gp.TILE_SIZE;
        setDefaultValues();
        try {
            jumpSheet = ImageIO.read(new File("res/sprites/Characterjumping.png"));
            groundSheet = ImageIO.read(new File("res/sprites/CharacterOnGround.png"));
            currentSheet = groundSheet;
            updateFrames();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void updateFrames(){
        int origW = currentSheet.getWidth()  / 8;
        int origH = currentSheet.getHeight();
        int scaleFactor = 4;

        // update obstacle dimensions
        width  = origW * scaleFactor;
        height = origH * scaleFactor;
        frames = new BufferedImage[8];
        for (int i = 0; i < 8; i++) {
            // extract the small frame
            BufferedImage orig = currentSheet.getSubimage(i * origW, 0, origW, origH);

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
    }

    public void setDefaultValues() {
        x = 100;
        y = groundY + 60;
        speed = 4;
    }

    public void update() {
        if (keyH == null) return;  // Safety check
        if(keyH.downPressed) {
            gravity += 10;
        }
        else{
            gravity = 1.5;
        }

        // Apply gravity
        velocityY += gravity;

        // Jump input (trigger jump only once)
        if (isJumpPressed() && onGround) {
            gravity = 1.5;
            velocityY = jumpForce;
            onGround = false;

        }

        // Apply velocity
        y += (int) velocityY;

        // Ground collision
        if (y >= groundY) {
            y = groundY;
            velocityY = 0;
            onGround = true;
        }
        animationCounter++;
        if (animationCounter >= animationSpeed) {
            frameIndex = (frameIndex + 1) % frames.length;
            animationCounter = 0;
        }


        if(!onGround) {
            if (!jumpSheet.equals(currentSheet)){
                currentSheet = jumpSheet;
                updateFrames();
            }
        }
        else{
            if (!groundSheet.equals(currentSheet)){
                currentSheet = groundSheet;
                updateFrames();
            }
        }
    }

    private boolean isJumpPressed() {
        return keyH.upPressed;
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
        return new Rectangle(x +35, y+10, width /3, height - (height /3));
    }
}