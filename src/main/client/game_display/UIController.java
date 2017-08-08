package main.client.game_display;

import main.client.SheepClient;
import main.client.components.ButtonType;
import main.protocols.ActionRequest;
import main.protocols.GameState;
import main.protocols.PlayerData;
import main.client.options.SoundEffect;
import main.client.components.TableButton;
import main.server.SheepHub;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Dave on 11/2/2015.
 * Main Controller between main.client and UI
 */
public class UIController extends MouseAdapter {
    public SheepsheadMainFrame frame;
    public int[] usersettings = new int[] {0,0,50,0}; //[sound effects, play music, game speed percentage, run debugger]public String name;
    public PlayerData currPlayerData;
    public GameState.State currState;
    public SheepClient client;
    private static final Logger LOGGER = Logger.getLogger( SheepClient.class.getName() );

    // for testing
    public ArrayList<ActionRequest> requests = new ArrayList();

    public boolean debuggerRunning = false;

    /**
     * initializes ui and displays intial state if it's not empty
     * @param initialGameState
     */
    public UIController(GameState initialGameState, SheepClient client) {

        this.client = client;

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

    /**
     * displays information to user
     * @param msg msg body
     * @param title title at top
     */
    public void displayMessage(String msg, String title) {
        frame.displayMessage(msg,title);
    }

    /**
     * asks user for prompt
     * @param msg
     * @return
     */
    public boolean yOrN(String msg) {
        return frame.yOrN(msg);
    }

    @Override
    /**
     * when user clicks a button
     * makes request to server
     */
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

            } else if (buttonType.equals(ButtonType.JOIN_CREATE)) {
                frame.joinCreatePushed(this);

            } else if (buttonType.equals(ButtonType.PLAYERCARD)) {
                if (button.cardID() != -1){ // send card played request to server
                    // create request
                    ActionRequest a = new ActionRequest(
                            ActionRequest.ActionType.CARD_PLAYED,
                            this.currPlayerData.id,
                            button.cardID()
                    );
                    requests.add(a);
                    if (client != null) client.send(a);

                }

            }
        }
    }

    /**
     * joins existing game
     * @param name
     * @param port
     */
    public void joinGame(String name, int port) {
        if (this.client != null) {
            LOGGER.log(Level.INFO, "Could not join game " + name + " because client is not null");
            this.displayMessage("Could not join game " + name + " on port " + port +". Already connected to different game.", "Network Error");
            return;
        }
        try {
            client = new SheepClient(name, port);
        } catch (IOException e) {
            e.printStackTrace();
            this.displayMessage("Could not join game " + name + " on port " + port + ". Error connecting to game.", "Network Error");
            return;
        }
        // joined successfully
        this.displayMessage("successfully joined" + name + " on port " + port, "Connection Successful");
    }

    /**
     * creates new game
     * @param name
     * @param port
     */
    public void createGame(String name, int port) {
        try {
            new SheepHub(port);
            Thread.sleep(100);
        } catch (IOException e) {
            this.displayMessage("Could not create game. Error connecting to port.", "Network Error");
            e.printStackTrace();
            return;
        } catch (InterruptedException e) {
            this.displayMessage("Could not create game. Error waiting for game to start", "Concurrecny Error");
            e.printStackTrace();
            return;
        }
        // created game successfully
        this.displayMessage("Game created successfully","Connection Successful");
        this.joinGame(name, port);
    }

    public void playSound(SoundEffect ef){
        frame.playSound(ef);
    }

    public static void main(String args[]) throws InterruptedException, IOException {

        SheepHub hub = new SheepHub(8080);
        Thread.sleep(100);

        //
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
        UIController ui = new UIController(mockState, null);



    }

}
