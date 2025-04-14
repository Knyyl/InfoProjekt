package entity;

import main.GamePanel;
import main.KeyHandler;

import java.awt.*;

public class Player extends Entity {
    public static double speed;
    GamePanel gp;
    KeyHandler keyH;

    // Dimensions for hitbox
    public int width;
    public int height;

    // Jumping Variables
    boolean isJumping = false;
    int jumpHeight = 0;
    final int maxJumpHeight = 150;
    final int groundY = 600;

    public static int jumpspeed;

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;

        this.width = gp.tileSize;
        this.height = gp.tileSize;

        setDefaultValues();
    }

    public void setDefaultValues() {
        x = 100;
        y = groundY; // Start on the ground
        speed = 4;
        jumpspeed = 1;
    }

    public void update() {
        if (keyH.upPressed && !isJumping && y == groundY) {
            isJumping = true;
            jumpHeight = maxJumpHeight;
        }

        if (isJumping) {
            if (jumpHeight > 0) {
                y -= jumpspeed;
                jumpHeight -= jumpspeed;
            } else {
                isJumping = false;
            }
        } else {
            if (y < groundY) {
                y += jumpspeed;
            }
        }
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.white);
        g2.fillRect(x, y, width, height);

        // OPTIONAL: Draw hitbox outline for debugging
        g2.setColor(Color.red);
        g2.drawRect(x, y, width, height);
    }

    public Rectangle getHitbox() {
        return new Rectangle(x, y, width, height);
    }
}
