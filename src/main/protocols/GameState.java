package main.protocols;

import main.client.Client;
import main.server.ai.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by dave on 2/21/17.
 * Serializable packet sent from main.server for main.client to display
 */
public class GameState implements Serializable{
    private static final Logger LOGGER = Logger.getLogger( Client.class.getName() );

    /**
     * Cards in the current player's hand
     * Each n corresponds to card ID
     * Valid lengths are between 0 and 7 (5 cards + 2 to bury)
     */
    public ArrayList<Integer> cardsInHand;
    /**
     * Cards to be displayed publically in the center of the table
     * Order matters here, each card corresponds to the 'place on the table'
     * it should have.
     * Ex) [1,5,7] means player 0 should have Seven of Spades, etc.
     *
     * Valid lengths are 0 through 5 (max of 5 players
     */
    public ArrayList<Integer> cardsOnTable;
    /**
     * Player data to be displayed around the table for each player
     * Again, order is important here and should match up with cardsPlayed
     * Valid length is five only
     * TODO: support for less than five players?
     */
    public ArrayList<PlayerData> players;
    /**
     * Current state of game in server. May not be null
     */
    public State state;

    // initialize empty state
    public GameState(){}

    public GameState(ArrayList<PlayerData> players, ArrayList<Integer> cardsOnTable, ArrayList<Integer> cardsInHand, State state){
        if (cardsInHand.size() >= 0 && cardsInHand.size() <= 7 )
            this.cardsInHand = cardsInHand;
        else LOGGER.log(Level.SEVERE, "Incorrect size : " + cardsInHand.size() + " for cardsInHand passed to GameState");

        if (cardsOnTable.size() >= 0 && cardsOnTable.size() <= 5)
            this.cardsOnTable = cardsOnTable;
        else LOGGER.log(Level.SEVERE, "Incorrect size : " + cardsOnTable.size() + " for cardsOnTable passed to GameState");

        if (players.size()==5)this.players = players;
        else LOGGER.log(Level.SEVERE, "Incorrect size " + players.size() + " for players passed to GameState");

        if (state!=null) this.state = state;
        else LOGGER.log(Level.SEVERE, "Null state passed to GameState");

    }

    public enum State {INIT,DEAL,BLIND,ROUND,POST_GAME}


}
