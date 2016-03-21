package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dave on 9/19/2015.
 * stores round histories of all rounds played
 * tallies up points in round for Game
 * only access to this class is Game
 */
public class ScoreBoard {
    private List<Round> scoreboard;
    private Player[] players;
    private Round currRound;
    private int roundNumber;
    private boolean blitzers;
    private int leasterCount;

    public ScoreBoard(Player[] players) {
        scoreboard = new ArrayList<Round>();
        this.players = players;
        roundNumber = 0;
        leasterCount = 0;
        blitzers = false;
    }

    /**
     * creates in new round and adds toscoreboard
     */
    public void newRound() {
        roundNumber++;
        currRound = new Round(roundNumber, players);
        scoreboard.add(currRound);
        blitzers = false;
    }

    /**
     * adds hand and points to current round
     * called by game after hand is over
     *
     * @param hand     Hand played
     * @param printAll should this hand be printed?
     */
    public void addHand(HandHistory hand, boolean printAll) {
        currRound.hands.add(hand);
        Player winner = hand.topPlayer();
        for (Player p : players) { //adds hand points to winner
            if (winner.equals(p)) {
                p.addPoints(hand.getPoints());
                if (printAll)
                    System.out.print("\t" + p.getUsername() + " won this hand for " + hand.getPoints() + " points \n");
            }
        }
    }

    /**
     * talles up points and end of round
     * called by game after each round
     *
     * @param printAll should details be printed?
     */
    public void awardPoints(boolean printAll) {
        int basePoint = 1;

        if(currRound.isLeaster()){
            awardLeasterPoints(basePoint,printAll);
        }
        else{
            awardNormalPoints(basePoint);
        }
        checkScoresBalance();
    }

    /**
     * award points for non-leaster round
     * @param basePoint lowest pointvalue. 1 by default
     */
    private void awardNormalPoints(int basePoint){
        if (!currRound.shniderReached()) basePoint *= 2;
        if (blitzers) basePoint *= 2;
        if (currRound.trickless()) basePoint *= 2;
        if (currRound.getPartnerTeamPoints() >= 60) { //partner team wins
            for (Player p : players) {
                if (p.pickedUp()){
                    p.incrGameWon();
                    p.addScore(basePoint * 2);//picked up
                    if(currRound.partner == currRound.picker) p.addScore(basePoint * 2); //played alone
                }
                else if (p.isOnPartnerTeam()){
                    p.incrGameWon();
                    p.addScore(basePoint);//partner
                }
                else {
                    p.addScore(basePoint * -1);                   //non-partner team (minus score)
                    p.incrGamesLost();
                }
            }
        } else { //majority team won
            for (Player p : players) {
                if (p.pickedUp()){ //picked up
                    p.addScore(basePoint * -2);
                    if(currRound.partner == currRound.picker) p.addScore(basePoint * -2); //played alone
                    p.incrGamesLost();
                }
                else if (p.isOnPartnerTeam()) p.addScore(basePoint * -1);//partner
                else{
                    p.incrGameWon();
                    p.addScore(basePoint);                             //non-partner team
                }
            }
        }
    }

    /**
     * special case when no one picks up blind
     * winner is player with lowest score where score != 0
     * shneider / trickless / playalone do not factor in
     * @param basePoint lowester point value. 1 by default.
     */
    private void awardLeasterPoints(int basePoint,boolean printAll){
        Player winner = currRound.getLeasterWinner();
        winner.incrGameWon();
        if(printAll)System.out.println("Player " + winner.getUsername() + " won leaster with " + winner.getPoints() + " points");
        for(Player p : players){
            if(winner == p) {
                p.addScore(basePoint * 4);
            }
            else{
                p.addScore(basePoint*-1);
                p.incrGamesLost();
            }
        }
    }

    private void checkScoresBalance(){
        //checks that scores are balanced
        int score = 0;
        for(Player p : players){
            score+=p.getScore();
        }
        if (score != 0) {
            System.out.println("GAME SCORES DO NOT BALANCE OUT TO ZERO:" + score);
            printScores();
            throw new IllegalStateException();
        }
        int points = 0;
        for (Player p : players) {
            points += p.getPoints();
        }
        if (points != 120 && !currRound.isLeaster()) {//leaster is dumb
            System.out.println("TOTAL NOT EQUAL TO 120 POINTS" + points);
            printPoints();
            throw new IllegalStateException();
        }

    }


    /* printers */


    /**
     * prints points for each player in current round
     */
    public void printPoints() {
        for (Player p : players)
            System.out.print(" | " + p.getUsername() + ": " + p.getPoints());
        System.out.println("\n");
    }

    //double checks round points for round total to 120
    //prints overall game scores of each player

    /**
     * prints overall game scores for each player
     * exits if points do not add up to 120 in round or
     * total game points do not balance to zero
     */
    public void printScores() {
        System.out.println("\nplayer scores: \n");
        for (Player p : players) {
            System.out.print(" | " + p.getUsername() + ": " + p.getScore());
        }

        System.out.println("");
    }

    /**
     * prints out details of round
     */

    public void printRoundDetails(){
        if(!currRound.isLeaster()){
            System.out.println("round stats:");
            System.out.println("\n\tshnider reached? " + currRound.shniderReached());
            System.out.println("\tbitz & punish? " + blitzers);
            System.out.println("\ttrickless? " + currRound.trickless() + "\n");
        }
    }

    /**
     * prints player teams. Used at end of the game, called by printScores()
     */
    public void printTeams(){
        if(currRound.isLeaster()) return; //no teams for leaster
        System.out.print("\tpartner team:  \t ");
        for(Player p : players){
            if(p.isOnPartnerTeam()) System.out.print(" " + p.getUsername() + " | ");
        }
        System.out.println("");
        System.out.print("\tnon-partner team: ");
        for(Player p: players){
            if(!p.isOnPartnerTeam()) System.out.print(" " + p.getUsername() + " | ");
        }
        System.out.println("");

    }


    /*getters and setters*/


    public void setLeaster(){
        leasterCount++;
        currRound.setLeaster();
    }

    public void setBlitzers(boolean blitz) {
        blitzers = blitz;
    }

    public void setPicker(Player p) {
        currRound.setPicker(p);
    }

    public void setPartner(Player p) {
        currRound.setPartner(p);
    }

    public int getLeasterCount(){return leasterCount;}

    public int roundsPlayed(){return scoreboard.size();}
}
