package main;

import entity.Player;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {
    // SCREEN SETTINGS
    final int originalTilesSize = 16; //16x16 Tile -> standard size pixel art
    final int scale = 3; //Multiply scale so not too small
    public final int tileSize = originalTilesSize * scale; //does the above -> Displayed tile size 48x48
    final int maxScreenCol = 40; //modify screen size here x
    final int maxScreenRow = 22; // same as above y
    final int screenWidth = tileSize * maxScreenCol;  // 1980 pixels
    final int screenHeight = tileSize * maxScreenRow + 24; // 1056  + 24, cus fullhd by multiplication with 16 not possilbe

    //FPS
    int FPS = 300;

    KeyHandler keyH = new KeyHandler();
    Thread gameThread; //game needs time
    Player player = new Player(this,keyH);

    //player default position
    int playerX = 100;
    int playerY = 600;
    //player walk speed -> is map movement speed, as player will stay still for this game and only map will move
    int playerSpeed = 4;
    int playerJumpSpeed = 2;

    // Jumping Variables
    boolean isJumping = false; // to track if player is jumping
    int jumpHeight = 0; // height of the jump
    final int maxJumpHeight = 150; // maximum jump height
    final int groundY = 600; // Y position of the ground

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
    }

    public void startGameThread(){
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000/FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        //FPS counter
        long lastTime = System.nanoTime();
        long frames = 0;

        while (gameThread != null){
            long currentTime = System.nanoTime();

            //runs as fast as game (fps)
            //update information: character pos
            update();
            //draw screen with updated information (example move character)
            repaint();

            //count frames
            frames++;

            if(currentTime - lastTime >= 1000000000){
                System.out.println("FPS: " + frames);
                //Reset counters
                frames = 0;
                lastTime = currentTime;
            }

            //Pauses game loop
            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime/100000; //Thread.sleep only accepts milliseconds, but we´re currently counting in nanoseconds.

                if(remainingTime < 0){
                    remainingTime = 0;
                }
                Thread.sleep((long) remainingTime);

                nextDrawTime += drawInterval;

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public void update() {
        player.update();
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;
        //Drawing character placeholder
        player.draw(g2);

        g2.dispose();
    }
}
