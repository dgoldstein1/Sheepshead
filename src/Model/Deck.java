package Model;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dave on 9/15/2015.
 * holds array of cards
 * only dealer has access
 */

public class Deck {
    private List<Card> deck;
    private int deckSize;

    /**
     * init deck and adds sheephead cards
     */
    public Deck() {
        deck = new ArrayList<Card>();
        int id = 0;
        for (Value cardValue : Value.values()) { //adding in all fail
            for (Suit suit : Suit.values()) {
                if (suit.equals(Suit.DIAMONDS)) break; //diamonds are higher than all fail
                if (cardValue.equals(Value.JACK) || cardValue.equals(Value.QUEEN)) break; //separate order for trump
                deck.add(new Card(cardValue, suit, id));
                id++;
            }
            //add in jacks in opposite suit value (so that points go low->highest
        }
        for (Value cardValue : Value.values()) {
            if (cardValue.equals(Value.JACK)) break; //separate order for trump
            deck.add(new Card(cardValue, Suit.DIAMONDS, id++));
        }

        deck.add(new Card(Value.JACK, Suit.DIAMONDS, id++));
        deck.add(new Card(Value.JACK, Suit.HEARTS, id++));
        deck.add(new Card(Value.JACK, Suit.SPADES, id++));
        deck.add(new Card(Value.JACK, Suit.CLUBS, id++));

        //add in Queens in opposite suit value (so that points go low->highest
        deck.add(new Card(Value.QUEEN, Suit.DIAMONDS, id++));
        deck.add(new Card(Value.QUEEN, Suit.HEARTS, id++));
        deck.add(new Card(Value.QUEEN, Suit.SPADES, id++));
        deck.add(new Card(Value.QUEEN, Suit.CLUBS, id++));

        deckSize = deck.size();
    }


    /**
     * returns blind of deck after cards are delt
     *
     * @return array of 2 cards
     */
    public Card[] getBlind() {
        Card[] blind = new Card[2];
        for (int i = 0; i < 2; i++) {
            blind[i] = deck.get(0);
            deck.remove(0);
        }
        return blind;
    }

    /**
     * compiles a hand for a given hand size to be dealt by dealer
     *
     * @param handSize size of Hand
     * @return hand of given size
     */
    public Hand dealHand(int handSize) {
        Hand h = new Hand(handSize);
        for (int i = 0; i < handSize; i++) {
            h.addCard(deck.get(0));
            deck.remove(0);
        }
        return h;
    }

    /**
     * puts given cards into deck
     *
     * @param cards array of cards
     */
    public void returnCards(List<Card> cards) {
        deck = cards;
        if (deck.size() != deckSize) {
            System.out.println(deckSize - deck.size() + " CARDS NOT RETURNED TO DECK");
            System.exit(1);
        }
    }

    /**
     * shuffles cards in deck into new position
     */
    public void shuffle() {
        ArrayList<Card> temp = new ArrayList<Card>();
        while (!deck.isEmpty()) {
            int loc = (int) (Math.random() * deck.size());
            temp.add(deck.get(loc));
            deck.remove(loc);
        }
        deck = temp;
    }

    /*
        getters and setters
     */

    /**
     * called by dealer to get card id based on input
     * example: QD = queen of diamonds
     * really weird order for deck, easiest just to do if statements
     *
     * @param cardName name of card from input
     * @return corresponding card id
     */
    public Card getID(String cardName) {
        if (cardName.equals("SH")) return new Card(Value.SEVEN, Suit.HEARTS, 0);
        if (cardName.equals("SS")) return new Card(Value.SEVEN, Suit.SPADES, 1);
        if (cardName.equals("SC")) return new Card(Value.SEVEN, Suit.CLUBS, 2);
        if (cardName.equals("EH")) return new Card(Value.EIGHT, Suit.HEARTS, 3);
        if (cardName.equals("ES")) return new Card(Value.EIGHT, Suit.SPADES, 4);
        if (cardName.equals("EC")) return new Card(Value.EIGHT, Suit.CLUBS, 5);
        if (cardName.equals("NH")) return new Card(Value.NINE, Suit.HEARTS, 6);
        if (cardName.equals("NS")) return new Card(Value.NINE, Suit.SPADES, 7);
        if (cardName.equals("NC")) return new Card(Value.NINE, Suit.CLUBS, 8);
        if (cardName.equals("KH")) return new Card(Value.KING, Suit.HEARTS, 9);
        if (cardName.equals("KS")) return new Card(Value.KING, Suit.SPADES, 10);
        if (cardName.equals("KC")) return new Card(Value.KING, Suit.CLUBS, 11);
        if (cardName.equals("TH")) return new Card(Value.TEN, Suit.HEARTS, 12);
        if (cardName.equals("TS")) return new Card(Value.TEN, Suit.SPADES, 13);
        if (cardName.equals("TC")) return new Card(Value.TEN, Suit.CLUBS, 14);
        if (cardName.equals("AH")) return new Card(Value.ACE, Suit.HEARTS, 15);
        if (cardName.equals("AS")) return new Card(Value.ACE, Suit.SPADES, 16);
        if (cardName.equals("AC")) return new Card(Value.ACE, Suit.CLUBS, 17);
        if (cardName.equals("SD")) return new Card(Value.SEVEN, Suit.DIAMONDS, 18);
        if (cardName.equals("ED")) return new Card(Value.EIGHT, Suit.DIAMONDS, 19);
        if (cardName.equals("ND")) return new Card(Value.NINE, Suit.DIAMONDS, 20);
        if (cardName.equals("KD")) return new Card(Value.KING, Suit.DIAMONDS, 21);
        if (cardName.equals("TD")) return new Card(Value.TEN, Suit.DIAMONDS, 22);
        if (cardName.equals("AD")) return new Card(Value.ACE, Suit.DIAMONDS, 23);
        if (cardName.equals("JD")) return new Card(Value.JACK, Suit.DIAMONDS, 24);
        if (cardName.equals("JH")) return new Card(Value.JACK, Suit.HEARTS, 25);
        if (cardName.equals("JS")) return new Card(Value.JACK, Suit.SPADES, 26);
        if (cardName.equals("JC")) return new Card(Value.JACK, Suit.CLUBS, 27);
        if (cardName.equals("QD")) return new Card(Value.QUEEN, Suit.DIAMONDS, 28);
        if (cardName.equals("QH")) return new Card(Value.QUEEN, Suit.HEARTS, 29);
        if (cardName.equals("QS")) return new Card(Value.QUEEN, Suit.SPADES, 30);
        if (cardName.equals("QC")) return new Card(Value.QUEEN, Suit.CLUBS, 31);
        else return new Card(Value.QUEEN, Suit.CLUBS, -1); //no card found

    }

    /**
     * prints out contents of deck
     */
    public void printDeck() {
        for (Card c : deck) {
            System.out.print(c.id());
            c.printCard();
        }
        System.out.println("------------");
    }


}
