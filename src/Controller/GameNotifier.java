package Controller;

import Model.Card;
import View.ButtonType;
import View.LogType;
import View.TableButton;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Dave on 11/2/2015.
 */
public class GameNotifier extends MouseAdapter implements GameObserver {
    private Controller control;

    public GameNotifier(final Controller control) {
        this.control = control;

    }

    /*in from game*/
    public String yOrN(String prompt) {
        return control.yOrN(prompt);
    }

    public boolean refreshView() {
        return control.refreshView();
    }

    public Card getPlayerCard(String prompt) {
        return control.getPlayerCard(prompt);
    }

    public int displayMessage(String s) {
        return control.displayMessage(s);
    }

    public void log(Class c, LogType type, String s) {
        control.log(c, type, s);
    }

    public void startDebugger(){
        control.startDebugger();
    }

    public void closeDebugger(){
        control.closeDebugger();
    }

    /*in from view*/
    @Override
    public void mousePressed(MouseEvent e) {

        if (e.getButton() == MouseEvent.BUTTON1) {
            TableButton button = (TableButton) e.getComponent();
            ButtonType buttonType = button.type();

            if (buttonType.equals(ButtonType.HELP)) {
                control.helpPushed();

            } else if (buttonType.equals(ButtonType.SCOREBOARD)) {
                control.statsPushed();

            } else if (buttonType.equals(ButtonType.SETTINGS)) {
                control.settingsPushed();

            } else if (buttonType.equals(ButtonType.PLAYERCARD)) {
                if (button.cardID() != -1)
                    control.playerCardPushed(button.card());

            }


        }


    }
}
