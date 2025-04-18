package main;

import entity.Obstacle;
import entity.Player;
import entity.AirO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GamePanel extends JPanel implements Runnable {
    // Game state management
    public enum GameState {
        MAIN_MENU,
        GAMEPLAY,
        GAME_OVER
    }
    private GameState currentState = GameState.MAIN_MENU;

    // Thread management
    private Thread menuListenerThread;
    private Thread gameThread;
    private volatile boolean running = false;

    // UI Components
    private MainMenu mainMenu;
    private MenuButton restartButton;
    private BufferedImage restartIcon;

    // Game elements
    public Player player;
    private Obstacle obstacle, obstacle2;
    private AirO airo, airo2;
    private KeyHandler keyH = new KeyHandler();

    // Game metrics
    private boolean playerDied;
    private long startTime;
    private int score = 0;
    private final MusicPlayer musicPlayer = new MusicPlayer();
    private boolean hasPlayedMenuMusic = false;

    // Game constants
    private static final int ORIGINAL_TILE_SIZE = 16;
    private static final int SCALE = 3;
    public final int TILE_SIZE = ORIGINAL_TILE_SIZE * SCALE;
    private static final int MAX_SCREEN_COL = 40;
    private static final int MAX_SCREEN_ROW = 22;
    public final int SCREEN_WIDTH = TILE_SIZE * MAX_SCREEN_COL;
    public final int SCREEN_HEIGHT = TILE_SIZE * MAX_SCREEN_ROW + 24;
    private static final int FPS = 60;

    public GamePanel() {
        initializeUI();
        initializeGame();
        setupInputHandlers();
        loadResources();
        startMenuSystem();
    }

    private void initializeUI() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
    }

    private void initializeGame() {
        mainMenu = new MainMenu(SCREEN_WIDTH, SCREEN_HEIGHT);
        resetGameObjects();
    }

    private void setupInputHandlers() {
        this.addKeyListener(keyH);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }
        });
    }

    private void loadResources() {
        try {
            restartIcon = ImageIO.read(new File("res/menubuttons/restart.png"));
            restartButton = new MenuButton(
                    SCREEN_WIDTH / 2 - 32,
                    SCREEN_HEIGHT / 2 + 50,
                    64, 64,
                    "restart",
                    restartIcon
            );
        } catch (IOException e) {
            System.err.println("Failed to load restart icon: " + e.getMessage());
            restartButton = new MenuButton(
                    SCREEN_WIDTH / 2 - 100,
                    SCREEN_HEIGHT / 2 + 50,
                    200, 50,
                    "restart",
                    null
            );
            restartButton.text = "Restart";
        }
    }

    private void startMenuSystem() {
        playMenuMusic();
        startMenuListenerThread();
        this.requestFocusInWindow();
    }

    private void handleMouseClick(int x, int y) {
        switch (currentState) {
            case MAIN_MENU:
                handleMainMenuClick(x, y);
                break;
            case GAME_OVER:
                if (restartButton.isClicked(x, y)) {
                    restartGame();
                }
                break;
        }
    }

    private void handleMainMenuClick(int x, int y) {
        for (MenuButton button : mainMenu.buttons) {
            if (button.isClicked(x, y)) {
                handleMenuAction(button.id);
                return;
            }
        }
    }

    private void handleMenuAction(String buttonId) {
        if (buttonId == null) return;

        switch (buttonId) {
            case "play":
                startGame();
                break;
            case "settings":
                SwingUtilities.invokeLater(() -> new SettingsMenu().setVisible(true));
                break;
            case "mute":
                toggleMute();
                mainMenu.toggleMuteState();
                repaint();
                break;
            case "quit":
                System.exit(0);
                break;
            default:
                System.out.println("Unknown button: " + buttonId);
        }
    }

    private void startGame() {
        musicPlayer.stop();
        currentState = GameState.GAMEPLAY;
        startTime = System.nanoTime();
        hasPlayedMenuMusic = false;

        if (!musicPlayer.isMuted()) {
            musicPlayer.playRandomFromFolder("res/music/bgm");
        }

        startGameThread();
    }

    private void toggleMute() {
        boolean wasMuted = musicPlayer.isMuted();
        musicPlayer.setMuted(!wasMuted);

        if (!musicPlayer.isMuted()) {
            if (currentState == GameState.MAIN_MENU) {
                hasPlayedMenuMusic = false;
                playMenuMusic();
            } else if (currentState == GameState.GAMEPLAY) {
                musicPlayer.playRandomFromFolder("res/music/bgm");
            }
        }
    }

    private void playMenuMusic() {
        if (hasPlayedMenuMusic || musicPlayer.isMuted()) {
            return;
        }
        musicPlayer.stop();
        musicPlayer.playRandomFromFolder("res/music/menu");
        hasPlayedMenuMusic = true;
    }

    private void startMenuListenerThread() {
        menuListenerThread = new Thread(() -> {
            while (currentState == GameState.MAIN_MENU || currentState == GameState.GAME_OVER) {
                handleKeyPress();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        });
        menuListenerThread.start();
    }

    public void startGameThread() {
        stopExistingThreads();
        resetGameObjects();
        this.requestFocusInWindow();
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    private void stopExistingThreads() {
        if (menuListenerThread != null && menuListenerThread.isAlive()) {
            menuListenerThread.interrupt();
        }

        if (gameThread != null && gameThread.isAlive()) {
            running = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;
        long lastTime = System.nanoTime();
        long frames = 0;

        while (running) {
            update();
            repaint();
            frames++;

            if (System.nanoTime() - lastTime >= 1000000000) {
                System.out.println("FPS: " + frames);
                frames = 0;
                lastTime = System.nanoTime();
            }

            try {
                double remainingTime = (nextDrawTime - System.nanoTime()) / 1000000;
                if (remainingTime < 0) remainingTime = 0;
                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        if (currentState == GameState.GAME_OVER) {
            startMenuListenerThread();
        }
    }

    public void update() {
        if (currentState == GameState.GAMEPLAY) {
            double elapsedTime = (System.nanoTime() - startTime) / 1_000_000_000.0;
            player.update();
            obstacle.update();
            obstacle2.update();
            airo.update();
            airo2.update();
            checkCollisions();
            updateScore(elapsedTime);
        }
    }

    private void checkCollisions() {
        if (!Settings.collisionEnabled) return;

        Rectangle playerHitbox = player.getHitbox();
        if (playerHitbox.intersects(obstacle.getHitbox()) ||
                playerHitbox.intersects(obstacle2.getHitbox()) ||
                playerHitbox.intersects(airo.getHitbox()) ||
                playerHitbox.intersects(airo2.getHitbox())) {
            playerDied = true;
            currentState = GameState.GAME_OVER;
            running = false;
        }
    }

    private void updateScore(double elapsedTime) {
        score = (int) ((elapsedTime / 10) * Obstacle.speed);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        switch (currentState) {
            case MAIN_MENU:
                renderMainMenu(g2);
                break;
            case GAMEPLAY:
                renderGameplay(g2);
                break;
            case GAME_OVER:
                renderGameOver(g2);
                break;
        }
        g2.dispose();
    }

    private void renderMainMenu(Graphics2D g2) {
        mainMenu.draw(g2);
    }

    private void renderGameplay(Graphics2D g2) {
        g2.setColor(Color.white);
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.drawString("Score: " + score, 20, 30);

        player.draw(g2);
        obstacle.draw(g2);
        obstacle2.draw(g2);
        airo.draw(g2);
        airo2.draw(g2);

        if (Settings.showHitboxes) {
            drawHitboxes(g2);
        }
    }

    private void drawHitboxes(Graphics2D g2) {
        g2.setColor(Color.RED);
        g2.draw(player.getHitbox());
        g2.draw(obstacle.getHitbox());
        g2.draw(obstacle2.getHitbox());
        g2.draw(airo.getHitbox());
        g2.draw(airo2.getHitbox());
    }

    private void renderGameOver(Graphics2D g2) {
        g2.setColor(Color.white);
        g2.setFont(new Font("Arial", Font.BOLD, 50));
        g2.drawString("Game Over", SCREEN_WIDTH / 2 - 150, SCREEN_HEIGHT / 2 - 100);
        g2.setFont(new Font("Arial", Font.PLAIN, 30));
        g2.drawString("Final Score: " + score, SCREEN_WIDTH / 2 - 100, SCREEN_HEIGHT / 2);

        restartButton.bounds.x = SCREEN_WIDTH / 2 - 32;
        restartButton.bounds.y = SCREEN_HEIGHT / 2 + 50;
        restartButton.draw(g2);
    }

    public void handleKeyPress() {
        if (keyH.enterPressed) {
            keyH.enterPressed = false;
            if (currentState == GameState.MAIN_MENU) {
                startGame();
            }
        }
    }

    public void restartGame() {
        // Reset game objects and state
        resetGameObjects();
        currentState = GameState.GAMEPLAY;  // Changed from MAIN_MENU to GAMEPLAY
        score = 0;
        playerDied = false;

        // Stop any existing music and threads
        musicPlayer.stop();
        if (menuListenerThread != null && menuListenerThread.isAlive()) {
            menuListenerThread.interrupt();
        }

        // Start the game fresh
        startTime = System.nanoTime();

        // Play game music if not muted
        if (!musicPlayer.isMuted()) {
            musicPlayer.playRandomFromFolder("res/music/bgm");
        }

        startGameThread();

        // Ensure focus is on the panel
        this.requestFocusInWindow();
    }

    private void resetGameObjects() {
        player = new Player(this, keyH);
        obstacle = new Obstacle(this);
        obstacle2 = new Obstacle(this);
        airo = new AirO(this);
        airo2 = new AirO(this);
        playerDied = false;
        score = 0;
    }
}