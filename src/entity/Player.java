package entity;

import main.GamePanel;
import main.KeyHandler;

import java.awt.*;

public class Player extends Entity{
    GamePanel gp;
    KeyHandler keyH;
    int jumpspeed;
    // Jumping Variables
    boolean isJumping = false; // to track if player is jumping
    int jumpHeight = 0; // height of the jump
    final int maxJumpHeight = 150; // maximum jump height
    final int groundY = 600; // Y position of the ground


    public Player(GamePanel gp, KeyHandler keyH){
        this.gp = gp;
        this.keyH = keyH;

        setDefaultValues();

    }
    public void setDefaultValues(){
        x = 100;
        y = 100;
        speed = 4;
        jumpspeed = 4;
    }
    public void update(){
        if(keyH.upPressed && !isJumping && y == groundY) { // Start jump only if not jumping and on the ground
            isJumping = true;
            jumpHeight = maxJumpHeight; // Set jump height to max
        }

        // Perform jump action
        if(isJumping) {
            if(jumpHeight > 0) {
                y -= jumpspeed;  // Move player upwards
                jumpHeight -= jumpspeed;
            } else {
                isJumping = false;  // End jump once jump height is reached
            }
        }
        else {
            // Add gravity effect to bring the player down slowly when not jumping
            if(y < groundY) { // Ensure player doesn't fall below ground level
                y += jumpspeed;
            }
        }

        if(keyH.rightPressed) {
            x = x + speed;
        }
    }
    public void draw(Graphics2D g2){
        g2.setColor(Color.white);
        g2.fillRect(x, y, gp.tileSize, gp.tileSize);
    }
}

