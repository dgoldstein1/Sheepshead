package server.scorekeeper;


import server.ai.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dave on 9/15/2015.
 * a round is 6 hands. Each hand is stored in List<HandHistory>
 */
public class Round {
    public int roundNumber;
    public int picker, partner;
    public List<HandHistory> hands;
    public Player[] players;
    private boolean leaster;
    int[] playerScores;

    public Round(int roundNumber, Player[] players) {
        this.players = players;
        this.roundNumber = roundNumber;
        hands = new ArrayList<HandHistory>();
        playerScores = new int[5];
    }


    /**
     * checks if either team has reached shnider (reached 30 points)
     *
     * @return true if either team has, false otherwise
     */
    public boolean shniderReached() {
        int partnerPoints = players[picker].getPoints() + players[partner].getPoints();
        return (partnerPoints > 30 && partnerPoints < 90);
    }

    /**
     * checks if either team has achieved at least one trick
     *
     * @return either team has no points
     */
    public boolean trickless() {
        int partnerPoints = players[picker].getPoints() + players[partner].getPoints();
        return (partnerPoints == 0 || partnerPoints == 120);
    }

    /*
     getters and setters
     */

    /**
     * compiles points of partner team
     *
     * @return points of partner team
     */
    public int getPartnerTeamPoints() {
        return players[partner].getPoints() + players[picker].getPoints();
    }

    /**
     * converts player into int in variable player
     *
     * @param picker protocols.PlayerData who picked up
     */
    public void setPicker(Player picker) {
        this.picker = -1;
        int i = 0;
        for (Player p : players) {
            if (p.equals(picker)) this.picker = i;
            i++;
        }

        if (this.picker == -1) {
            System.out.println("PICKER NOT INITIALIZED");
            System.exit(1);
        }
    }

    /**
     * sets partner to given player
     *
     * @param partner protocols.PlayerData partner
     */
    public void setPartner(Player partner) {
        this.partner = -1;
        int i = 0;
        for (Player p : players) {
            if (p.equals(partner)) this.partner = i;
            i++;
        }
        if (this.partner == -1) this.partner = this.picker; //playing alone
    }

    /**
     * called by scoreBoard to get lowest non zero player
     *
     * @return protocols.PlayerData winner
     * @throws IndexOutOfBoundsException no lowest score found
     */
    public Player getLeasterWinner() {
        int lowestNonZero = 121;
        Player winner = null;
        for (Player p : players) {
            if (p.getPoints() < lowestNonZero && p.getPoints() != 0) {
                lowestNonZero = p.getPoints();
                winner=p;
            }
        }
        if (winner == null) {
            throw new IndexOutOfBoundsException("NO LEASTER WINNER FOUND");
        }
        return winner;
    }

    /**
     * called at end of round
     * tallies up curr points for each player
     * stores in array to be displayed by options panel
     */
    public void createPointHistory(){
        int i = 0;
        for(Player p : players){
            playerScores[i] = p.getScore();
            i++;
        }
    }

    /**
     * sets this round to 'leaster' round
     */
    public void setLeaster() {
        leaster = true;
    }

    public boolean isLeaster() {
        return leaster;
    }

    public int[] getPlayerScores(){
        return playerScores;
    }

}
