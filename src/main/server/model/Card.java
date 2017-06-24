package main.server.model;

/**
 * Created by Dave on 9/15/2015.
 * <p/>
 * Card is a basic unit that makes up deck
 * Each card has an id, which is also it's 'power', ie 8 > 7
 * power goes by suit first and then number
 */
public class Card {
    private Value value;
    private Suit suit;
    private int id;

    public Card(Value value, Suit suit, int id) {
        this.value = value;
        this.suit = suit;
        this.id = id;
    }

    /**
     * gets the point value of this card given face value
     *
     * @return point value of card
     */
    public int getPointValue() {
        if (value.equals(Value.ACE)) return 11;
        if (value.equals(Value.TEN)) return 10;
        if (value.equals(Value.KING)) return 4;
        if (value.equals(Value.QUEEN)) return 3;
        if (value.equals(Value.JACK)) return 2;
        else return 0;
    }


    public void printCard() {
        System.out.println("" + value + " of " + suit);
    }

    public boolean greaterThan(Card c) {
        return id > c.id();
    }

    public boolean equals(Card c) {
        return id == c.id();
    }

    //getters and setters
    public Value getCardValue() {
        return value;
    }

    public int id() {
        return id;
    }

    public Suit getCardSuit() {
        return suit;
    }

    public boolean isTrump() {
        return value == Value.QUEEN || value == Value.JACK || suit == Suit.DIAMONDS;
    }

    @Override
    public String toString(){
        return ("" + value + " of " + suit);
    }

}
