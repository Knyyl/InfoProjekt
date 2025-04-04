package entity;

import main.GamePanel;
import java.util.Random;
import java.awt.*;

public class Obstacle extends Entity {
    Random rand = new Random();
    public Obstacle(GamePanel gp){
        this.gp = gp;
        setDefaultValues();


    }
    GamePanel gp;

    public void setDefaultValues(){
        //int x = rand.nextInt(1922) + 6;
        x = rand.nextInt(1930) + 2000;
        y = 600;
        speed = 0.5;
    }
    public void update(){
        speed =  speed * 1.0001;
        x = (int) (x - speed);
        if(x <= 0){
            x = rand.nextInt(1920) + 2000;
        }
    }
    public void draw(Graphics2D g2){
        g2.setColor(Color.white);
        g2.fillRect(x, y, gp.tileSize, gp.tileSize);
    }
}
