package Controller;

import Model.Card;
import Model.Game;
import View.LogType;
import View.SheepsheadMainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Dave on 11/2/2015.
 * serves as intersection between view and model
 *
 */
public class Controller {
    private Game g;
    private SheepsheadMainFrame frame;
    private GameNotifier gameNotifier;
    private Timer refreshTimer;


    /**
     * takes in Game and matching GUI
     * creates corresponding gameNotifier
     */
    public Controller(String playerName){
        setup(playerName);
        playRound();
        playGameLoop();
    }

    /**
     * continuous loop keeping the game going
     */
    private void playGameLoop(){
        while(gameNotifier.yOrN("Play another round?").equals("y")){
            playRound();
        }
        gameNotifier.log(this.getClass(),LogType.SYSTEM,"shutting down");
        System.exit(0);
    }

    /**
     * sets up Sheapshead game
     */
    private void setup(String playerName){
        gameNotifier = new GameNotifier(this);
        g = new Game(gameNotifier, playerName);


        //run GUI from EDT
        frame = new SheepsheadMainFrame(g, gameNotifier);
        int FPS = 30;
        refreshTimer = new Timer(1000 / FPS, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.refresh(g);
            }
        });
        refreshTimer.start();


    }

    /**
     * plays one round of Sheapshead
     */
    private void playRound(){
        g.playRound();
    }

    /*in from View out to Game*/

    public void playerCardPushed(Card card){
        frame.playerCardPushed(card);
    }


    /*in from Game out to View*/
    public String yOrN(String prompt){
        return frame.yOrN(prompt);
    }
    public boolean refreshView() {
        return frame.refresh(g);
    }
    public Card getPlayerCard(String prompt) {
        return frame.getPlayerCard(prompt);
    }
    public int displayMessage(String prompt) {
        return frame.displayMessage(prompt);
    }
    public void log(Class c, LogType type, String s){
        frame.log(c,type,s);
    }

    /*in from View out to View*/
    public void helpPushed(){
        frame.helpPushed();
    }
    public void statsPushed(){
        frame.statsPushed(g.getScoreboard());
    }
    public void settingsPushed(){
        frame.settingsPushed(g);
    }
    public void startDebugger(){
        frame.launchDebugger();
    }
    public void closeDebugger(){
        frame.closeDebugger();
    }

}



