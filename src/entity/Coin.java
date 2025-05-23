package entity;

import main.GamePanel;
import main.GamePlayManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.Random;

public class Coin extends Entity {
    int frameIndex = 0;
    int animationSpeed = 5; // Lower = faster animation
    int animationCounter = 0;
    BufferedImage[] frames;
    GamePanel gp;
    GamePlayManager gpm;
    BufferedImage image;
    Random rand = new Random();
    public static int coinValue = 1;
    public static double speed = 6;

    public int width = 32;
    public int height = 32;

    public Coin(GamePanel gp, GamePlayManager gpm) {
        this.gp = gp;
        this.gpm = gpm;
        setDefaultValues();
        try {
            BufferedImage sheet = ImageIO.read(new File("res/sprites/coin.png"));
            int origW = sheet.getWidth() / 15;   // original frame width / frames amount
            int origH = sheet.getHeight();      // original frame height
            int scaleFactor = 2;                // Bigger = bigger sprite

            // update your obstacle dimensions
            width = origW * scaleFactor;
            height = origH * scaleFactor;

            frames = new BufferedImage[15];
            for (int i = 0; i < 15; i++) {
                // extract the small frame
                BufferedImage orig = sheet.getSubimage(i * origW, 0, origW, origH);

                // create a new BufferedImage at the scaled size
                BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = scaled.createGraphics();
                // use nearest‑neighbor to keep pixels sharp
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                g.drawImage(orig, 0, 0, width, height , null);
                g.dispose();

                frames[i] = scaled;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDefaultValues() {
        x = rand.nextInt(1930) + 2000;
        y = gpm.player.y - 70;
    }


    public void update() {
        x = (int) (x - speed);
        if (x < -width) {
            setDefaultValues(); // Respawn if off-screen
        }
        animationCounter++;
        if (animationCounter >= animationSpeed) {
            frameIndex = (frameIndex + 1) % frames.length;
            animationCounter = 0;
        }
        if (getHitbox().intersects(gpm.player.getHitbox())) {
            collectCoin();
            setDefaultValues(); // Respawn
        }
    }

    public void draw(Graphics2D g2) {
        if (frames != null && frames[frameIndex] != null) {
            g2.drawImage(frames[frameIndex], x, y, null);
        } else {
            // Fallback: draw a white box
            g2.setColor(Color.white);
            g2.fillRect(x, y, width, height);
        }
    }

    public Rectangle getHitbox() {

        return new Rectangle(x , y, width, height);
    }

    public void collectCoin() {
        int currentCoins = getWalletCoins();
        saveWalletCoins(currentCoins + coinValue);
    }

    public static int getWalletCoins() {
        File wallet = new File("wallet.txt");
        if (!wallet.exists()) return 0;

        try (BufferedReader br = new BufferedReader(new FileReader(wallet))) {
            return Integer.parseInt(br.readLine().trim());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    public static void saveWalletCoins(int coins) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("wallet.txt"))) {
            bw.write(String.valueOf(coins));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
