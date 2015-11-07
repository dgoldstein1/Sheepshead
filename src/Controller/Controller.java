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

    /**
     * takes in Game and matching GUI
     * creates corresponding gameNotifier
     */
    public Controller(){
        gameNotifier = new GameNotifier(this);
        g = new Game(false,true,gameNotifier);
        frame = new SheepsheadMainFrame(g,gameNotifier);

    }

    /*in from View out to Game*/
    public void cardPlayed(int cardID){
        //g.nonAICardPlayed(int id)
    }

    /*in from Game out to View*/
    public void validCardPlayed(int id){
        //frame.validCardPlayed(int id)
    }
    public void invalidCardPlayed(int id){
        //frame.invalidCardPlayed(int id)
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
        //todo
    }


}
