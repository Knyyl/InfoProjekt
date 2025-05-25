package main;
import entity.Coin;
import entity.Obstacle;
import entity.Player;
import entity.AirO;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Random;


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
    private Coin coin;

    //constructor, initializes gameplay
    public GamePlayManager(GamePanel gp, KeyHandler keyH, GameStateManager gsm) {
        this.gp = gp;
        this.keyH = keyH;
        this.gsm = gsm;
        resetGame();
    }
    // initializes and resets all needed objects
    private void resetGame() {
        wallpaper = new Movedaback(gp,this);
        player = new Player(gp, keyH);
        obstacle = new Obstacle(gp, this);
        obstacle2 = new Obstacle(gp, this);
        airo = new AirO(gp);
        airo2 = new AirO(gp);
        coin = new Coin(gp, this);
        playerDied = false;
        score = 0;
        startTime = System.nanoTime();
    }
    //updates game logic every frame
    public void update() {

        double elapsedTime = (System.nanoTime() - startTime) / 1_000_000_000.0;
        wallpaper.update();
        player.update();
        obstacle.update();
        obstacle2.update();
        ensureObstacleSpacing();
        airo.update();
        airo2.update();
        coin.update();
        checkCollisions();
        updateScore(elapsedTime);

    }
    //draws alle game elements
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
        coin.draw(g2);

        if (Settings.showHitboxes) {
            g2.setColor(Color.RED);
            g2.draw(player.getHitbox());
            g2.draw(obstacle.getHitbox());
            g2.draw(obstacle2.getHitbox());
            g2.draw(airo.getHitbox());
            g2.draw(airo2.getHitbox());
            g2.draw(coin.getHitbox());
        }
    }
    //checks and updates highscore
    public void updateHighscore(){
        int currentscore = getScore();
        saveHighScore(currentscore);
    }
    //returns highscore
    public static int getHighscore() {
        File highscore = new File("highscore.txt");
        if (!highscore.exists()) return 0;

        try (BufferedReader br = new BufferedReader(new FileReader(highscore))) {
            return Integer.parseInt(br.readLine().trim());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    //writes highscore into the file
    public static void saveHighScore(int score) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("highscore.txt"))) {
            bw.write(String.valueOf(score));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //checks collisions between obstacles and
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
            if(getHighscore() < score){
                updateHighscore();
            }

        }
    }
    // check if the obstacles are too close and higher the distance
    private void ensureObstacleSpacing() {
        int minDistance = 300;

        if (Math.abs(obstacle.x - obstacle2.x) < minDistance) {

            if (obstacle.x < obstacle2.x) {
                obstacle.x = obstacle2.x + minDistance + new Random().nextInt(500);
            } else {
                obstacle2.x = obstacle.x + minDistance + new Random().nextInt(500);
            }
        }
    }
    //calculates score
    private void updateScore(double elapsedTime) {
        score = (int) ((elapsedTime / 10) * Obstacle.speed);
    }

    public int getScore() {

        return score;
    }

}