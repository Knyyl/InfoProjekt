package main;

import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("NAOI");

        try {
            Image icon = ImageIO.read(new File("res/icon/ACABI.png"));
            window.setIconImage(icon);
        } catch (IOException e) {
            System.out.println("Could not load icon. ");
        }

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);

        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);

        //gamePanel.startGameThread();
    }
}
