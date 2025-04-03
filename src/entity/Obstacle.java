package entity;

import main.GamePanel;

import java.awt.*;

public class Obstacle extends Entity {
    public Obstacle(GamePanel gp){
        this.gp = gp;
        setDefaultValues();


    }
    GamePanel gp;

    public void setDefaultValues(){
        x = 1920;
        y = 600;
        speed = 1;
    }
    public void update(){
        speed =  speed * 1.0001;
        x = (int) (x - speed);
        if(x <= 0){
            x = 1920;
        }
    }
    public void draw(Graphics2D g2){
        g2.setColor(Color.white);
        g2.fillRect(x, y, gp.tileSize, gp.tileSize);
    }
}
