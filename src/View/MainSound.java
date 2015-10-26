package View;

import sun.tools.jar.Main;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Plays sounds from Panels
 */

public class MainSound {
    private ExecutorService pool;
    private final int maxThreads = 10;
    private boolean muted;
    private List<Clip> currSounds;

    public MainSound() {
        pool = Executors.newFixedThreadPool(maxThreads);
        currSounds = new ArrayList<Clip>();
        muted = false;

    }

    /**
     * plays effect from dir
     * if dir is not runnable, throws exception and prints stack trace
     *
     * @param dir
     */
    public void playEffect(final String dir) {
        if (muted) return;
        try {
            Clip clip = createClip(dir);
            currSounds.add(clip);
            Runnable sound = new SoundThread(clip, false);
            pool.execute(sound);
        } catch (Exception e) {
            new PopUpFrame("could not initialize " + dir + " in MainSound.java");
            e.printStackTrace();
        }
    }

    /**
     * plays sounds from dir forever, or until stopped
     *
     * @param dir sound file
     */
    public void loopSound(final String dir) {
        if (muted) return;
        try {
            Clip clip = createClip(dir);
            currSounds.add(clip);
            Runnable sound = new SoundThread(clip, true);
            pool.execute(sound);
        } catch (Exception e) {
            new PopUpFrame("could not initialize " + dir + " in MainSound.java");
            e.printStackTrace();
        }
    }

    /**
     * internal method for creating playable file from dir
     *
     * @param filename
     * @return Clip from sunJava library
     * @throws Exception file not found
     */
    private Clip createClip(String filename) throws Exception {
        InputStream is = new BufferedInputStream(
                Main.class.getResourceAsStream("/" + filename));
        AudioInputStream ais = AudioSystem.getAudioInputStream(is);
        Clip clip = AudioSystem.getClip();
        clip.open(ais);
        return clip;
    }


    /**
     * mutes all audio
     */
    public void mute() {
        muted = true;
        for (Clip c : currSounds) {
            if (c.isActive()) {
                c.close();
            }
        }
        currSounds.clear();
    }

    /**
     * unmutes all audio if muted
     */
    public void unmute() {
        muted = false;
    }

    public boolean isMuted() {
        return muted;
    }


    static class SoundThread implements Runnable {
        private boolean loop;
        private Clip clip;

        SoundThread(final Clip clip, boolean loop) {
            this.clip = clip;
            this.loop = loop;
        }

        public void run() {
            if (loop) clip.loop(clip.LOOP_CONTINUOUSLY);
            else clip.start();
        }
    }
}


