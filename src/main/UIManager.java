package main;

import java.awt.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;


public  class UIManager {

    private MainMenu mainMenu;
    private MenuButton restartButton;
    private MenuButton homeButton;
    private BufferedImage restartIcon;
    private BufferedImage homeIcon;
    private int screenWidth, screenHeight;

    public UIManager(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.mainMenu = new MainMenu(screenWidth, screenHeight);
        loadResources();
    }

    private void loadResources() {
        try {
            restartIcon = ImageIO.read(new File("res/menubuttons/restart.png"));
            restartButton = new MenuButton(
                    screenWidth / 2 - 32,
                    screenHeight / 2 + 50,
                    64, 64,
                    "restart",
                    restartIcon
            );
        } catch (IOException e) {
            System.err.println("Failed to load restart icon: " + e.getMessage());
            restartButton = new MenuButton(
                    screenWidth / 2 - 100,
                    screenHeight / 2 + 50,
                    200, 50,
                    "restart",
                    null
            );
            restartButton.text = "Restart";
        }
        try {
            homeIcon = ImageIO.read(new File("res/menubuttons/back.png"));
            homeButton = new MenuButton(
                    screenWidth / 2 + 32,
                    screenHeight / 2 + 50,
                    64, 64,
                    "home",
                   homeIcon
            );
        } catch (IOException e) {
            System.err.println("Failed to load back icon: " + e.getMessage());
            homeButton = new MenuButton(
                    screenWidth / 2 + 100,
                    screenHeight / 2 + 50,
                    200, 50,
                    "restart",
                    null
            );
            homeButton.text = "back to main menu";
        }
    }

    public void drawMainMenu(Graphics2D g2) {

        mainMenu.draw(g2);
    }

    public void drawGameOver(Graphics2D g2, int score) {
        g2.setColor(Color.white);
        g2.setFont(new Font("Arial", Font.BOLD, 50));
        g2.drawString("Game Over", screenWidth / 2 - 150, screenHeight / 2 - 100);
        g2.setFont(new Font("Arial", Font.PLAIN, 30));
        g2.drawString("Final Score: " + score, screenWidth / 2 - 100, screenHeight / 2);
        restartButton.bounds.x = screenWidth / 2 - 32;
        restartButton.bounds.y = screenHeight / 2 + 50;
        restartButton.draw(g2);
        homeButton.bounds.x = screenWidth / 2 + 32;
        homeButton.bounds.y = screenHeight / 2 + 50;
        homeButton.draw(g2);
    }

    public void handleMainMenuClick(int x, int y, Consumer<String> menuActionHandler) {
        for (MenuButton button : mainMenu.buttons) {
            if (button.isClicked(x, y)) {
                menuActionHandler.accept(button.id);
                return;
            }
        }
    }

    public boolean restartClicked(int x, int y) {

        return restartButton.isClicked(x, y);
    }

    public void toggleMuteState() {

        mainMenu.toggleMuteState();
    }

    public boolean homeButtonclicked(int x, int y) {
        return homeButton.isClicked(x, y);
    }
}


