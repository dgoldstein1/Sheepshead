package View;

/**
 * Created by Dave on 11/12/2015.
 */

import Model.Card;
import Model.Player;
import Model.Table;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * displays numberFaceDownCards played on table
 */
class TablePanel extends JPanel {
    private ArrayList<PlayerPanel> aiplayers;
    private CardsOnTable cardsPlayed;

    public TablePanel(Player[] players) {
        cardsPlayed = new CardsOnTable(players);
        aiplayers = new ArrayList<PlayerPanel>(4);
        for(int i=0;i<4;i++){
            aiplayers.add(new PlayerPanel(players[i+1].getUsername()));
        }
        setupLayout();
        setBorder(BorderFactory.createLineBorder(Color.black));

    }

    /**
     * helper method sets up gridbagLayout
     */
    private void setupLayout(){
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        //add in top two AIplayer panels
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.5;
        add(aiplayers.get(1),c); //player 2

        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 0.5;
        add(aiplayers.get(2),c); //palyer 3

        //add in card view panel
        c.weightx = 0.5;
        c.ipady = 200;
        c.ipadx = 200;
        c.gridx = 1;
        c.gridy = 1;
        add(cardsPlayed,c);
        c.ipadx = 0;
        c.ipady = 0;

        //add bottom two AIplayer panels
        c.gridx = 0;
        c.gridy = 2;

        c.weightx = 0.5;
        add(aiplayers.get(0),c); //player 1

        c.gridx = 2;
        c.gridy = 2;
        c.weightx = 0.5;
        add(aiplayers.get(3),c);//player 5

    }

    public boolean refresh(java.util.List<Card> cardsOnTable,Player[] players, Table t) {
        for(int i = 0;i<4;i++){
            if(!aiplayers.get(i).refresh((players[i+1]).getNCards(),(players[i+1]).getPoints())){
                return false;
            }
        }
        return cardsPlayed.refresh(cardsOnTable,t.getCurrentHand());
    }
}

