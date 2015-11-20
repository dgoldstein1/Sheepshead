package View;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Dave on 11/15/2015.
 */

public class PlayerPanel extends JPanel {
    private ArrayList<TableButton> displayedCards;
    private AIPlayerHandDisplay aiHandDisplay;
    private int numberFaceDownCards;
    private int pointsDisplayed;
    private JLabel pointDisplayer;

    //stores cards for each AI Player
    PlayerPanel(String playerName) {
        setLayout(new BorderLayout());
        int allign = (int) JPanel.CENTER_ALIGNMENT;
        JLabel name = new JLabel(playerName);
        name.setHorizontalAlignment(allign);
        add(name,BorderLayout.NORTH);
        displayedCards = new ArrayList<TableButton>(8);
        numberFaceDownCards = 0;
        aiHandDisplay = new AIPlayerHandDisplay();
        this.add(aiHandDisplay,BorderLayout.CENTER);
        pointDisplayer = new JLabel("Points: " + pointsDisplayed);
        pointDisplayer.setHorizontalAlignment(allign);
        add(pointDisplayer, BorderLayout.SOUTH);

        setBorder(BorderFactory.createLineBorder(Color.black));
    }

    /**
     * refresh called by controller
     * sets displayed cards = number of cards in hand
     *
     * @param cardsInHand number of cards in hand
     * @param points player points in current round
     * @return true if success, false otherwise
     */
    public boolean refresh(int cardsInHand, int points) {
        while (cardsInHand > numberFaceDownCards) {//more cards in hand than displayed
            aiHandDisplay.addCard();
        }
        while (cardsInHand < numberFaceDownCards) {//more cards displayed than in hand
            aiHandDisplay.deleteCard();
        }

        //update player points
        if(points!=pointsDisplayed){
            pointDisplayer.setText("Points: " + points);
            pointsDisplayed = points;
        }

        return true;
    }

    //holder for TableButtons within Player Panel
    class AIPlayerHandDisplay extends JPanel{

        AIPlayerHandDisplay(){
            for (int i = 0; i < 8; i++) {
                displayedCards.add(i, new TableButton(ButtonType.AICARD));
                this.add(displayedCards.get(i)); //put on panel
            }
        }
        /**
         * helper. adds face down card to hand
         */
        private void addCard() {
            for (TableButton tb : displayedCards) {
                if (tb.isNull()) {
                    tb.setFaceDown();
                    numberFaceDownCards++;
                    return;
                }
            }
            throw new IndexOutOfBoundsException("HAND FULL CANNOT ADD CARD");
        }
        /**
         * helper. make one face down card null
         */
        private void deleteCard() {
            for (TableButton tb : displayedCards) {
                if (tb.isFaceDown()) {
                    tb.setAsNull();
                    numberFaceDownCards--;
                    return;
                }
            }
            throw new IndexOutOfBoundsException("HAND EMPTY CANNOT DELETE CARD");
        }



    }



}

