package main;

import java.awt.*;
import java.util.ArrayList;

public class MainMenu {
    private final int screenWidth, screenHeight;
    public ArrayList<MenuButton> buttons = new ArrayList<>();

    public MainMenu(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        int btnWidth = 200;
        int btnHeight = 50;
        int x = screenWidth / 2 - btnWidth / 2;

        buttons.add(new MenuButton(x, screenHeight / 3, btnWidth, btnHeight, "Play"));
        buttons.add(new MenuButton(x, screenHeight / 3 + 70, btnWidth, btnHeight, "Settings"));
        buttons.add(new MenuButton(20, screenHeight - 70, 100, 40, "Mute"));
        buttons.add(new MenuButton(screenWidth - 120, screenHeight - 70, 100, 40, "Quit"));
    }

    public void draw(Graphics2D g2) {
        for (MenuButton button : buttons) {
            button.draw(g2);
        }
    }
}
