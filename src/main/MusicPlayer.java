package main;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class MusicPlayer {
    private Clip clip;

    public void playRandomFromFolder(String folderPath) {
        File folder = new File(folderPath);
        System.out.println("Requested to play music from folder: " + folderPath);
        System.out.println("Looking for music in: " + folder.getAbsolutePath());

        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".wav"));
        if (files == null || files.length == 0) {
            System.err.println("No .wav files found in " + folder.getAbsolutePath());
            return;
        }

        File randomFile = files[new Random().nextInt(files.length)];
        System.out.println("Now playing: " + randomFile.getName());

        try {
            // Stop and close previous clip if it exists
            if (clip != null) {
                if (clip.isRunning()) clip.stop();
                clip.close();
            }

            AudioInputStream audioInput = AudioSystem.getAudioInputStream(randomFile);
            clip = AudioSystem.getClip();
            clip.open(audioInput);
            clip.loop(Clip.LOOP_CONTINUOUSLY);

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Failed to play: " + randomFile.getName());
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
