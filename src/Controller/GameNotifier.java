package Controller;

import Model.Game;
import View.ButtonType;
import View.TableButton;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Dave on 11/2/2015.
 */
public class GameNotifier extends MouseAdapter implements GameObserver {
    private Controller control;

    public GameNotifier(Controller control) {
        this.control = control;
    }

    /*in from Game*/
    public void aiCardPlayed(int cardID) {
        //g.playNonAiCard(id)
    }


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

            }


        }


    }
}
