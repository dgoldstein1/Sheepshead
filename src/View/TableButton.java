package View;

import Model.Card;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;

/**
 * Created by Dave on 04/06/2015.
 */
public class TableButton extends JButton{
    private ButtonType type;
    private String label;
    private Card c;

    /**
     * constructor for non-card buttons
     * @param type type of button
     * @param label label on button
     */
    public TableButton(ButtonType type,String label){
        c = null;
        this.type = type;
        this.label = label;
        this.setText(label);
        init();
    }

    /**
     * constructor for cards
     * @param type type of card / button
     * cards are initialized to null (showing "-")
     */
    public TableButton(ButtonType type){
        this.type = type;
        this.label = "-";
        setText(label);
        this.c = null;
        //setIcon(new StretchIcon("icons/untouched/untouched"+c.id()+".png",false));
        init();
    }

    /**
     * initializes TableButton features
     */
    private void init(){
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setBorderPainted(false);
        setMargin(new Insets(0, 0, 0, 0));
    }


    /*getters and setters*/

    public ButtonType type(){
        return type;
    }
    public String label(){
        return label;
    }

    /**
     * if not of type card returns null
     * @return card
     */
    public Card card(){
        return c;
    }

    /**
     * called by view to make card view null
     */
    public void setAsNull(){
        label = "-";
        setText(label);
        c = null;
    }

    /**
     * called by view for AI players
     */
    public void setFaceDown(){
        label = "x";
        setText(label);
    }

    /**
     * called by view to setButton to specific value
     * @param c card to set value to
     */
    public void setCard(Card c){
        label = c.getCardValue() + " of " + c.getCardSuit();
        setText(label);
        this.c = c;
    }

    /*getters and setters*/

    public boolean isNull(){
        return label.equals("-");
    }
    public boolean isFaceDown(){
        return label.equals("x");
    }

}
