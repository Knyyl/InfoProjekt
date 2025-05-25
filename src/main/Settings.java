package main;

public class Settings {
    public static boolean collisionEnabled = true;
    public static boolean showHitboxes = false;

    public static boolean level1 = true;
    public static boolean level2 = false;
    public static int level = 1;

    //Updates level based on level1/level2 boolean flags.
    public static void levelchecker(){
        if (level1) {
            level = 1;
        } else if (level2) {
            level = 2;
        }

    }

}


