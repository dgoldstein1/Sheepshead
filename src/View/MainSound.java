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
    private ArrayList<Clip> currEffects, currMusic;
    private ArrayList<String> loopedDirs;
    private boolean effectsMuted, musicMuted;

    public MainSound() {
        pool = Executors.newFixedThreadPool(10);
        currEffects = new ArrayList<Clip>();
        currMusic = new ArrayList<Clip>();
        loopedDirs = new ArrayList<String>(10);
        effectsMuted = musicMuted = false;
    }

    /**
     * plays effect from dir
     * if dir is not runnable, throws exception and prints stack trace
     *
     * @param dir
     */
    public void playEffect(final String dir) {
        if (!effectsMuted){
            try {
                Clip clip = createClip(dir);
                currEffects.add(clip);
                Runnable sound = new SoundThread(clip, false);
                pool.execute(sound);
            } catch (Exception e) {
                new PopUpFrame("could not initialize " + dir + " in MainSound.java");
                e.printStackTrace();
            }
        }
    }

    /**
     * plays sounds from dir forever, or until stopped
     *
     * @param dir sound file
     */
    public void playMusic(String dir) {
        if (!musicMuted){
            try {
                Clip clip = createClip(dir);
                Runnable sound = new SoundThread(clip, true);
                pool.execute(sound);
                currMusic.add(clip);
            } catch (Exception e) {
                new PopUpFrame("could not initialize " + dir + " in MainSound.java");
                e.printStackTrace();
            }
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



    /*
        getters and setters
     */

    public void setEffectsMuted(boolean b){
        if(b&&!effectsMuted){ //turn effects off
            for(Clip c: currEffects){
                if(c.isActive()) c.close();
            }
            currEffects.clear();
        }
        effectsMuted = b;

    }
    public synchronized void setMusicMuted(boolean b){
        if(b && !musicMuted){//turn off
            for(Clip c : currMusic){
                if(c.isActive()) c.close();
            }
        }
        else if(!b && musicMuted){//turn on
            musicMuted = false;
            for(String s : loopedDirs){
                try {
                    Clip clip = createClip(s);
                    Runnable sound = new SoundThread(clip, true);
                    pool.execute(sound);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return;//no need to set musicMuted twice
        }

        musicMuted = b;
    }
    public boolean effectsMuted(){
        return effectsMuted;
    }
    public boolean musicMuted(){
        return musicMuted;
    }


    /*
        class for running sound threads
     */

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


