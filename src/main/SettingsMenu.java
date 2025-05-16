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


        //level checkboxes
        JCheckBox level1Checkbox = new JCheckBox(" level 1");
        level1Checkbox.setSelected(Settings.level1);
        JCheckBox level2Checkbox = new JCheckBox(" level 2");
        level2Checkbox.setSelected(Settings.level2);
        JCheckBox level3Checkbox = new JCheckBox(" level 3");
        level3Checkbox.setSelected(Settings.level3);



        level1Checkbox.addActionListener(e -> {
            if (level1Checkbox.isSelected()) {
                level2Checkbox.setSelected(false);
                level3Checkbox.setSelected(false);
            }
            Settings.level1 = level1Checkbox.isSelected();
            Settings.level2 = level2Checkbox.isSelected();
            Settings.level3 = level3Checkbox.isSelected();
            System.out.println("level1: " + Settings.level1);
        });


        level2Checkbox.addActionListener(e -> {
            if (level2Checkbox.isSelected()) {
                level1Checkbox.setSelected(false);
                level3Checkbox.setSelected(false);
            }
            Settings.level1 = level1Checkbox.isSelected();
            Settings.level2 = level2Checkbox.isSelected();
            Settings.level3 = level3Checkbox.isSelected();
            System.out.println("level2: " + Settings.level2);
        });



        level3Checkbox.addActionListener(e -> {
            if (level3Checkbox.isSelected()) {
                level2Checkbox.setSelected(false);
                level1Checkbox.setSelected(false);
            }
            Settings.level1 = level1Checkbox.isSelected();
            Settings.level2 = level2Checkbox.isSelected();
            Settings.level3 = level3Checkbox.isSelected();
            System.out.println("level3: " + Settings.level3);
        });
        add(level1Checkbox);
        add(level2Checkbox);
        add(level3Checkbox);







    }

}