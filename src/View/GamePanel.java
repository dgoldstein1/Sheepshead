package View;

import Model.Card;
import Model.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;


/**
 * Created by Dave on 11/4/2015.
 * <p/>
 * Table displaying numberFaceDownCards on the table / player hand
 */
public class GamePanel extends JPanel {
    private PlayerHandPanel playerHand;
    private TablePanel tableView;

    /**
     * sets up player hand panel and card table and adds them to this panel
     */
    public GamePanel(Game g, MouseListener listener) {
        //find ai player
        setLayout(new BorderLayout());
        playerHand = new PlayerHandPanel(g.getPlayers()[0].getUsername(),listener);
        tableView = new TablePanel(g.getPlayers());
        this.add(playerHand, BorderLayout.SOUTH);
        this.add(tableView, BorderLayout.CENTER);
        setOpaque(false);
    }


    /**
     * refreshes values in GUI
     *
     * @return true if success, false otherwise
     */
    public boolean refresh(Game g) {
        repaint();
        playerHand.refresh(g.getNonAiPlayer());
        tableView.refresh((g.table.getCurrentHand().getCardsInHand()),g.getStaticPlayers(),g.table);
        return true;
    }

    //in from model
    public Card getPlayerCard(String prompt){
        return playerHand.getPlayerCard(prompt);
    }
    public void playerCardPushed(Card card){
        playerHand.setSelectedCard(card);
    }

}
