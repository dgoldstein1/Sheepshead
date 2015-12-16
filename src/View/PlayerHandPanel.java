package View;

/**
 * Created by Dave on 11/12/2015.
 */

import Model.Card;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.util.*;

/**
 * displays player hand
 */
class PlayerHandPanel extends JPanel {
    private ArrayList<TableButton> cardsDisplayed; //buttons of numberFaceDownCards in hand
    private MouseListener listener;
    private JLabel pointsDisplayer;
    private int pointsDisplayed;

    PlayerHandPanel(String name,MouseListener listener) {
        this.listener = listener;
        JLabel nameLabel = new JLabel(name);
        nameLabel.setForeground(Color.WHITE);
        add(nameLabel);

        pointsDisplayed = 0;
        pointsDisplayer = new JLabel("| Points: " + pointsDisplayed);
        pointsDisplayer.setForeground(Color.WHITE);
        add(pointsDisplayer);

        //init numberFaceDownCards as buttons and init 6 null buttons
        cardsDisplayed = new ArrayList<TableButton>(8);
        for (int i = 0; i < 8; i++) {
            TableButton card = new TableButton(ButtonType.PLAYERCARD);
            card.addMouseListener(listener);
            cardsDisplayed.add(i, card);
            this.add(cardsDisplayed.get(i)); //put on panel
        }


        setOpaque(false);


    }

    /**
     * called by timer
     * repaints panel based on updated model
     *
     * @return true if success, else return false
     */
    public boolean refresh(java.util.List<Card> cardsInHand,int points) {
        //remove numberFaceDownCards not in hand
        for (TableButton tb : cardsDisplayed) {
            if (!cardsInHand.contains(tb.card())) { //table button not in card model
                tb.setAsNull();
            }
        }
        //add numberFaceDownCards not already displayed
        for (Card c : cardsInHand) {
            if (!showing(c)) {
                addCardToHand(c);
            }
        }
        if(points!=pointsDisplayed){
            pointsDisplayer.setText("| Points: " + points);
            pointsDisplayed = points;
        }
        return true;
    }

    /**
     * helper function
     *
     * @param c card to check
     * @return true if showing selected card, false otherwise
     */
    private boolean showing(Card c) {
        for (TableButton tb : cardsDisplayed) {
            if (tb.card() != null) { //check for nullpointer
                if (tb.card().equals(c)) return true;
            }
        }
        return false;
    }

    /**
     * adds card to hand in null spot
     *
     * @param c Card to add
     * @throws IndexOutOfBoundsException hand is full
     */
    private void addCardToHand(Card c) {
        for (TableButton tb : cardsDisplayed) {
            if (tb.card() == null) {
                tb.setCard(c);
                return;
            }
        }
        throw new IndexOutOfBoundsException("ADDING CARD TO FULL PLAYER HAND");
    }


}

