package main;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class SettingsMenu extends JDialog {
    private int coins;

    //Creates settings dialog with collision/hitbox toggles and level purchase.
    public SettingsMenu(JFrame parent) {
        super(parent, "Settings", true); // Modal dialog
        setSize(350, 200);
        setLayout(new FlowLayout());
        setLocationRelativeTo(parent);

        coins = readCoins();

        // Checkbox: Enable or disable collision detection
        JCheckBox collisionCheckbox = new JCheckBox("Enable Collision");
        collisionCheckbox.setSelected(Settings.collisionEnabled);
        collisionCheckbox.addActionListener(e -> {
            Settings.collisionEnabled = collisionCheckbox.isSelected();
            System.out.println("Collision enabled: " + Settings.collisionEnabled);
        });
        add(collisionCheckbox);

        // Checkbox: Show or hide hitboxes
        JCheckBox hitboxCheckbox = new JCheckBox("Show Hitboxes");
        hitboxCheckbox.setSelected(Settings.showHitboxes);
        hitboxCheckbox.addActionListener(e -> {
            Settings.showHitboxes = hitboxCheckbox.isSelected();
            System.out.println("Show hitboxes: " + Settings.showHitboxes);
        });
        add(hitboxCheckbox);

        // Checkboxes for selecting level 1 or level 2
        JCheckBox level1Checkbox = new JCheckBox(" level 1");
        JCheckBox level2Checkbox = new JCheckBox(" level 2");

        level1Checkbox.setSelected(Settings.level1);
        level2Checkbox.setSelected(Settings.level2);
        level2Checkbox.setEnabled(Settings.level2); // Enable only if level 2 is purchased

        // Button to purchase level 2
        JButton buyLevel2Button = new JButton("Buy level 2 for 100 coins");
        buyLevel2Button.setEnabled(coins >= 100 && !Settings.level2);

        buyLevel2Button.addActionListener(e -> {
            if (coins >= 100) {
                coins -= 100;
                saveCoins(coins); // Save updated coin count

                // Activate level 2 and update UI accordingly
                Settings.level2 = true;
                level2Checkbox.setEnabled(true);
                level2Checkbox.setSelected(true);
                level1Checkbox.setSelected(false);
                Settings.level1 = false;

                // Disable and hide purchase button after buying
                buyLevel2Button.setEnabled(false);
                buyLevel2Button.setVisible(false);

                System.out.println("Level 2 purchased. Coins left: " + coins);
            }
        });

        // Ensure only one level can be selected at a time
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

        // Add components to the dialog
        add(level1Checkbox);
        add(level2Checkbox);
        add(buyLevel2Button);
    }

    // Reads the number of coins from "wallet.txt"
    private int readCoins() {
        try (BufferedReader reader = new BufferedReader(new FileReader("wallet.txt"))) {
            String line = reader.readLine();
            return Integer.parseInt(line.trim());
        } catch (Exception e) {
            System.err.println("Could not read wallet.txt, defaulting to 0 coins.");
            return 0;
        }
    }

    // Saves the current coin count to "wallet.txt"
    private void saveCoins(int newCoins) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("wallet.txt"))) {
            writer.write(String.valueOf(newCoins));
        } catch (IOException e) {
            System.err.println("Could not write to wallet.txt");
        }
    }
}