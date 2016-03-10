package View;

import sun.tools.jar.Main;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
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
    final int numberOfBahs = 3;

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
     * @param ef
     */
    public void playEffect(SoundEffect ef) {
        if (!effectsMuted){
            String dir = "Sounds/Effects/";
            if(ef.equals(SoundEffect.BAH)) dir += "Sheep/bah" + (int) (Math.random() * numberOfBahs) + ".wav";
            else dir += ef.toString() + ".wav";

            Clip clip = createClip(dir);
            currEffects.add(clip);
            Runnable sound = new SoundThread(clip, false);
            pool.execute(sound);
        }
    }

    /**
     * plays sounds from dir forever, or until stopped
     * if currently muted, adds to list to be played when unmuted
     * @param dir sound file
     */
    public void playMusic(String dir) {
        Clip clip = createClip(dir);
        currMusic.add(clip);
        if(!musicMuted){
            Runnable sound = new SoundThread(clip, true);
            pool.execute(sound);
        }

    }

    /**
     * internal method for creating playable file from dir
     *
     * @param filename
     * @return Clip from sunJava library
     * @throws Exception file not found
     */
    private Clip createClip(String filename) {
        try{
            InputStream is = new BufferedInputStream(Main.class.getResourceAsStream("/" + filename));
            AudioInputStream ais = AudioSystem.getAudioInputStream(is);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            return clip;
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
        return null;
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
                c.stop();
            }
        }
        else if(!b && musicMuted){//turn on
            musicMuted = false;
            for(Clip c : currMusic){
                c.start();
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


