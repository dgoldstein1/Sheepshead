package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * history of hand played used to store data and tally up points
 * used by table, scoreboard
 * Created by Dave on 9/24/2015.
 */
public class HandHistory {
    private List<CardHistory> hand;

    public HandHistory() {
        hand = new ArrayList<CardHistory>();
    }

    /**
     * adds given hand history to hand
     *
     * @param h hand history
     */
    public void add(CardHistory h) {
        hand.add(h);
    }

    /**
     * gets player with highest card played in hand
     * returns by value then suit
     *
     * @return Player winner
     */
    public Player topPlayer() {
        Card cardLed = hand.get(0).c;
        if (cardLed.getId() == -1) {//no cards played
            System.out.println("NO CARDS PLAYED YET. IMPOSSIBLE TO GET TOP CARD");
            System.exit(1);
        }
        Player p = hand.get(0).playedBy; //place holder for comparison
        Card top = hand.get(0).c;        //place holder for comparison
        if (cardLed.isTrump()) {//trump led
            for (CardHistory ch : hand) {
                if ((ch.c).greaterThan(top)) { //Value > Suit
                    top = ch.c;
                    p = ch.playedBy;
                }
            }
        } else {//fail led
            Suit suitLed = cardLed.getCardSuit();
            Suit suitPlayed = null;
            for (CardHistory ch : hand) {
                if (top.isTrump() || ch.c.isTrump()) {//comparing trump Value > Suit
                    if ((ch.c).greaterThan(top)) {
                        top = ch.c;
                        p = ch.playedBy;
                    }
                } else {//comparing fail with fail. Defaults to Suit > Value
                    suitPlayed = ch.c.getCardSuit();
                    if (suitLed == suitPlayed) {//only compare between suit led
                        if ((ch.c).greaterThan(top)) {
                            top = ch.c;
                            p = ch.playedBy;
                        }
                    }
                }
            }
        }
        return p;
    }


    /*
     getters and setters
     */


    /**
     * gets and returns total points in hand
     *
     * @return points of hand
     */
    public int getPoints() {
        int points = 0;
        for (CardHistory ch : hand) {
            points += ch.c.getPointValue();
        }
        return points;
    }




}



