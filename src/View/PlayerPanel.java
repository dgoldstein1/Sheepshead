package View;

import Model.Player;

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
    private JLabel pointDisplayer, name;

    //stores cards for each AI Player
    PlayerPanel(String playerName) {
        setLayout(new BorderLayout());
        int allign = (int) JPanel.CENTER_ALIGNMENT;
        name = new JLabel(playerName);
        name.setHorizontalAlignment(allign);
        name.setForeground(Color.WHITE);
        add(name,BorderLayout.NORTH);
        displayedCards = new ArrayList<TableButton>(8);
        numberFaceDownCards = 0;
        aiHandDisplay = new AIPlayerHandDisplay();
        this.add(aiHandDisplay,BorderLayout.CENTER);
        pointDisplayer = new JLabel("Points: " + pointsDisplayed);
        pointDisplayer.setForeground(Color.WHITE);
        pointDisplayer.setHorizontalAlignment(allign);
        add(pointDisplayer, BorderLayout.SOUTH);
        setOpaque(false);
    }

    /**
     * refresh called by controller
     * sets displayed cards = number of cards in hand
     *
     * @param p curr player updating
     * @return true if success, false otherwise
     */
    public boolean refresh(Player p) {
        if(!p.getUsername().equals(name.getText())){
            name.setText(p.getUsername());
        }

        while (p.getNCards() > numberFaceDownCards) {//more cards in hand than displayed
            aiHandDisplay.addCard();
        }
        while (p.getNCards() < numberFaceDownCards) {//more cards displayed than in hand
            aiHandDisplay.deleteCard();
        }

        //update player points
        if(p.getPoints()!=pointsDisplayed){
            pointDisplayer.setText("Points: " + p.getDisplayablePoints());
            pointsDisplayed = p.getDisplayablePoints();
        }

        return true;
    }

    //holder for TableButtons within Player Panel
    class AIPlayerHandDisplay extends JPanel{

        AIPlayerHandDisplay(){
            for (int i = 0; i < 8; i++) {
                displayedCards.add(i, new TableButton(ButtonType.AICARD));
                this.add(displayedCards.get(i)); //put on panel
                setOpaque(false);
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

