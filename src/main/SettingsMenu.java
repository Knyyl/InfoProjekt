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

        JCheckBox level1Checkbox = new JCheckBox(" level 1");
       level1Checkbox.setSelected(Settings.level1);
        hitboxCheckbox.addActionListener(e -> {
            Settings.level1 = level1Checkbox.isSelected();
            Settings.levelchecker();
            System.out.println("level1: " + Settings.level1);
        });
        add(level1Checkbox);

        JCheckBox level2Checkbox = new JCheckBox(" level 2");
        level1Checkbox.setSelected(Settings.level2);
        hitboxCheckbox.addActionListener(e -> {
            Settings.level2 = level2Checkbox.isSelected();
            Settings.levelchecker();
            System.out.println("level2: " + Settings.level2);
        });
        add(level2Checkbox);

        JCheckBox level3Checkbox = new JCheckBox(" level 3");
        level3Checkbox.setSelected(Settings.level3);
        hitboxCheckbox.addActionListener(e -> {
            Settings.level3 = level3Checkbox.isSelected();
            Settings.levelchecker();
            System.out.println("level3: " + Settings.level3);
        });
        add(level3Checkbox);





    }
}
