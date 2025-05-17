package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class SettingsMenu extends JFrame {
    private int coins;

    public SettingsMenu() {
        setTitle("Settings");
        setSize(350, 200);
        setLayout(new FlowLayout());
        setLocationRelativeTo(null);

        coins = readCoins();

        JCheckBox collisionCheckbox = new JCheckBox("Enable Collision");
        collisionCheckbox.setSelected(Settings.collisionEnabled);
        collisionCheckbox.addActionListener(e -> {
            Settings.collisionEnabled = collisionCheckbox.isSelected();
            System.out.println("Collision enabled: " + Settings.collisionEnabled);
        });
        add(collisionCheckbox);

        JCheckBox hitboxCheckbox = new JCheckBox("Show Hitboxes");
        hitboxCheckbox.setSelected(Settings.showHitboxes);
        hitboxCheckbox.addActionListener(e -> {
            Settings.showHitboxes = hitboxCheckbox.isSelected();
            System.out.println("Show hitboxes: " + Settings.showHitboxes);
        });
        add(hitboxCheckbox);

        JCheckBox level1Checkbox = new JCheckBox(" level 1");
        JCheckBox level2Checkbox = new JCheckBox(" level 2");

        level1Checkbox.setSelected(Settings.level1);
        level2Checkbox.setSelected(Settings.level2);
        level2Checkbox.setEnabled(Settings.level2); // Only enable if already purchased

        JButton buyLevel2Button = new JButton("Buy level 2 for 100 coins");
        buyLevel2Button.setEnabled(coins >= 100 && !Settings.level2);

        buyLevel2Button.addActionListener(e -> {
            if (coins >= 100) {
                coins -= 100;
                saveCoins(coins);

                Settings.level2 = true;
                level2Checkbox.setEnabled(true);
                level2Checkbox.setSelected(true);
                level1Checkbox.setSelected(false);
                Settings.level1 = false;

                buyLevel2Button.setEnabled(false);
                buyLevel2Button.setVisible(false);

                System.out.println("Level 2 purchased. Coins left: " + coins);
            }
        });

        level1Checkbox.addActionListener(e -> {
            if (level1Checkbox.isSelected()) {
                level2Checkbox.setSelected(false);
            }
            Settings.level1 = level1Checkbox.isSelected();
            Settings.level2 = level2Checkbox.isSelected();
            System.out.println("level1: " + Settings.level1);
        });

        level2Checkbox.addActionListener(e -> {
            if (level2Checkbox.isSelected()) {
                level1Checkbox.setSelected(false);
            }
            Settings.level1 = level1Checkbox.isSelected();
            Settings.level2 = level2Checkbox.isSelected();
            System.out.println("level2: " + Settings.level2);
        });

        add(level1Checkbox);
        add(level2Checkbox);
        add(buyLevel2Button);
    }

    private int readCoins() {
        try (BufferedReader reader = new BufferedReader(new FileReader("wallet.txt"))) {
            String line = reader.readLine();
            return Integer.parseInt(line.trim());
        } catch (Exception e) {
            System.err.println("Could not read wallet.txt, defaulting to 0 coins.");
            return 0;
        }
    }

    private void saveCoins(int newCoins) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("wallet.txt"))) {
            writer.write(String.valueOf(newCoins));
        } catch (IOException e) {
            System.err.println("Could not write to wallet.txt");
        }
    }
}
