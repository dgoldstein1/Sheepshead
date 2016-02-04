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
    private CardBox cardsDisplayed; //buttons of numberFaceDownCards in hand
    private MouseListener listener;
    private JLabel pointsDisplayer,promptDisplayer;
    private int pointsDisplayed;
    private boolean readyForPlayerInput;
    private Card playerCardEntered;

    PlayerHandPanel(String name,MouseListener listener) {
        this.listener = listener;
        setLayout(new BorderLayout());
        initLabels(name);
        cardsDisplayed = new CardBox(listener);
        this.add(cardsDisplayed,BorderLayout.CENTER);
        setOpaque(false);
    }

    /**
     * inits buttons
     * @param playername
     */
    private void initLabels(String playername){
        addLabel(playername, BorderLayout.WEST);
        pointsDisplayed = 0;
        pointsDisplayer = addLabel("| Points: " + pointsDisplayed, BorderLayout.EAST);
        promptDisplayer = addLabel("",BorderLayout.NORTH);
        readyForPlayerInput = false;
        playerCardEntered = null;
    }



    /**
     * internal method to add labels to box
     * @param startingText
     * @return new JLabel made
     */
    private JLabel addLabel(String startingText, String layout){
        JLabel label = new JLabel(startingText);
        label.setForeground(Color.WHITE);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        add(label,layout);
        return label;
    }

    /**
     * called by timer
     * repaints panel based on updated model
     *
     * @return true if success, else return false
     */
    public boolean refresh(java.util.List<Card> cardsInHand,int points) {
        //remove numberFaceDownCards not in hand
        for (TableButton tb : cardsDisplayed.cards) {
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
        repaint();


        return true;
    }

    /**
     * helper function
     *
     * @param c card to check
     * @return true if showing selected card, false otherwise
     */
    private boolean showing(Card c) {
        for (TableButton tb : cardsDisplayed.cards) {
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
        for (TableButton tb : cardsDisplayed.cards) {
            if (tb.card() == null) {
                tb.setCard(c);
                return;
            }
        }
        throw new IndexOutOfBoundsException("ADDING CARD TO FULL PLAYER HAND");
    }

    /**
     * in from model, runs on application thread
     * @param prompt displayed to ask for Card
     * @return Card
     */
    public Card getPlayerCard(String prompt){
        readyForPlayerInput = true;
        Card temp = null;
        promptDisplayer.setText(prompt);
        try{
            while(playerCardEntered==null){
                Thread.sleep(100);
            }
            //card selected by user
            temp = playerCardEntered;
            playerCardEntered = null;
            readyForPlayerInput = false;
            return temp;


        } catch(InterruptedException e){
            e.printStackTrace();
            System.exit(1);
        }

        promptDisplayer.setText("");
        return temp;

    }

    /**
     * in from view on EDT
     * @param c card selected
     */
    public void setSelectedCard(Card c){
        synchronized (this) { //critical section with getPlayerCard()
            if(readyForPlayerInput) {
                playerCardEntered = c;
            }
        }
    }


}


class CardBox extends JPanel{
    public ArrayList<TableButton> cards;

    public CardBox( MouseListener listener){
        cards = new ArrayList<TableButton>(8);

        //initButtons numberFaceDownCards as buttons and initButtons 6 null buttons
        for (int i = 0; i < 8; i++) {
            TableButton card = new TableButton(ButtonType.PLAYERCARD);
            card.addMouseListener(listener);
            cards.add(i, card);
            this.add(cards.get(i)); //put on panel
        }

        setOpaque(false);
    }


}

