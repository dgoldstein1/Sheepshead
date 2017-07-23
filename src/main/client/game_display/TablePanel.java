package main.client.game_display;

/**
 * Created by Dave on 11/12/2015.
 */

import main.protocols.GameState;
import main.protocols.PlayerData;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * displays numberFaceDownCards played on table
 */
class TablePanel extends JPanel {
    private ArrayList<PlayerPanel> otherPlayers;
    public CardsOnTable cardsPlayed;

    public TablePanel(GameState g) {
        cardsPlayed = new CardsOnTable(g);
        otherPlayers = new ArrayList<PlayerPanel>(4);
        for(PlayerData p : g.players){
            if(p.id!=0) otherPlayers.add(new PlayerPanel(p.name));
        }

        setOpaque(false);
        setupLayout();

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
        add(otherPlayers.get(1),c); //player 2

        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 0.5;
        add(otherPlayers.get(2),c); //palyer 3

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
        add(otherPlayers.get(0),c); //player 1

        c.gridx = 2;
        c.gridy = 2;
        c.weightx = 0.5;
        add(otherPlayers.get(3),c);//player 5

    }

    /**
     * used in testing
     * @return players currently displayed in UI
     */
    public ArrayList<PlayerData> getDisplayedPlayers() {
        ArrayList<PlayerData> players = new ArrayList();
        for (PlayerPanel pp : otherPlayers) players.add(pp.getPlayerData());
        return players;
    }

    public boolean refresh(GameState g) {
        for(int i = 0;i<4;i++){
            if(!otherPlayers.get(i).refresh(g.players.get(i+1))){
                return false;
            }
        }
        repaint();
        return cardsPlayed.refresh(g);
    }
}

