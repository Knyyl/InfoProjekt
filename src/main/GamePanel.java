package main;

import entity.Obstacle;
import entity.Player;
import entity.AirO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GamePanel extends JPanel implements Runnable {
    Thread menuListenerThread;
    MainMenu mainMenu;
    MenuButton restartButton; // Added for game over screen

    public enum GameState {
        MAIN_MENU,
        GAMEPLAY,
        GAME_OVER
    }

    private MusicPlayer musicPlayer = new MusicPlayer();
    private boolean hasPlayedMenuMusic = false;
    private boolean playerdied;
    private long startTime;
    private int score = 0;

    private GameState currentState = GameState.MAIN_MENU;

    final int originalTilesSize = 16;
    final int scale = 3;
    public final int tileSize = originalTilesSize * scale;
    final int maxScreenCol = 40;
    final int maxScreenRow = 22;
    final int screenWidth = tileSize * maxScreenCol;
    final int screenHeight = tileSize * maxScreenRow + 24;

    int FPS = 500;

    KeyHandler keyH = new KeyHandler();
    Thread gameThread;
    private volatile boolean running = false;

    Player player;
    Obstacle obstacle;
    Obstacle obstacle2;
    AirO airo;
    AirO airo2;

    public GamePanel() {
        mainMenu = new MainMenu(screenWidth, screenHeight);
        restartButton = new MenuButton(screenWidth/2 - 100, screenHeight/2 + 50, 200, 50, "Restart");

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (currentState == GameState.MAIN_MENU) {
                    for (MenuButton button : mainMenu.buttons) {
                        if (button.isClicked(e.getX(), e.getY())) {
                            handleMenuClick(button.text);
                        }
                    }
                }
                else if (currentState == GameState.GAME_OVER && restartButton.isClicked(e.getX(), e.getY())) {
                    restartGame();
                }
            }
        });

        resetGame();
        playMenuMusic();
        startMenuListenerThread();
    }

    private void handleMenuClick(String buttonText) {
        switch (buttonText) {
            case "Play" -> {
                musicPlayer.stop();
                currentState = GameState.GAMEPLAY;
                startTime = System.nanoTime();
                hasPlayedMenuMusic = false;
                startGameThread();
            }
            case "Settings" -> System.out.println("Settings clicked!");
            case "Mute" -> {
                boolean currentlyMuted = musicPlayer.isMuted();
                musicPlayer.setMuted(!currentlyMuted);
                System.out.println("Mute toggled. Muted: " + !currentlyMuted);
            }
            case "Quit" -> System.exit(0);
        }
    }

    private void startMenuListenerThread() {
        menuListenerThread = new Thread(() -> {
            while (currentState == GameState.MAIN_MENU || currentState == GameState.GAME_OVER) {
                handleKeyPress();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        menuListenerThread.start();
    }

    public void startGameThread() {
        System.out.println("Starting game thread");

        if (menuListenerThread != null && menuListenerThread.isAlive()) {
            menuListenerThread.interrupt();
        }

        if (running && gameThread != null && gameThread.isAlive()) {
            running = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        musicPlayer.stop();
        musicPlayer.playRandomFromFolder("res/music/bgm");

        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;
        long lastTime = System.nanoTime();
        long frames = 0;

        while (running) {
            long currentTime = System.nanoTime();

            handleKeyPress();
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
                remainingTime = remainingTime / 1000000;
                if (remainingTime < 0) remainingTime = 0;
                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Game thread stopped");
        gameThread = null;
    }

    public void update() {
        if (currentState == GameState.GAMEPLAY) {
            long currentTime = System.nanoTime();
            double elapsedTimeInSeconds = (currentTime - startTime) / 1_000_000_000.0;

            player.update();
            obstacle.update();
            obstacle2.update();
            airo.update();
            airo2.update();

            Rectangle playerHitbox = player.getHitbox();
            Rectangle obstacleHitbox1 = obstacle.getHitbox();
            Rectangle obstacleHitbox2 = obstacle2.getHitbox();
            Rectangle airOHitbox1 = airo.getHitbox();
            Rectangle airOHitbox2 = airo2.getHitbox();

            if (playerHitbox.intersects(obstacleHitbox1) ||
                    playerHitbox.intersects(obstacleHitbox2) ||
                    playerHitbox.intersects(airOHitbox1) ||
                    playerHitbox.intersects(airOHitbox2)) {

                playerdied = true;
                currentState = GameState.GAME_OVER;
                running = false;
                startMenuListenerThread();
            } else {
                score = (int) (elapsedTimeInSeconds * Obstacle.speed);
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        switch (currentState) {
            case MAIN_MENU -> mainMenu.draw(g2);
            case GAMEPLAY -> drawGameplay(g2);
            case GAME_OVER -> drawGameOver(g2);
        }
        g2.dispose();
    }

    private void drawGameplay(Graphics2D g2) {
        g2.setColor(Color.white);
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.drawString("Score: " + score, 20, 30);

        if (playerdied) {
            g2.setFont(new Font("Arial", Font.BOLD, 50));
            g2.drawString("You died with a score of: " + score, 650, 500);
        }

        player.draw(g2);
        obstacle.draw(g2);
        obstacle2.draw(g2);
        airo.draw(g2);
        airo2.draw(g2);
    }

    private void drawGameOver(Graphics2D g2) {
        g2.setColor(Color.white);
        g2.setFont(new Font("Arial", Font.BOLD, 50));
        g2.drawString("Game Over", screenWidth/2 - 150, screenHeight/2 - 100);
        g2.setFont(new Font("Arial", Font.PLAIN, 30));
        g2.drawString("Final Score: " + score, screenWidth/2 - 100, screenHeight/2);
        restartButton.draw(g2);
    }

    private void playMenuMusic() {
        if (!hasPlayedMenuMusic) {
            musicPlayer.playRandomFromFolder("res/music/menu");
            hasPlayedMenuMusic = true;
        }
    }

    public void handleKeyPress() {
        if (keyH.enterPressed) {
            keyH.enterPressed = false;
            System.out.println("Enter pressed in state: " + currentState);

            if (currentState == GameState.MAIN_MENU) {
                musicPlayer.stop();
                currentState = GameState.GAMEPLAY;
                startTime = System.nanoTime();
                hasPlayedMenuMusic = false;
                startGameThread();
            }
            else if (currentState == GameState.GAME_OVER) {
                restartGame();
            }
        }
    }

    public void restartGame() {
        // Stop current game thread
        running = false;

        if (gameThread != null) {
            try {
                gameThread.join(); // Wait for thread to finish
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Reset game state
        resetGame();


        // Start new game
        currentState = GameState.GAMEPLAY;
        startTime = System.nanoTime();
        startGameThread(); // This should properly restart the game loop
    }

    private void resetGame() {
        playerdied = false;
        score = 0;
        player = new Player(this, keyH);
        obstacle = new Obstacle(this);
        obstacle2 = new Obstacle(this);
        airo = new AirO(this);
        airo2 = new AirO(this);
    }
}