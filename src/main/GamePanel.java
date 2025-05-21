package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GamePanel extends JPanel implements Runnable {
    // Game state management
    private GameStateManager gsm;

    // Thread management
    private Thread menuListenerThread;
    private Thread gameThread;
    private volatile boolean running = false;

    // UI Components
    private UIManager uiManager;

    // Game elements
    private GamePlayManager gpm;
    private KeyHandler keyH = new KeyHandler();

    // Game metrics

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
        startMenuSystem();
    }

    private void initializeUI() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
    }

    private void initializeGame() {
        uiManager = new UIManager(SCREEN_WIDTH, SCREEN_HEIGHT);
        gsm = new GameStateManager();
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



    private void startMenuSystem() {
        playMenuMusic();
        startMenuListenerThread();
        this.requestFocusInWindow();
    }

    private void handleMouseClick(int x, int y) {
        switch (gsm.getState()) {
            case MAIN_MENU:
                uiManager.handleMainMenuClick(x, y, this::handleMenuAction);
                break;
            case GAME_OVER:
                if (uiManager.restartClicked(x, y )) {
                    restartGame();
                }
                else if (uiManager.backtoMenuclicked(x, y)) {
                        returnToMainMenu();
                }
                break;
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
                uiManager.toggleMuteState();
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
        gsm.setState(GameStateManager.GameState.GAMEPLAY) ;
        hasPlayedMenuMusic = false;
        Settings.levelchecker();

        if (!musicPlayer.isMuted()) {
            musicPlayer.playRandomFromFolder("res/music/bgm");
        }

        startGameThread();
    }

    private void toggleMute() {
        boolean wasMuted = musicPlayer.isMuted();
        musicPlayer.setMuted(!wasMuted);

        if (!musicPlayer.isMuted()) {
            if (gsm.is(GameStateManager.GameState.MAIN_MENU)) {
                hasPlayedMenuMusic = false;
                playMenuMusic();
            } else if (gsm.is(GameStateManager.GameState.GAMEPLAY)) {
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
            while (gsm.is(GameStateManager.GameState.MAIN_MENU) || gsm.is(GameStateManager.GameState.GAME_OVER)) {
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
    public void setRunning(boolean running) {

        this.running = running;
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
            gpm.update();
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

        if (gsm.is(GameStateManager.GameState.GAME_OVER)) {
            startMenuListenerThread();
        }
    }







    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        switch (gsm.getState()) {
            case MAIN_MENU:
                renderMainMenu(g2);
                break;
            case GAMEPLAY:
                gpm.draw(g2);
                break;
            case GAME_OVER:
                renderGameOver(g2);
                break;
        }
        g2.dispose();
    }

    private void renderMainMenu(Graphics2D g2) {

        uiManager.drawMainMenu(g2);
    }



    private void renderGameOver(Graphics2D g2) {
        uiManager.drawGameOver(g2, gpm.getScore());
    }

    public void handleKeyPress() {
        if (keyH.enterPressed) {
            keyH.enterPressed = false;
            if (gsm.is(GameStateManager.GameState.MAIN_MENU)) {
                startGame();
            }
        }
    }

    public void restartGame() {
        // Reset game objects and state
        resetGameObjects();
       gsm.setState(GameStateManager.GameState.GAMEPLAY);  // Changed from MAIN_MENU to GAMEPLAY


        // Stop any existing music and threads
        musicPlayer.stop();
        if (menuListenerThread != null && menuListenerThread.isAlive()) {
            menuListenerThread.interrupt();
        }

        // Start the game fresh


        // Play game music if not muted
        if (!musicPlayer.isMuted()) {
            musicPlayer.playRandomFromFolder("res/music/bgm");
        }

        startGameThread();

        // Ensure focus is on the panel
        this.requestFocusInWindow();
    }
    public void returnToMainMenu() {
        stopExistingThreads(); // Falls GameThread noch aktiv ist
        gsm.setState(GameStateManager.GameState.MAIN_MENU);
        playMenuMusic();
        this.requestFocusInWindow();
        running = true;
        startMenuListenerThread(); //
        repaint();
    }


    private void resetGameObjects() {

        gpm = new GamePlayManager(this, keyH, gsm );
    }
}