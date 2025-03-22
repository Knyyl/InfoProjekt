package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    public boolean upPressed, downPressed, leftPressed, rightPressed;
    @Override
    public void keyTyped(KeyEvent e) {
        //Not gonna use
    }
    // KeyPressed and KeyReleased manages keyboard input, which will be used for character movement.
    //Need to release keys so that code will stop moving character once keyboard not pressed anymore
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        //Checks if key has been pressed, Vk_KEYHERE
        if(code == KeyEvent.VK_W){

            //CREATE REACTION TABLE!!!
            upPressed = true;
        }

        if(code == KeyEvent.VK_D){
            rightPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if(code == KeyEvent.VK_W){
            upPressed = false;
        }

        if(code == KeyEvent.VK_D){
            rightPressed = false;
        }


    }
}
