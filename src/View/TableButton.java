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
     * @param c card
     */
    public TableButton(ButtonType type, Card c){
        this.type = type;
        this.label = c.getPointValue() + " of " + c.getCardSuit();
        this.c = c;
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

}
