package main.server.model;

import java.util.ArrayList;
import java.util.List;

/**
 * cards held by each player
 * different from hand history, which is history of hand played
 * Created by Dave on 9/15/2015.
 */
public class Hand {
    private List<Card> hand;
    int handSize;

    public Hand(int handSize) {
        hand = new ArrayList<Card>();
        this.handSize = handSize;
    }

    public void addCard(Card c) {
        hand.add(c);
    }


    /**
     * helper method for PlayCard
     * called if cannot find appropriate suit in Hand
     */

    public Card randomCard() {
        int i = (int) Math.random() * hand.size();
        return hand.get(i);
    }

    /**
     * gets middle card
     *
     * @return Card middle
     * @throws IllegalStateException
     */
    public Card middleCard(List<Card> possibilities) {
        if (possibilities == null) possibilities = hand;
        int mid = 15, closetToMid = mid + 2;
        Card midC = null;
        for (Card c : possibilities) {
            if (Math.abs(mid - c.id()) < closetToMid) {
                closetToMid = Math.abs(mid - c.id());
                midC = c;
            }
        }
        if (midC == null) {
            throw new IllegalStateException("no card found in middleCard()");
        }
        return midC;
    }

    /**
     * gets lowest card
     *
     * @param possibilities list of options to choose from, default = null
     * @return lowest card in possibilites
     */
    public Card lowestCard(List<Card> possibilities) {
        if (possibilities == null) possibilities = hand;
        Card toPlay = null;
        int lowestPoints = 32;
        for (Card c : possibilities) {
            if (c.id() < lowestPoints) {
                toPlay = c;
                lowestPoints = c.id();
            }
        }
        return toPlay;
    }

    /*
        getters + setters
     */

    //prints contents of hand
    public void printHand() {
        for (Card c : hand) {
            System.out.print("\t");
            c.printCard();
        }
    }

    /**
     * Checks if any card in hand is of given suit
     *
     * @param s Suit to check
     * @return true if contains given suit, false otherwise
     */
    public boolean contains(Suit s) {
        if (s == Suit.DIAMONDS) { //special case for trump
            for (Card card : hand) {
                if (card.isTrump())
                    return true;
            }
            return false;
        }
        for (Card card : hand) { //fail
            if (card.getCardSuit().equals(s) && !card.isTrump())
                return true;
        }
        return false;
    }

    /**
     * check if hand has trump
     *
     * @return true if yes, no otherwise
     */
    public boolean containsTrump() {
        for (Card c : hand) {
            if (c.isTrump()) return true;
        }
        return false;
    }

    /**
     * checks if Card c is in hand
     *
     * @param c Card in hand
     * @return true if in hand, false otherwise
     */
    public boolean contains(Card c) {
        for (Card card : hand) {
            if (c.getCardSuit().equals(card.getCardSuit()) && c.getCardValue().equals(card.getCardValue()))
                return true;
        }
        return false;
    }

    /**
     * checks if hand contains given card
     *
     * @param id Card id
     * @return true if in hand, false otherwise
     */
    public boolean contains(int id) {
        for (Card c : hand) {
            if (c.id() == id) return true;
        }
        return false;
    }

    public List<Card> getHand() {
        return hand;
    }

    /**
     * removes target card from hand
     *
     * @return true if success, false otherwise
     */
    public boolean remove(Card c) {
        for (int i = 0; i < hand.size(); i++) {
            if (c.equals(hand.get(i))) {
                hand.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * called by main.protocols.PlayerData after deciding to pick up blind
     * adds blind to hand to make cards viewable and removes from blind
     *
     * @return success
     */
    public boolean addBlindtoHand(Card[] blind) {
        //adds to hand
        for (int i = 0; i < 2; i++) {
            hand.add(blind[i]);
        }
        for (int i = 0; i < 2; i++) {
            blind[i] = null;
        }
        if (blind.length != 0) return false;
        return true;

    }

    /**
     * returns card given id
     *
     * @param id Card id (int 0-31)
     * @return Card, else exits if out of range
     */
    public Card getCard(int id) {
        for (Card c : hand) {
            if (c.id() == id)
                return c;
        }
        System.out.println("NO CARD FOUND IN HAND id : " + id);
        System.exit(1);
        return null; //unreachable code
    }

    /**
     * getter for number of cards curr in hand (different from hand size)
     * @return #cards in hand
     */
    public int numberCardinHand(){
        return hand.size();
    }

}
