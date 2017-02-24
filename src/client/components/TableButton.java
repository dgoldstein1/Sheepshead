package client.components;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Dave on 04/06/2015.
 */
public class TableButton extends JButton{
    private ButtonType type;
    private String label;
    private int c;
    //isFaceDown means displaying 'faceDown' icon
    //isEmpty means not displaying any icon (is null)
    private boolean isFaceDown,isNull;

    /**
     * constructor for non-card buttons
     * @param type type of button
     * @param label label on button
     */
    public TableButton(ButtonType type,String label){
        c = -1;
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
        Dimension standardCardSize = new Dimension(50,75);
        Dimension aiPlayerCardSize = new Dimension(20,27);
        if(type.equals(ButtonType.PLAYERCARD)) setPreferredSize(standardCardSize);
        else if(type.equals(ButtonType.AICARD)){
            setPreferredSize(aiPlayerCardSize);
            setMinimumSize(aiPlayerCardSize);
        }
        else if(type.equals(ButtonType.CARD_PLAYED)) setPreferredSize(standardCardSize);

        //set as transparent
        setBorder(null);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);



        isFaceDown=true;
        isNull=true;
        this.type = type;
        this.c = -1;

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

    /**
     * if not of type card returns null
     * @return card
     */
    public int cardID(){
        if(c!=-1)
            return c;
        return -1;
    }

    /**
     * called by view to make card view null
     */
    public void setAsNull(){
        setIcon(null);
        c = -1;
        isNull=true;
        isFaceDown=false;
    }

    /**
     * called by view for AI players
     */
    public void setFaceDown(){
        isNull=false;
        isFaceDown=true;
        setIcon(new StretchIcon("CardImages/FACE_DOWN.JPG", false));
    }

    /**
     * called by view to setButton to specific value
     * @param c card to set value to
     */
    public void setCard(int c){
        isFaceDown=false;
        isNull=false;
        setIcon(new StretchIcon("CardImages/" + c + ".JPG", false));
        this.c = c;
    }

    /*getters and setters*/

    public boolean isNull(){
        return isNull;
    }
    //default (null) state is faceDown
    public boolean isFaceDown(){
        return isFaceDown;
    }
    public int card(){
        return c;
    }

}
