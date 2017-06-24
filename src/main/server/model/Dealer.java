package main.server.model;


import main.server.ai.Player;

/**
 * Created by Dave on 9/17/2015.
 * procides cards to players
 * and collect cards from table
 */
public class Dealer {
    private Player[] players;
    private Deck deck;
    Table table;

    public Dealer(Player[] pl, Table table) {
        players = pl;
        deck = new Deck();
        this.table = table;
    }

    /**
     * called by game
     * deals cards to each player in players[]
     *
     * @param handSize size of hand (standard is six cards)
     */
    public void dealCards(int handSize) {
        deck.shuffle();
        table.dealBlind(deck.getBlind());
        for (Player p : players) {
            p.dealHand(deck.dealHand(handSize));
        }
    }

    /**
     * called by game at end of round
     * moves cards from table back into deck
     */
    public void collectCards() {
        deck.returnCards(table.clearTable());
    }

    /*
        getters and setters
     */

    /**
     * prints out cards in deck
     */
    public void printCards() {
        deck.printDeck();
    }

    /**
     * called by game after player input
     * if card is not found returns QC with id of -1
     *
     * @param cardName String name of card in format VS (value, suit)
     * @return Card
     */
    public Card getCardID(String cardName) {
        return deck.getID(cardName);
    }

    /**
     * called by game to process player input
     * @param ID card id
     * @return card if found, null if not
     */
    public Card getCard(int ID){
        return deck.getCard(ID);
    }



}
