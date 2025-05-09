package main;
import entity.Obstacle;
import entity.Player;
import entity.AirO;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GamePlayManager {
    private Movedaback wallpaper;
    BufferedImage background;
    public Player player;
    private Obstacle obstacle, obstacle2;
    private AirO airo, airo2;
    private boolean playerDied;
    private long startTime;
    private int score = 0;
    private final GamePanel gp;
    private final KeyHandler keyH;
    private final GameStateManager gsm;


    public GamePlayManager(GamePanel gp, KeyHandler keyH, GameStateManager gsm) {
        //try {
         //   background = ImageIO.read(new File("res/mainmenuwp/WPlvlone.png"));
       // } catch (IOException e) {
        //    throw new RuntimeException(e);
       // }
        this.gp = gp;
        this.keyH = keyH;
        this.gsm = gsm;
        resetGame();
    }

    private void resetGame() {
        wallpaper = new Movedaback(gp,this);
        player = new Player(gp, keyH);
        obstacle = new Obstacle(gp, this);
        obstacle2 = new Obstacle(gp, this);
        airo = new AirO(gp);
        airo2 = new AirO(gp);
        playerDied = false;
        score = 0;
        startTime = System.nanoTime();
    }

    public void update() {

            double elapsedTime = (System.nanoTime() - startTime) / 1_000_000_000.0;
            wallpaper.update();
            player.update();
            obstacle.update();
            obstacle2.update();
            airo.update();
            airo2.update();
            checkCollisions();
            updateScore(elapsedTime);
        }

    public void draw(Graphics2D g2) {
        if (background != null) {
            g2.drawImage(background, 0, 0, null);
        }
        g2.setColor(Color.white);
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.drawString("Score: " + score, 20, 30);

        wallpaper.draw(g2);
        player.draw(g2);
        obstacle.draw(g2);
        obstacle2.draw(g2);
        airo.draw(g2);
        airo2.draw(g2);

        if (Settings.showHitboxes) {
            g2.setColor(Color.RED);
            g2.draw(player.getHitbox());
            g2.draw(obstacle.getHitbox());
            g2.draw(obstacle2.getHitbox());
            g2.draw(airo.getHitbox());
            g2.draw(airo2.getHitbox());
        }
    }



    private void checkCollisions() {
        if(!Settings.collisionEnabled) return;
        Rectangle playerHitbox = player.getHitbox();
        if (playerHitbox.intersects(obstacle.getHitbox()) ||
                playerHitbox.intersects(obstacle2.getHitbox()) ||
                playerHitbox.intersects(airo.getHitbox()) ||
                playerHitbox.intersects(airo2.getHitbox())) {
            playerDied = true;
            gsm.setState(GameStateManager.GameState.GAME_OVER);
            gp.setRunning(false);
        }
    }
    private void updateScore(double elapsedTime) {
        score = (int) ((elapsedTime / 10) * Obstacle.speed);
    }

    public int getScore() {
        return score;
    }

}