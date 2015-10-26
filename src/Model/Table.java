package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dave on 9/16/2015.
 * remembers only cards played for each hand
 * see scoreboard.class for detailed statistics on card history
 * all players, Game, scoreboard, and dealer have access to table
 * at max, this class keeps track of the cards played for each round
 */
public class Table {
    private HandHistory currentHand;
    private List<Card> table;
    private Card[] blind;
    private Card partnerCard, cardLed;
    private int currHandNumber;
    private boolean printAll, leaster;
    private Player leasterBlindWinner;

    public Table(boolean printAll) {
        currHandNumber = 0;
        table = new ArrayList<Card>();
        currentHand = new HandHistory();
        blind = new Card[2];
        this.printAll = printAll;
        leasterBlindWinner = null;
        leaster = false;
    }

    /**
     * called by player to pick up blind, which is then set to null
     *
     * @param username username of player picking up blind
     * @return Blind
     */
    public Card[] pickUpBlind(String username) {
        Card[] temp = blind;
        blind = null;
        if (printAll) System.out.println("\t" + username + " picked up \n");
        return temp;
    }

    /**
     * called by dealer to intialize blind
     *
     * @param blind
     */
    public void dealBlind(Card[] blind) {
        this.blind = blind;
    }

    /**
     * called by players to return blind
     *
     * @param cards
     */
    public void returnBlind(Card[] cards) {
        for (Card c : cards) {
            table.add(c);
        }
    }

    /**
     * called by players
     * players card on table and sets cads led if first card played
     *
     * @param c    Card to play
     * @param name name of player
     */
    public void playCard(Card c, Player name) {
        currentHand.add(new CardHistory(c, name));
        if (cardLed == null) {
            cardLed = c;
        }
        table.add(c);
        if (printAll) {
            System.out.print("\t" + name.getUsername() + " played ");
            c.printCard();
        }
        if (leaster && c.getId() == 24) { //J of D played
            if(printAll)System.out.println("\t\tblind revealed:");
            for (Card card : blind) {
                if(printAll){
                    System.out.print("\t\t");
                    card.printCard();
                }
                currentHand.add(new CardHistory(card, name));
                table.add(card);
            }
            blind = null;
        }
    }

    /**
     * clears table and currend Round data
     * called by dealer at end of round
     *
     * @return List of cards played this hand
     */
    public List<Card> clearTable() {
        List<Card> temp = new ArrayList<Card>(table);
        if (blind != null && leaster) { //is leaster and blind not used
            for (Card c : blind) {
                temp.add(c);
            }
        }
        table.clear();
        leaster = false;
        return temp;
    }

    /**
     * ends hand after all 6 cards have been played
     * resets variables to start next round
     *
     * @return HandHistory cards played this round
     */
    public HandHistory endHand() {
        currHandNumber = currHandNumber + 1 % 6;
        partnerCard = null;
        cardLed = null;
        HandHistory temp = currentHand;
        currentHand = new HandHistory();
        leasterBlindWinner = null;
        return temp;

    }

/*
    non action functions
 */

    /**
     * called at end of round by game
     * returns the winner of the hand
     *
     * @return Player winner
     */
    public Player getWinner() {
        return currentHand.topPlayer();
    }

    /**
     returns card led for each hand
     called by players to determine which card to play
     returns new card with id -1 if no cards have been played
     */

    /**
     * called by players to determine which card to play
     *
     * @return card player, or new card with id -1 if no cards have been played
     */
    public Card cardLed() {
        if (cardLed == null) { //first card played
            return new Card(Value.QUEEN, Suit.CLUBS, -1);
        }
        return cardLed;
    }

    /**
     * called by player if choosing to call up past J of D
     *
     * @param id       new partner card number
     * @param username username of player calling up
     */
    public void callUp(int id, String username) {
        partnerCard = intToCard(id);
        if (printAll) {
            System.out.print("\t" + username + " calling up to ");
            partnerCard.printCard();
            System.out.println("");
        }
    }

    /**
     * gets the current partner card
     *
     * @return partner Card, default = J of D
     */
    public Card getPartnerCard() {
        if (partnerCard != null)
            return partnerCard;
        return new Card(Value.JACK, Suit.DIAMONDS, -1);
    }

    /**
     * getter for currents cards played so far
     *
     * @return List<Card> Cards Played
     */
    public List<Card> cardsOnTable() {
        return table;
    }

    /**
     * given an id, returns a new card of appropriate id value
     * internal function, used by callUp()
     *
     * @param id card Id
     * @return new Card, id with -1 if out of range
     */
    private Card intToCard(int id) {
        if (id == 0) return new Card(Value.SEVEN, Suit.HEARTS, 0);
        if (id == 1) return new Card(Value.SEVEN, Suit.SPADES, 1);
        if (id == 2) return new Card(Value.SEVEN, Suit.CLUBS, 2);
        if (id == 3) return new Card(Value.EIGHT, Suit.HEARTS, 3);
        if (id == 4) return new Card(Value.EIGHT, Suit.SPADES, 4);
        if (id == 5) return new Card(Value.EIGHT, Suit.CLUBS, 5);
        if (id == 6) return new Card(Value.NINE, Suit.HEARTS, 6);
        if (id == 7) return new Card(Value.NINE, Suit.SPADES, 7);
        if (id == 8) return new Card(Value.NINE, Suit.CLUBS, 8);
        if (id == 9) return new Card(Value.ACE, Suit.HEARTS, 9);
        if (id == 10) return new Card(Value.ACE, Suit.SPADES, 10);
        if (id == 11) return new Card(Value.ACE, Suit.CLUBS, 11);
        if (id == 12) return new Card(Value.TEN, Suit.HEARTS, 12);
        if (id == 13) return new Card(Value.TEN, Suit.SPADES, 13);
        if (id == 14) return new Card(Value.TEN, Suit.CLUBS, 14);
        if (id == 15) return new Card(Value.KING, Suit.HEARTS, 15);
        if (id == 16) return new Card(Value.KING, Suit.SPADES, 16);
        if (id == 17) return new Card(Value.KING, Suit.CLUBS, 17);
        if (id == 18) return new Card(Value.SEVEN, Suit.DIAMONDS, 18);
        if (id == 19) return new Card(Value.EIGHT, Suit.DIAMONDS, 19);
        if (id == 20) return new Card(Value.NINE, Suit.DIAMONDS, 20);
        if (id == 21) return new Card(Value.ACE, Suit.DIAMONDS, 21);
        if (id == 22) return new Card(Value.TEN, Suit.DIAMONDS, 22);
        if (id == 23) return new Card(Value.KING, Suit.DIAMONDS, 23);
        if (id == 24) return new Card(Value.JACK, Suit.DIAMONDS, 24);
        if (id == 25) return new Card(Value.JACK, Suit.HEARTS, 25);
        if (id == 26) return new Card(Value.JACK, Suit.SPADES, 26);
        if (id == 27) return new Card(Value.JACK, Suit.CLUBS, 27);
        if (id == 28) return new Card(Value.QUEEN, Suit.DIAMONDS, 28);
        if (id == 29) return new Card(Value.QUEEN, Suit.HEARTS, 29);
        if (id == 30) return new Card(Value.QUEEN, Suit.SPADES, 30);
        if (id == 31) return new Card(Value.QUEEN, Suit.CLUBS, 31);
        else return new Card(Value.QUEEN, Suit.CLUBS, -1); //no card found
    }

    /**
     * sets leaster round to true
     * called by Game after blind not picked up
     */
    public void setLeaster() {
        leaster = true;
    }

    /**
     * getter for if curr round is leaster round
     * called by Players when choosing card to play
     *
     * @return is leaster
     */
    public boolean isLeasterRound() {
        return leaster;
    }


}
