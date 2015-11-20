package View;

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
    }


    /**
     * refreshes values in GUI
     *
     * @return true if success, false otherwise
     */
    public boolean refresh(Game g) {
        playerHand.refresh(g.getNonAiPlayer().getHand().getHand(),g.getNonAiPlayer().getPoints());
        tableView.refresh((g.table.getCurrentHand().getCardsInHand()),g.getStaticPlayers(),g.table);
        return true;
    }

}
