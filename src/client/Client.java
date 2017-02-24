package client;

import client.components.ButtonType;
import client.model.GameState;
import client.options.SoundEffect;
import client.components.TableButton;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Dave on 11/2/2015.
 * Sends changes to server
 */
public class Client extends MouseAdapter {
    SheepsheadMainFrame frame;
    public int[] usersettings; //[sound effects, play music, game speed percentage, run debugger]
    public String name;

    public Client(GameState initialGameState) {
        this.frame = new SheepsheadMainFrame(initialGameState,this);
        usersettings = new int[] {0,0,50,0};
        name = initialGameState.players.get(0).name;
        if (!refresh(initialGameState)) {
            System.out.println("could not initialize Client");
            System.exit(1);
        }
    }


    /* in from server */

    /**
     * updates display
     * @param g
     */
    public boolean refresh(GameState g){
        return frame.refresh(g);
    }


    /*in from client*/
    public boolean debuggerRunning = false;
    public String username = "default";


    @Override
    public void mousePressed(MouseEvent e) {

        if (e.getButton() == MouseEvent.BUTTON1) {
            TableButton button = (TableButton) e.getComponent();
            ButtonType buttonType = button.type();

            if (buttonType.equals(ButtonType.HELP)) {
                frame.helpPushed();

            } else if (buttonType.equals(ButtonType.SCOREBOARD)) {
                //frame.statsPushed(); // TODO: 2/21/17

            } else if (buttonType.equals(ButtonType.SETTINGS)) {
                frame.settingsPushed(this);

            } else if (buttonType.equals(ButtonType.PLAYERCARD)) {
                if (button.cardID() != -1){}
                    //// TODO: 2/21/17

            }


        }


    }

    public void playSound(SoundEffect ef){
        frame.playSound(ef);
    }
}
