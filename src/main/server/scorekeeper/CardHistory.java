package main.server.scorekeeper;

import main.server.ai.Player;
import main.server.model.Card;

/**
 * Created by Dave on 9/24/2015.
 * object used to store when cards were played
 * card histories for each round and packaged and stored in hand histories
 */


public class CardHistory{
    Card c;
    Player playedBy;


    public CardHistory(Card c, Player playedBy){
        this.c = c;
        this.playedBy = playedBy;
    }

    public Player playerBy(){
        return playedBy;
    }

    public Card card(){
        return c;
    }

}

