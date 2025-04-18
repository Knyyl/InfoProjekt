// SettingsMenu.java
package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SettingsMenu extends JFrame {
    public SettingsMenu() {
        setTitle("Settings");
        setSize(300, 150);
        setLayout(new FlowLayout());
        setLocationRelativeTo(null);

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

    }
}
