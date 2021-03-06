package View;

import Model.Player;
import Model.Table;

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
    private JLabel pointDisplayer, nameLabel;
    private boolean partnerDisplayed, pickerDislayed;

    //stores cards for each AI Player
    PlayerPanel(String playerName) {
        setLayout(new BorderLayout());
        displayedCards = new ArrayList<TableButton>(8);
        numberFaceDownCards = 0;
        aiHandDisplay = new AIPlayerHandDisplay();
        this.add(aiHandDisplay,BorderLayout.CENTER);

        //add labels
        nameLabel = createAddLabel(playerName, Color.WHITE,BorderLayout.NORTH);
        pointDisplayer = createAddLabel("Points: " + pointsDisplayed,Color.WHITE,BorderLayout.SOUTH);

    }

    /**
     * creates JLabel and adds to panel
     * @param startingText
     * @param color
     */
    private JLabel createAddLabel(String startingText, Color color, String layout){
        JLabel label = new JLabel(startingText);
        label.setForeground(color);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        add(label, layout);
        setOpaque(false);
        return label;
    }

    /**
     * refresh called by controller
     * sets displayed cards = number of cards in hand
     *
     * @param p curr player updating
     * @return true if success, false otherwise
     */
    public boolean refresh(Player p) {

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

        //update partner + picker displayed
        if(p.isKnownPartner() && !partnerDisplayed){//partner not displayed
            nameLabel.setFont(new Font("Helvetica",Font.ITALIC,12));
            nameLabel.setForeground(Color.orange);
            nameLabel.setText(nameLabel.getText() + "  (PARTNER) ");
            partnerDisplayed = true;
        }
        if(p.pickedUp() && !pickerDislayed){//picker not displayed
            nameLabel.setFont(new Font("Helvetica",Font.ITALIC,12));
            nameLabel.setForeground(Color.MAGENTA);
            nameLabel.setText(nameLabel.getText() + "  (PICKER) ");
            pickerDislayed = true;
        }
        else if((!p.isKnownPartner() && partnerDisplayed) ||
                 !p.pickedUp() && pickerDislayed){//partner or picker should not be displayed (reset)
            nameLabel.setText(p.getUsername());
            partnerDisplayed = pickerDislayed = false;
            nameLabel.setFont(new Font("Helvetica",Font.PLAIN,12));
            nameLabel.setForeground(Color.WHITE);
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

