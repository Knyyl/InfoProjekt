package main;

public class Settings {
    public static boolean collisionEnabled = true;
    public static boolean showHitboxes = false;

    public static boolean level1 = false;
    public static boolean level2 = false;
    public static boolean level3 = false;

    public static int level = 0;

    public static void levelchecker (){
        if (level1 == true) {
            level2 = false;
            level3 = false;
            level = 0;
        }
        else if ( level2 == true){
            level1 = false;
            level3 = false;
            level = 1;
        }
        else if ( level3 == true){
            level2 = false;
            level1 = false;
            level = 2;
        }
    }
}
