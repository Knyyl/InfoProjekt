package main;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class MusicPlayer {
    private Clip clip;
    private boolean muted = false;

    public void setMuted(boolean muted) {
        this.muted = muted;
        if (muted) stop();
    }

    public boolean isMuted() {
        return muted;
    }

    public void playRandomFromFolder(String folderPath) {
        if (muted) return; // 🔇 Prevent playback if muted

        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".wav"));
        if (files == null || files.length == 0) return;

        File randomFile = files[new Random().nextInt(files.length)];

        try {
            if (clip != null) {
                if (clip.isRunning()) clip.stop();
                clip.close();
            }

            AudioInputStream audioInput = AudioSystem.getAudioInputStream(randomFile);
            clip = AudioSystem.getClip();
            clip.open(audioInput);
            clip.loop(Clip.LOOP_CONTINUOUSLY);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (clip != null) {
            if (clip.isRunning()) clip.stop();
            clip.close();
        }
    }
}
