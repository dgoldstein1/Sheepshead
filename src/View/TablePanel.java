package View;

import Model.Game;

import javax.swing.*;
import java.awt.event.MouseListener;
import java.util.List;

/**
 * Created by Dave on 11/4/2015.
 *
 * Table displaying cards on the table / player hand
 *
 */
public class TablePanel extends JPanel {
    private List<TableButton> cardsOnTable,cardsInHand;

    public TablePanel(Game g,MouseListener listner){



    }

    /**
     * puts items on board, using mouse listner
     * @param l listerner
     * @return true if set up, false otherwise
     */
    private boolean setUpBoard(MouseListener l){
        return true; //todo
    }


    /**
     * refreshes values in GUI
     * @return
     */
    public boolean refresh(Game g){
        return true; //todo
    }

}
