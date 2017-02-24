package client.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by dave on 2/21/17.
 * Serializable packet sent from server for client to display
 */
public class GameState implements Serializable{

    public ArrayList<Integer> cardsInHand, cardsOnTable;
    public ArrayList<PlayerData> players;

    public GameState(ArrayList<PlayerData> players, ArrayList<Integer> cardsOnTable, ArrayList<Integer> cardsInHand){
        this.cardsInHand = cardsInHand;
        this.cardsOnTable = cardsOnTable;
        this.players = players;
    }


}
