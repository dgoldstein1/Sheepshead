package Controller;

import Model.Card;
import Model.Game;
import View.ButtonType;
import View.TableButton;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by Dave on 11/2/2015.
 */
public class GameNotifier extends MouseAdapter implements GameObserver {
    private Controller control;
    private Card playerCardToPlay;

    public GameNotifier(final Controller control) {
        this.control = control;
        playerCardToPlay = null;

    }

    /*in from game*/


    /*in from view*/
    @Override
    public void mousePressed(MouseEvent e) {
        TableButton button = (TableButton) e.getComponent();

        ButtonType buttonType = button.type();

        if (e.getButton() == MouseEvent.BUTTON1) {
            if (buttonType.equals(ButtonType.HELP)) {
                control.helpPushed();

            } else if (buttonType.equals(ButtonType.SCOREBOARD)) {

            } else if (buttonType.equals(ButtonType.NEW_GAME)) {

            } else if (buttonType.equals(ButtonType.PLAYERCARD)) {
                System.out.println("-----------------------------> got here");
                if(playerCardToPlay==null){ //expecting input
                    playerCardToPlay = button.card(); //return card above

                }
            }


        }


    }
}
