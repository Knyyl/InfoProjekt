package main;

import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Main {

    //Entry point that creates undecorated fullscreen window with game panel.
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame window = new JFrame();
            window.setUndecorated(true); // Remove window borders and title bar
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setResizable(false);
            window.setTitle("NAOI");

            try {
                Image icon = ImageIO.read(new File("res/icon/ACABI.png"));
                window.setIconImage(icon);
            } catch (IOException e) {
                System.out.println("Could not load icon.");
            }

            GamePanel gamePanel = new GamePanel();
            window.add(gamePanel);
            window.pack();

            // Set fullscreen using GraphicsDevice
            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            if (gd.isFullScreenSupported()) {
                gd.setFullScreenWindow(window);
            } else {
                // fallback: manually resize to screen dimensions
                window.setExtendedState(JFrame.MAXIMIZED_BOTH);
                window.setVisible(true);
            }

            // Ensure focus is on the game panel
            gamePanel.requestFocusInWindow();
        });
    }
}
