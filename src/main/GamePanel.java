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

    // UI and input
    private UIManager uiManager;
    private final KeyHandler keyH = new KeyHandler();

    // Core game logic
    private GamePlayManager gpm;

    // Music handling
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

    // Constructor
    public GamePanel() {
        initializeUI();           // Set panel size, background etc.
        initializeGame();         // Initialize game logic and state
        setupInputHandlers();     // Register key and mouse listeners
        startMenuSystem();        // Start main menu and music
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
        resetGameObjects(); // Create initial game state
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
                if (uiManager.restartClicked(x, y)) restartGame();
                if (uiManager.homeButtonclicked(x, y)) returnToMainMenu();
                break;
        }
    }

    // Handles main menu button clicks
    private void handleMenuAction(String buttonId) {
        if (buttonId == null) return;

        switch (buttonId) {
            case "play": startGame(); break;
            case "settings":
                SwingUtilities.invokeLater(() -> {
                    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                    SettingsMenu settingsMenu = new SettingsMenu(parentFrame);
                    settingsMenu.setVisible(true);
                });
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

    // Starts the actual gameplay
    private void startGame() {
        musicPlayer.stop();
        gsm.setState(GameStateManager.GameState.GAMEPLAY);
        hasPlayedMenuMusic = false;
        Settings.levelchecker();
        if (!musicPlayer.isMuted()) {
            musicPlayer.playRandomFromFolder("res/music/bgm");
        }
        startGameThread();
    }

    // Mutes/unmutes the music
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

    // Plays main menu music if not already playing
    private void playMenuMusic() {
        if (hasPlayedMenuMusic || musicPlayer.isMuted()) return;
        musicPlayer.stop();
        musicPlayer.playRandomFromFolder("res/music/menu");
        hasPlayedMenuMusic = true;
    }

    // Starts a thread to listen for menu actions
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

    // Starts the main game loop in a separate thread
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

    // Stops running threads before switching state
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

    // Switches to the main menu state
    public void returnToMainMenu() {
        stopExistingThreads();
        gsm.setState(GameStateManager.GameState.MAIN_MENU);
        this.requestFocusInWindow();
        running = true;
        startMenuListenerThread();
        repaint();
    }

    // Main game loop
    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;
        long lastTime = System.nanoTime();
        long frames = 0;

        while (running) {
            gpm.update();   // Game logic
            repaint();      // Redraw screen
            frames++;

            // FPS output every second
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

        // Return to menu listener if game ends
        if (gsm.is(GameStateManager.GameState.GAME_OVER)) {
            startMenuListenerThread();
        }
    }

    // Rendering based on game state
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        switch (gsm.getState()) {
            case MAIN_MENU:
                Settings.levelchecker();
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

    // Start game on ENTER key in menu
    public void handleKeyPress() {
        if (keyH.enterPressed) {
            keyH.enterPressed = false;
            if (gsm.is(GameStateManager.GameState.MAIN_MENU)) {
                startGame();
            }
        }
    }

    // Fully resets and restarts the game
    public void restartGame() {
        resetGameObjects();
        gsm.setState(GameStateManager.GameState.GAMEPLAY);
        musicPlayer.stop();

        if (menuListenerThread != null && menuListenerThread.isAlive()) {
            menuListenerThread.interrupt();
        }

        if (!musicPlayer.isMuted()) {
            musicPlayer.playRandomFromFolder("res/music/bgm");
        }

        startGameThread();
        this.requestFocusInWindow();
    }

    // Creates a new GamePlayManager with fresh state
    private void resetGameObjects() {
        gpm = new GamePlayManager(this, keyH, gsm);
    }
}
