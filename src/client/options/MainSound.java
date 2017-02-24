package client.options;

import sun.tools.jar.Main;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Plays sounds from Panels
 */

public class MainSound {
    private ExecutorService pool;
    private ArrayList<Clip> effectsPlayed, currMusic, bahList;
    private Clip card_Drawn, card_played, woah;
    private boolean effectsMuted, musicMuted;
    private final int number_bahs = 4;

    public MainSound() {
        pool = Executors.newFixedThreadPool(10);
        effectsPlayed = new ArrayList<Clip>();
        currMusic = new ArrayList<Clip>(3);
        init();
    }

    /**
     * inits clips in enum SoundEffect
     */
    private void init(){
        effectsMuted = musicMuted = false;
        bahList = new ArrayList<Clip>(3);
        for(int i=0;i<number_bahs;i++){
            bahList.add(createClip("Sounds/Effects/Sheep/bah" + i + ".wav"));
        }
        card_Drawn = createClip("Sounds/Effects/CARD_DRAWN.wav");
        card_played = createClip("Sounds/Effects/CARD_PLAYED.wav");
        woah = createClip("Sounds/Effects/WOAH.wav");

    }

    /**
     * plays effect from dir
     * if dir is not runnable, throws exception and prints stack trace
     *
     * @param ef
     */
    public void playEffect(SoundEffect ef) {
        if (!effectsMuted){
            Clip c=null;
            if(ef.equals(SoundEffect.BAH))
                c = bahList.get((int) (Math.random() * bahList.size()));
            else if(ef.equals(SoundEffect.CARD_DRAWN))
                c= card_Drawn;
            else if(ef.equals(SoundEffect.CARD_PLAYED))
                c = card_played;
            else if(ef.equals(SoundEffect.WOAH))
                c = woah;
            assert c != null;
            pool.execute(new SoundThread(c,false));
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
            System.out.println("Error reading " + filename);
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
            for(Clip c: effectsPlayed){
                if(c.isActive()) c.close();
            }
            effectsPlayed.clear();
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

        SoundThread(Clip clip, boolean loop) {
            this.clip = clip;
            this.loop = loop;
        }

        public void run() {
            if (loop) clip.loop(clip.LOOP_CONTINUOUSLY);
            else {
                if(clip.isRunning())
                    clip.stop();
                clip.setFramePosition(0);
                clip.start();
                clip.start();
            }

        }
    }
}


