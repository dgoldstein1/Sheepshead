package main.client.game_display;

import main.client.components.ButtonType;
import main.protocols.GameState;
import main.protocols.PlayerData;
import main.client.options.SoundEffect;
import main.client.components.TableButton;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * Created by Dave on 11/2/2015.
 * Main Controller between main.client and UI
 */
public class UIController extends MouseAdapter {
    SheepsheadMainFrame frame;
    public int[] usersettings; //[sound effects, play music, game speed percentage, run debugger]
    public String name;

    public UIController(GameState initialGameState) {
        this.frame = new SheepsheadMainFrame(initialGameState,this);
        usersettings = new int[] {0,0,50,0};
        name = initialGameState.players.get(0).name;
        if (!refresh(initialGameState)) {
            System.out.println("could not initialize UIController");
            System.exit(1);
        }
    }


    /* in from main.server */

    /**
     * updates display
     * @param g
     */
    public boolean refresh(GameState g){
        return frame.refresh(g);
    }


    /*in from main.client*/
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


    /**
     * display sample game
     * @param args
     */
    public static void main(String[] args) {

        ArrayList<PlayerData> players = new ArrayList<PlayerData>();
        for (int i = 0; i < 5; i++) {
            players.add(new PlayerData(i + "", 10, 5, i, false, false));
        }

        ArrayList<Integer> cardsOnTable = new ArrayList<Integer>(6){{
            add(14); add(15); add(4); add(8); add(13); add(1);
        }};

        ArrayList<Integer> cardsInHand = new ArrayList<Integer>(6){{
            add(2); add(5); add(7); add(3); add(9); add(11);
        }};

        GameState defaultData = new GameState(players, cardsOnTable,cardsInHand);

        new UIController(defaultData);



    }

}
