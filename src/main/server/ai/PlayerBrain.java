package main.server.ai;

import main.server.model.*;
import main.server.scorekeeper.HandHistory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dave on 10/1/2015.
 * operates as the brain for the player
 * main.protocols.PlayerData class feeds information into brain and brain makes decisions
 */
public class PlayerBrain {
    private AIPersonalities traits;
    private int pickUpThreshold;

    public PlayerBrain(Trait trait1, Trait trait2) {
        traits = new AIPersonalities(trait1, trait2);
        pickUpThreshold = 460; //arbitrary value of points * power in hand
    }

    /**
     * called by main.protocols.PlayerData when deciding to pick up before hand
     *
     * @param h current Hand
     * @return true if want to pick up, false otherwise
     */
    public boolean chooseToPickUp(Hand h) {
        int x = 0; //hand power
        for (Card c : h.getHand()) {
            x += c.id();
        }
        double ratioWinLoss = (9.53426 * Math.pow(10,-8)) * Math.pow(x,4)
                -0.0000186422 * Math.pow(x,3)
                +0.00110236 * Math.pow(x,2)
                -0.00875964 * x
                -0.0568146;

        if (ratioWinLoss < 5 && (traits.is(Trait.GREASY_FINGERS) || traits.is(Trait.MAUER)))
            return false;
        if (traits.is(Trait.STICKY_FINGERS) && ratioWinLoss > 0.3)
            return true;

        return ratioWinLoss > 1;
    }

    /**
     * decides for main.protocols.PlayerData if wise to call up
     *
     * @return false to call up, return true to play alone
     */
    public boolean playAlone(Hand h) {
        int handPower = 0;
        for (Card c : h.getHand()) {
            handPower += c.id();
        }
        if (traits.is(Trait.LONE_WOLF) && handPower > 160) return true;//about 20% chance
        return (handPower > 173); //about 3% chance
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
                if (c.id() <= lowestPower)
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
     * chooses card to play based on main.protocols.PlayerData factors
     *
     * @param playerHand   current main.protocols.PlayerData hand
     * @param cardLed      card led in hand. default = QC w/ id of -1
     * @param partner      is player on partner team?
     * @param isLeaster    is curr hand leaster
     * @param knownPartner is partner known from table (null if not)
     * @param currHand     hand history from table of current hand
     * @return Card to be played
     */
    public Card cardToPlay(Hand playerHand, Card cardLed, boolean partner,
                           boolean isLeaster, Player knownPartner, HandHistory currHand) {
        if (cardLed.id() == -1) //first card played
            return leadHand(playerHand, partner);

        //not first card played
        Suit suitLed = cardLed.getCardSuit();
        Card toPlay;

        //artifically make diamonds led if trump led
        if (cardLed.isTrump()) {
            suitLed = Suit.DIAMONDS;
        }
        if (playerHand.contains(suitLed)) { //suit in hand
            toPlay = chooseWithinSuit(suitLed, playerHand, isLeaster, knownPartner, currHand,partner);
        } else {//suit not in hand
            toPlay = playWithoutSuit(playerHand, partner, isLeaster, knownPartner, currHand);
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
     * @return Card to lead hand
     */
    private Card leadHand(Hand playerHand, boolean partner) {
        Card highestC = null, lowestC = null;
        int highest = -1;
        int lowest = 32;
        for (Card c : playerHand.getHand()) {
            if (c.id() > highest) {
                highest = c.id();
                highestC = c;
            }
            if (c.id() < lowest) {
                lowest = c.id();
                lowestC = c;
            }
        }
        if (partner) return highestC;
        return lowestC;
    }

    /**
     * helper method for intern cardToPlay()
     * chooses best card within given suit to play
     *
     * @param s            suitled
     * @param isLeaster    currroudn = leaster?
     * @param knownPartner current partner known by table
     * @param hh           hand history of hand
     * @return Card to be played
     * @throws IllegalStateException suit not in hand
     */
    private Card chooseWithinSuit(Suit s, Hand h, boolean isLeaster, Player knownPartner, HandHistory hh,
                                  boolean partner) {
        List<Card> possibleCards = new ArrayList<Card>(5);
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
        if (isLeaster) { //return least powerful card in suit
            return h.lowestCard(possibleCards);
        }
        //not leaster
        if (hh.topCard().id() > 27) { //top player likely going to win
            if((partner && knownPartner==hh.topPlayer()) || (!partner && knownPartner != hh.topPlayer())){
                //true if on winning team
                return smear(possibleCards,h); //smear
            }
        }
        //top player unknown..
        return h.middleCard(possibleCards);

    }

    /**
     * Chooses card to play if suit led not in hand
     *
     * @param h            player hand
     * @param partner      is player on partner team
     * @param isLeaster    curr round is leaster
     * @param knownPartner Partner known by table
     * @param hh           current hand history
     * @return card to play
     */
    private Card playWithoutSuit(Hand h, boolean partner, boolean isLeaster, Player knownPartner, HandHistory hh) {
        if (isLeaster) return h.lowestCard(null); //leaster
        if (hh.topCard().id() > 27) { //top player likely going to win
            if((partner && knownPartner==hh.topPlayer()) || (!partner && knownPartner != hh.topPlayer())){
                //true if on winning team
                return smear(h.getHand(),h); //smear
            }
        } //partner unknown by table
        if (partner && h.containsTrump()) //if you are on partner team and have trump
            return chooseWithinSuit(Suit.DIAMONDS, h, isLeaster, knownPartner, hh,partner); //play trump card

        return h.middleCard(null); //otherwise play it safe
    }

    /**
     * throws highest possible point value into round as changes of
     * team winning are high
     *
     * @param possibleCards possible options to play
     * @param h player Hand
     * @return Card to play
     * @throws IndexOutOfBoundsException if no cards to choose from
     */
    private Card smear(List<Card> possibleCards,Hand h) {
        if (possibleCards.isEmpty())
            throw new IndexOutOfBoundsException("no cards to choose from in smear()");
        int highest = -1;
        Card toPlay = null;
        for (Card c : possibleCards) {
            if (c.getPointValue() > highest && c.id() < 24) {//high points but low power
                highest = c.getPointValue();
                toPlay = c;
            }
        }
        if (toPlay == null) {//no smear card found
            return h.middleCard(possibleCards);
        }
        return toPlay;
    }
}

class AIPersonalities {
    private Trait trait1, trait2;

    /**
     * AIPersonalities is a way of holding and using traits in the player brain class
     *
     * @param trait1 trait associated with main.protocols.PlayerData
     * @param trait2 trait associated with main.protocols.PlayerData
     */
    public AIPersonalities(Trait trait1, Trait trait2) {
        this.trait1 = trait1;
        this.trait2 = trait2;
    }

    /**
     * used by main.protocols.PlayerData brain
     * checks if this brain is a certain trait
     *
     * @param t trait to check
     * @return true if containts trait, false otherwise
     */
    public boolean is(Trait t) {
        return t.equals(trait1) || t.equals(trait2);
    }


}
