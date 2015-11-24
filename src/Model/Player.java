package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dave on 9/15/2015.
 * Player functins as both AI and Non-AI players
 * the 5 main game players are held in an array in Game players[]
 * all functions are called by Game at appropriate time during round
 * all decision making functions are operated by PlayerBrain
 */
public class Player {
    private Hand hand;
    private String username;
    private Table table;
    private Card[] blind;
    private Card[] buried;
    private int points,score,numberPickedUp, numberPlayAlone,totalPoints,gamesWon,ableToPlayAlone;
    private boolean pickedUp;
    private boolean onPartnerTeam;
    private boolean isPlayer;
    private PlayerBrain brain;
    private int playerID;
    private boolean playAlone;
    private boolean isNonAIPlayer;

    public Player(String username, int playerID, Table table, boolean isPlayer, Trait trait1) {
        this.username = username;
        this.table = table;
        points=score=numberPickedUp=numberPlayAlone = totalPoints=gamesWon=ableToPlayAlone= 0;
        onPartnerTeam = false;
        blind = null;
        pickedUp = false;
        this.isPlayer = isPlayer;
        brain = new PlayerBrain(trait1, Trait.Normal_Player);
        this.playerID = playerID;
        playAlone=false;
        isNonAIPlayer=false;

    }

    /**
     * 'one stop shop' method for aiplayers in choosing to pick up blind
     * if blind is chosen, adds blind to hand and calls buryCards()
     * if the J of D is in hand or blind, calls up to next highest card
     * used only for Ai players looking to 'choose' if to pick up
     *
     * @return true if blind picked up, otherwise false
     */
    public boolean chooseToPickUp() {
        if (brain.chooseToPickUp(hand)) {
            pickUpBlind();
            chooseToCallUp();
            buryCards(brain.toBury(hand));
            return true;
        }
        return false; //blind not picked up

    }

    /**
     * called by non-player in Game when chooses to pick up
     * also called internally as a helper
     *
     * @return success
     */
    public boolean pickUpBlind() {
        pickedUp = true;
        numberPickedUp++;
        onPartnerTeam = true;
        blind = table.pickUpBlind(username);
        hand.addBlindtoHand(blind);
        return true;
    }

    /**
     * called by non-ai player in game after choosing to pick up blind
     * helper function for buryCards(List<Card>)
     *
     * @param c1 Card one to bury
     * @param c2 Card two to bury
     * @return success
     */
    public boolean buryCards(Card c1, Card c2) {
        List<Card> toBury = new ArrayList<Card>(2);
        toBury.add(c1);
        toBury.add(c2);
        buryCards(toBury);
        return true;
    }

    /**
     * makes player call up if J of D already in hand
     *
     * @return success
     */
    public boolean chooseToCallUp() {
        if(hand.contains(24)){ //contains j of d
            ableToPlayAlone++;
            if(brain.playAlone(hand)){//choose to play alone
                setPlayAlone();
            }
            else{//choose to call up
                setNotPlayAlone();
            }
        }
        return true;
    }

    /**
     * used as helper for nonAI to
     * notify playing alone
     */
    public void setPlayAlone(){
        numberPlayAlone++;
        playAlone = true;
    }

    /**
     * helper to for nonAI to
     * notify not playing alone
     */
    public void setNotPlayAlone(){
        table.callUp(username,hand); //notifies table
    }


    /**
     * internal helper function called by chooseToPickUp()
     * puts two chosen cards into blind to be "buried"
     * cards are already removed from hand at this point
     * adds points to hand point value
     *
     * @param toBury list of cards to be buried
     */
    private void buryCards(List<Card> toBury) {
        //buries cards
        blind[0] = toBury.get(0);
        blind[1] = toBury.get(1);
        buried = blind; //copes over cards in blind to buried cards

        //adds to player points
        this.addPoints(blind[0].getPointValue());
        this.addPoints(blind[1].getPointValue());

        //returns buried cards to table
        table.returnBlind(blind);
        blind = null;
    }


    /**
     * called by game for AI player
     * chooses a card to play from Decider
     * 'plays' a card by putting it on table
     * removes it from hand
     *
     * @return true if no errors
     */
    public boolean playCard() {
        Card c = brain.cardToPlay(hand, table.cardLed(), onPartnerTeam, table.isLeasterRound(),
                table.currPartner(),table.getCurrentHand());
        if (!table.validMove(c, hand)) { //not valid move
            System.out.println("INVALID MOVE BY AI");
            throw new IllegalStateException();
        }
        hand.remove(c);
        table.playCard(c, this);
        return true;
    }

    /**
     * called by game
     * plays given card chosen by non-AI player
     * assuming already checked is valid move by Game
     *
     * @param card card chose by player
     * @return true if valid move, false if not
     */
    public void playCard(Card card) {
        table.playCard(card, this);
        hand.remove(card);
    }

    /**
     * called by game in setupTeams()
     * checks if player has the 'partner card'
     * and sets is partner accordingly
     *
     * @return true if partner, false otherwise
     */
    public boolean checkIsPartner() {
        if (playAlone) return true; //playing alone = both picker and partner
        if (hand.contains(table.getPartnerCard())) {
            onPartnerTeam = true;
            return true;
        }
        return false;
    }

    /**
     * called by game after round has been played
     * reinitializes variables
     */
    public void endRound() {
        pickedUp = false;
        playAlone = false;
        onPartnerTeam = false;
        hand.getHand().clear();
        totalPoints+=points;
        points = 0;
        buried = null;
    }

    /**
     * @override equals()
     * @param other player
     * @return true if players are same, false otherwise
     */
    public boolean equals(Player other) {
        return playerID == other.id();
    }


    /*
        getters and setters
     */

    public boolean hasBlitzers() {
        return (hand.contains(31) && hand.contains(30));
    }

    public int id() {
        return playerID;
    }

    public boolean isPlayer() {
        return isPlayer;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public int getScore() {
        return score;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public int getPoints() {
        return points;
    }

    public boolean pickedUp() {
        return pickedUp;
    }

    public void dealHand(Hand h) {
        hand = h;
    }

    public boolean isOnPartnerTeam() {
        return onPartnerTeam;
    }

    public String getUsername() {
        return username;
    }

    public Hand getHand(){
        return hand;
    }

    public void incrAbleToPlayAlone(){
        ableToPlayAlone++;
    }

    public void setNonAIPlayer() {
        isNonAIPlayer=true;
    }

    public boolean isNonAIPlayer() {
        return isNonAIPlayer;
    }

    /**
     * prints player hand
     * @param printBuried should burried cards be printed?
     */
    public void printHand(boolean printBuried) {
        if (pickedUp && printBuried) {
            System.out.print("\tburied: " + buried[0].getCardValue() + " of " + buried[0].getCardSuit());
            System.out.println(" and " + buried[1].getCardValue() + " of " + buried[1].getCardSuit());
        }
        hand.printHand();
    }

    public void incrGameWon(){
        gamesWon++;
    }

    public void printDetailedStats(int gamesPlayed){
        System.out.println(" | Player Name : " + username);
        System.out.println(" | score: " + score);
        System.out.println(" | winning percentage:  " + (float) gamesWon / gamesPlayed * 100 + "%");
        System.out.println(" | percentage picked up: " + (float) numberPickedUp / gamesPlayed * 100 + "%");
        System.out.println(" | percentage played alone: " + (float) numberPlayAlone / ableToPlayAlone * 100 + "%");
        System.out.println(" | total points: " + totalPoints + "\n");
    }

    public int getNCards(){
        return hand.numberCardinHand();
    }
}
