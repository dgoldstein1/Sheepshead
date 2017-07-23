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
    public SheepsheadMainFrame frame;
    public int[] usersettings = new int[] {0,0,50,0}; //[sound effects, play music, game speed percentage, run debugger]public String name;
    public PlayerData currPlayerData;
    public GameState.State currState;

    public boolean debuggerRunning = false;

    /**
     * initializes ui and displays intial state if it's not empty
     * @param initialGameState
     */
    public UIController(GameState initialGameState) {

        // display inital state if not empty
        if (initialGameState.state != null){
            frame = new SheepsheadMainFrame(initialGameState,this);
            if (!refresh(initialGameState)) {
                System.out.println("could not initialize UIController");
                System.exit(1);
            }
        }
    }


    /* in from main.server */

    /**
     * updates display
     * @param g
     */
    public boolean refresh(GameState g){
        // update high level player data
        currPlayerData = g.players.get(0);
        currState = g.state;

        return frame.refresh(g);
    }

    /**
     * creates GameState from data displayed in UI
     * Used in testing
     * @return
     */
    public GameState getDataCurrentlyDisplayed() {
        GamePanel table = frame.getTable(); //table displayed
        ArrayList<PlayerData> players = table.tableView.getDisplayedPlayers();
        players.add(0,currPlayerData); // add current player to others being displayed
        return new GameState(
                players,
                table.tableView.cardsPlayed.getCardsOnTable(),
                table.playerHand.getPlayerCards(),
                currState
        );

    }

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

    public static void main(String args[]) throws InterruptedException {
        ArrayList<PlayerData> players = new ArrayList<PlayerData>();
        for (int i = 0; i < 5; i++) {
            players.add(new PlayerData(i + "", 10, 5, i, false, false));
        }
        ArrayList<Integer> cardsOnTable = new ArrayList<Integer>(6){{
            add(14); add(15); add(4); add(8); add(13);
        }};
        ArrayList<Integer> cardsInHand = new ArrayList<Integer>(6){{
            add(2); add(5); add(7); add(3); add(9); add(11);
        }};
        GameState mockState = new GameState(players, cardsOnTable,cardsInHand, GameState.State.INIT);
        UIController ui = new UIController(mockState);

    }

}
