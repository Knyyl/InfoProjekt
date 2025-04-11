package main;

import entity.Obstacle;
import entity.Player;
import entity.AirO;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {
    private boolean playerdied;
    private long startTime;
    private int score = 0;
    private int finalscore;
    // SCREEN SETTINGS
    final int originalTilesSize = 16; // 16x16 Tile -> standard size pixel art
    final int scale = 3;
    public final int tileSize = originalTilesSize * scale; // 48x48 tile
    final int maxScreenCol = 40;
    final int maxScreenRow = 22;
    final int screenWidth = tileSize * maxScreenCol; // 1920 pixels
    final int screenHeight = tileSize * maxScreenRow + 24; // 1056 + 24

    // FPS
    int FPS = 500;

    KeyHandler keyH = new KeyHandler();
    Thread gameThread;

    Player player = new Player(this, keyH);
    Obstacle obstacle = new Obstacle(this);
    Obstacle obstacle2 = new Obstacle(this);
    AirO airo = new AirO(this);
    AirO airo2 = new AirO(this);

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        startTime = System.nanoTime();
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        long lastTime = System.nanoTime();
        long frames = 0;

        while (gameThread != null) {
            long currentTime = System.nanoTime();

            // Game logic and rendering
            update();
            repaint();

            frames++;

            if (currentTime - lastTime >= 1000000000) {
                System.out.println("FPS: " + frames);
                frames = 0;
                lastTime = currentTime;
            }

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime / 1000000; // to milliseconds

                if (remainingTime < 0) {
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
        //score and time calcs
        long currentTime = System.nanoTime();
        long elapsedTime = currentTime - startTime;  // Time in nanoseconds
        double elapsedTimeInSeconds = elapsedTime / 1_000_000_000.0;

        // Update all game objects
        player.update();
        obstacle.update();
        obstacle2.update();
        airo.update();
        airo2.update();

        // Get hitboxes
        Rectangle playerHitbox = player.getHitbox();
        Rectangle obstacleHitbox1 = obstacle.getHitbox();
        Rectangle obstacleHitbox2 = obstacle2.getHitbox();
        Rectangle airOHitbox1 = airo.getHitbox();
        Rectangle airOHitbox2 = airo2.getHitbox();

        // Collision detection
        if (playerHitbox.intersects(obstacleHitbox1) ||
                playerHitbox.intersects(obstacleHitbox2) ||
                playerHitbox.intersects(airOHitbox1) ||
                playerHitbox.intersects(airOHitbox2)) {

            playerdied = true;
            Player.speed=0;
            Obstacle.speed=0;
            AirO.speed=0;
            player.jumpspeed=0;
        }
        else{
            score = (int) (elapsedTimeInSeconds  * Obstacle.speed);
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        //draw score
        g2.setColor(Color.white);
        g2.setFont(new Font("Arial", Font.BOLD, 24));  // font
        g2.drawString("Score: " + score, 20, 30);  // Draw the score at position (20, 30)
        if(playerdied == true){
            g2.setFont(new Font("Arial", Font.BOLD, 50));
            g2.drawString("You died with a score of: " + score, 650, 500);
        }
        // Draw all entities
        player.draw(g2);
        obstacle.draw(g2);
        obstacle2.draw(g2);
        airo.draw(g2);
        airo2.draw(g2);

        g2.dispose();
    }
}
