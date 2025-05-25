package main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class MainMenu {
    GamePlayManager gpm;
    BufferedImage background;
    private final int screenWidth, screenHeight;
    public ArrayList<MenuButton> buttons = new ArrayList<>();

    // Add both mute state icons
    private BufferedImage playIcon, settingsIcon, muteIcon, unmuteIcon, quitIcon;
    private boolean isMuted = false; // Track mute state

    //Initializes menu with background and buttons (play, settings, mute, quit).
    public MainMenu(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        int btnWidth = 64;
        int btnHeight = 64;

        // positions
        int rightSideX = screenWidth - btnWidth - 150;
        int verticalCenter = screenHeight / 3;
        int topY = 40;
        int buttonSpacing = 100;

        try {
            if (Settings.level2) {
                background = ImageIO.read(new File("res/mainmenuwp/lvl2wp.png"));
            }
            else{
                background = ImageIO.read(new File("res/mainmenuwp/BackgroundLevel1.png"));
            }
            // Load images
            playIcon = ImageIO.read(new File("res/menubuttons/play.png"));
            settingsIcon = ImageIO.read(new File("res/menubuttons/settings.png"));
            muteIcon = ImageIO.read(new File("res/menubuttons/notmute.png"));
            unmuteIcon = ImageIO.read(new File("res/menubuttons/mute.png")); //I know its the wrong way, but doesnt matter as it works.
            quitIcon = ImageIO.read(new File("res/menubuttons/quit.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Create buttons
        buttons.add(new MenuButton(rightSideX, verticalCenter, btnWidth, btnHeight, "play", playIcon));
        buttons.add(new MenuButton(rightSideX, verticalCenter + buttonSpacing, btnWidth, btnHeight, "settings", settingsIcon));

        // Mute button starts with appropriate icon
        buttons.add(new MenuButton(20, topY + 900, btnWidth, btnHeight, "mute", isMuted ? unmuteIcon : muteIcon));

        buttons.add(new MenuButton(screenWidth - btnWidth - 20, topY + 900, btnWidth, btnHeight, "quit", quitIcon));
    }

    // Toggle mute state and icon
    public void toggleMuteState() {
        isMuted = !isMuted;
        for (MenuButton button : buttons) {
            if ("mute".equals(button.id)) {
                button.icon = isMuted ? unmuteIcon : muteIcon;
                break;
            }
        }
    }

    //Renders background and all menu buttons.
    public void draw(Graphics2D g2) {
        // Draw background image
        try{
            if (Settings.level2) {
                background = ImageIO.read(new File("res/mainmenuwp/lvl2wp.png"));
            }
            else{
                background = ImageIO.read(new File("res/mainmenuwp/BackgroundLevel1.png"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (background != null) {
            g2.drawImage(background, 0, 0, null);
        }
        int baseFontSize = 20;
        int scaleFactor = 3;
        Font scaledFont = new Font("Arial", Font.PLAIN, (int)(baseFontSize * scaleFactor));
        g2.setFont(scaledFont);
        g2.setColor(Color.WHITE);
        g2.drawString("Highscore: " + GamePlayManager.getHighscore(), 100, 100);

        // Draw buttons with icons
        for (MenuButton button : buttons) {
            button.draw(g2);
        }
    }

}