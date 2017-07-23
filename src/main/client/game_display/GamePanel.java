package main.client.game_display;

import main.protocols.GameState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;


/**
 * Created by Dave on 11/4/2015.
 * <p/>
 * Table displaying numberFaceDownCards on the table / player hand
 */
public class GamePanel extends JPanel {
    public PlayerHandPanel playerHand;
    public TablePanel tableView;

    /**
     * sets up player hand panel and card table and adds them to this panel
     */
    public GamePanel(GameState g, MouseListener listener) {
        //find ai player
        setLayout(new BorderLayout());
        playerHand = new PlayerHandPanel(g.players.get(0).name ,listener);
        tableView = new TablePanel(g);
        this.add(playerHand, BorderLayout.SOUTH);
        this.add(tableView, BorderLayout.CENTER);
        setOpaque(false);
    }


    /**
     * refreshes values in GUI
     *
     * @return true if success, false otherwise
     */
    public boolean refresh(GameState g) {
        repaint();
        playerHand.refresh(g);
        tableView.refresh(g);
        return true;
    }

    //in from model
    public int getPlayerCard(String prompt){
        return playerHand.getPlayerCard(prompt);
    }
    public void playerCardPushed(int card){
        playerHand.setSelectedCard(card);
    }

}
