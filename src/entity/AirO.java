package entity;
import main.GamePanel;
import java.util.Random;
import java.awt.*;
public class AirO extends Entity {
    Random rand = new Random();
    public AirO(GamePanel gp){
        this.gp = gp;
        setDefaultValues();


    }
    GamePanel gp;

    public void setDefaultValues(){
        x = rand.nextInt(1930) + 20000;
        y = 500;
        speed = 0.6;
    }
    public void update(){
        speed =speed * 1.0001;
        x = (int) (x - speed);
        if(x <= 0){
            x = rand.nextInt(1970) + 20000;
        }
    }
    public void draw(Graphics2D g2){
        g2.setColor(Color.white);
        g2.fillRect(x, y, gp.tileSize, gp.tileSize);
    }
}

