package Controller;

import Model.Card;
import Model.Game;
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
    public Controller(){
        gameNotifier = new GameNotifier(this);
        g = new Game(true,gameNotifier);

        //run GUI from EDT
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                frame = new SheepsheadMainFrame(g,gameNotifier);

                int FPS = 30;
                refreshTimer = new Timer( 1000 / FPS, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        frame.refresh(g);
                    }
                });
                refreshTimer.start();
            }
        } );



        while(true){
            g.playRound();
        }
    }

    /*in from View out to Game*/

    public void playerCardPushed(int cardID){
        g.playerCardPushed(cardID);
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
    public void newRound(){
        //frame.newRound
    }

    /*in from View out to View*/
    public void helpPushed(){
        frame.helpPushed();
    }
    public void statsPushed(){
        frame.statsPushed(g.getScoreboard());
    }

    public void newGamePushed(){

        /*
        frame.dispose();
        g = new Game(true,gameNotifier);
        frame = new SheepsheadMainFrame(g,gameNotifier);
 //       this.play();
 */
    }

}



