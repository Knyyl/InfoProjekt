package entity;

import main.GamePanel;
import main.KeyHandler;
import java.awt.*;

public class Player extends Entity {
    public static double speed;
    private final GamePanel gp;  // Made private
    public final KeyHandler keyH;  // Made private

    public int width;
    public int height;

    // Jumping and physics variables
    private double velocityY = 0;
    private final double gravity = 1.5;
    private final double jumpForce = -20;
    private final int groundY = 600;
    private boolean onGround = true;

    // Main constructor
    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        this.width = gp.TILE_SIZE;
        this.height = gp.TILE_SIZE;
        setDefaultValues();
    }

    public void setDefaultValues() {
        x = 100;
        y = groundY;
        speed = 4;
    }

    public void update() {
        if (keyH == null) return;  // Safety check

        // Apply gravity
        velocityY += gravity;

        // Jump input (trigger jump only once)
        if (isJumpPressed() && onGround) {
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
    }

    // New method to encapsulate key check
    private boolean isJumpPressed() {
        return keyH.upPressed;
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.white);
        g2.fillRect(x, y, width, height);
    }

    public Rectangle getHitbox() {
        return new Rectangle(x, y, width, height);
    }
}