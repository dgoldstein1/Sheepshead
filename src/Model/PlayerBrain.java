package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dave on 10/1/2015.
 * operates as the brain for the player
 * Player class feeds information into brain and brain makes decisions
 */
public class PlayerBrain {
    private AIPersonality trait1, trait2;

    public PlayerBrain(AIPersonality trait1, AIPersonality trait2) {
        this.trait1 = trait1;
        this.trait2 = trait2;
    }

    /**
     * called by Player when deciding to pick up before hand
     * player set to pick up if 1) more than 3 trump and 2) more than 20 points
     * @param h current Hand
     * @return true if want to pick up, false otherwise
     */
    public boolean chooseToPickUp(Hand h) {
        boolean stickyFingers = trait1.equals(AIPersonality.STICKY_FINGERS) || trait2.equals(AIPersonality.STICKY_FINGERS);
        int powerInHand = 0;
        for (Card c : h.getHand()) {
            powerInHand += c.getId();
        }
        return (stickyFingers && powerInHand > 60) || (powerInHand > 100);
    }

    /**
     * decides for Player if wise to call up
     *
     * @return true to call up, return false to play alone
     */
    public boolean playAlone(Hand h) {
        boolean aggressive = trait1==AIPersonality.AGGRESSIVE || trait2==AIPersonality.AGGRESSIVE;
        if (aggressive)
            return true;
        int trumpCount = 0;
        int handPower = 0;
        for (Card c : h.getHand()) {
            if (c.isTrump()) trumpCount++;
            handPower += c.getId();
        }
        return (trumpCount >= 4 && handPower > 100);
    }

    /**
     * after a player has decide to call up, chooses which card to call up to
     *
     * @param h hand
     * @return Card to Call up to
     */
    public int callUp(Hand h) {
        for (int i = 25; i < 30; i++) { //start at j of h
            if (!h.contains(i)) {
                return i;
            }
        }
        return 30; //player has JD -> QC
    }

    /**
     * chooses which cards to bury after looking at blind
     * currently set to bury cards of two lowest power
     *
     * @param h current Hand
     * @return List<Cards> to bury
     */
    public List<Card> toBury(Hand h) { //todo
        List<Card> toBury = new ArrayList<Card>(2);
        int lowestPower = 0;
        Card lowest = new Card(Value.QUEEN, Suit.CLUBS, -1); //placeholder to initialize

        //removes lowest power card twice
        for (int i = 0; i < 2; i++) {
            for (Card c : h.getHand()) {
                if (c.getId() <= lowestPower)
                    lowestPower = c.getPointValue();
                lowest = c;
            }
            toBury.add(lowest);
            h.remove(lowest);
            lowestPower = 0;
        }

        return toBury;
    }

    /**
     * chooses card to play based on Player factors
     *
     * @param cardsPlayed cards on the table
     * @param h           current Player hand
     * @param cardLed     card led in hand. default = QC w/ id of -1
     * @param partner     is player on partner team?
     * @param points      current player points
     * @return Card to be played
     */
    public Card cardToPlay(List<Card> cardsPlayed, Hand h, Card cardLed, boolean partner, int points, boolean isLeaster) {
        if (cardLed.getId() == -1) //first card played
            return leadHand(h, partner, points);

        //not first card played
        Suit suitLed = cardLed.getCardSuit();
        Card toPlay;

        //artifically make diamonds led if trump led
        if (cardLed.isTrump()) {
            suitLed = Suit.DIAMONDS;
        }
        if (h.contains(suitLed)) { //suit in hand
            toPlay = chooseWithinSuit(cardsPlayed, suitLed, h, partner, points, isLeaster);
        } else {
            toPlay = playWithoutSuit(cardsPlayed, h, partner, points, isLeaster); //suit not in hand
        }

        //no card selected
        if (toPlay == null) {
            System.out.println("COULD NOT SET CARD TO PLAY");
            System.exit(1);
        }
        return toPlay;
    }

    /**
     * chooses card to lead hand
     * the highest card to lead if partner, lowest card otherwise
     *
     * @param playerHand player hand
     * @param partner    is partner
     * @param points     player points
     * @return Card to lead hand
     */
    private Card leadHand(Hand playerHand, boolean partner, int points) {
        Card toPlay = null;
        if (partner) {
            int highest = -1;
            for (Card c : playerHand.getHand()) {
                if (c.getId() > highest) {
                    highest = c.getId();
                    toPlay = c;
                }
            }
            return toPlay;
        }
        //player is not partner, find lowest card in hand
        int lowest = 32;
        for (Card c : playerHand.getHand()) {
            if (c.getId() < lowest) {
                lowest = c.getId();
                toPlay = c;
            }
        }
        return toPlay;
    }

    /**
     * helper method for intern cardToPlay()
     * chooses best card within given suit to play
     *
     * @param cardsPlayed cards played so far
     * @param s           suitled
     * @param isLeaster   currroudn = leaster?
     * @return Card to be played
     * @throws IllegalStateException suit not in hand
     */
    private Card chooseWithinSuit(List<Card> cardsPlayed, Suit s, Hand h, boolean partner, int points, boolean isLeaster) {
        List<Card> possibleCards = new ArrayList<Card>(6);
        if (s == Suit.DIAMONDS) { //special case for diamonds led
            for (Card c : h.getHand()) {
                if (c.isTrump())
                    possibleCards.add(c);
            }
        }
        for (Card c : h.getHand()) { //fail
            if ((c.getCardSuit() == s) && (!c.isTrump()))
                possibleCards.add(c);
        }

        if (possibleCards.isEmpty()) throw new IllegalStateException("COULD NOT FIND PROPER SUIT ");
        Card toPlay = null;
        if (isLeaster) { //return least powerful card in suit
            int lowestPoints = 32;
            for (Card c : possibleCards) {
                if (c.getId() < lowestPoints) {
                    toPlay = c;
                    lowestPoints = c.getId();
                }
            }
        } else { //not leaster
            int highestPoints = -1;
            for (Card c : possibleCards) {
                if (c.getId() > highestPoints) {
                    toPlay = c;
                    highestPoints = c.getId();
                }
            }
        }
        if (toPlay == null) throw new IllegalStateException("NO LOWEST / HIGHEST CARD FOUND");
        return toPlay;
    }

    /**
     * Chooses card to play if suit led not in hand
     *
     * @param cardsPlayed cards played so far
     * @param h           player hand
     * @param partner     is player on partner team
     * @param points      points in player
     * @param isLeaster   curr round is leaster
     * @return card to play
     */
    private Card playWithoutSuit(List<Card> cardsPlayed, Hand h, boolean partner, int points, boolean isLeaster) {
        return h.randomCard(); //todo
    }

    /**
     * checks if player is playing a valid card
     *
     * @param cardToPlay Card to Play
     * @param h          current Hand
     * @param cardLed    first card played in hand
     * @return true if valid move, false if invalid
     */
    public boolean validMove(Card cardToPlay, Hand h, Card cardLed) {
        Suit suitLed = cardLed.getCardSuit();
        Suit playedSuit = cardToPlay.getCardSuit();
        if (cardLed.isTrump())
            suitLed = Suit.DIAMONDS; // make suit diamonds if trump led
        if (cardLed.isTrump())
            playedSuit = Suit.DIAMONDS; //make suit diamonds if card is trump

        if (!h.contains(cardToPlay)) return false; //not in hand
        if (cardLed.getId() != -1) { //not first card led
            if (h.contains(suitLed)) { //suit is in hand
                if (playedSuit != suitLed) { //trump played and trump not led or vice versa
                    System.out.print("DOES NOT FOLLOW SUIT. Suit led: " + cardLed.getCardSuit());
                    System.out.print(", but card played: ");
                    cardToPlay.printCard();
                    return false; //not of same suit
                }
            }
        }
        return true;
    }

}
