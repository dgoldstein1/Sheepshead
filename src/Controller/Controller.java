package Controller;

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
        frame = new SheepsheadMainFrame(g,gameNotifier);

        refreshTimer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.refresh(g);
            }
        });
        refreshTimer.start();
        this.play();
    }

    /*in from View out to Game*/

    public void playerCardPushed(int cardID){
        g.playerCardPushed(cardID);
    }

    /**
     * plays sheepshead games indefinitely
     */
    public void play(){
        while(true){
            g.playRound();
        }
    }

    /*in from Game out to View*/


    public void newRound(){
        //frame.newRound
    }

    /*in from View out to View*/
    public void helpPushed(){
        frame.helpPushed();
    }
    public void statsPushed(){
       // frame.statsPushed(g.getScoreboard());
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
