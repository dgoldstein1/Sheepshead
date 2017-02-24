package server;

import client.options.LogType;

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
//    private ModelObserver obs;
//
    public ScoreBoard(Player[] players) {
//        this.obs = obs;
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
     */
    public void addHand(HandHistory hand) {
        currRound.hands.add(hand);
        Player winner = hand.topPlayer();
        for (Player p : players) { //adds hand points to winner
            if (winner.equals(p)) {
                p.addPoints(hand.getPoints());
//                obs.log(this.getClass(), LogType.INFO, p.getUsername() + " won this hand for " + hand.getPoints() + " points");
            }
        }
    }

    /**
     * talles up points and end of round
     * called by game after each round
     *
     */
    public void awardPoints() {
        int basePoint = 1;

        if (currRound.isLeaster()) {
            awardLeasterPoints(basePoint);
        } else {
            awardNormalPoints(basePoint);
        }
        checkScoresBalance();
        currRound.createPointHistory(); //finalize scores in scoreboard
    }

    /**
     * award points for non-leaster round
     *
     * @param basePoint lowest pointvalue. 1 by default
     */
    private void awardNormalPoints(int basePoint) {
        if (!currRound.shniderReached()) basePoint *= 2;
        if (blitzers) basePoint *= 2;
        if (currRound.trickless()) basePoint *= 2;
        if (currRound.getPartnerTeamPoints() > 60) { //partner team wins
            for (Player p : players) {
                if (p.pickedUp()) {
                    p.incrGameWon();
                    p.addScore(basePoint * 2);//picked up
                    if (currRound.partner == currRound.picker) p.addScore(basePoint * 2); //played alone
                } else if (p.isOnPartnerTeam()) {
                    p.incrGameWon();
                    p.addScore(basePoint);//partner
                } else p.addScore(basePoint * -1);                   //non-partner team (minus score)
            }
        } else { //majority team won
            for (Player p : players) {
                if (p.pickedUp()) { //picked up
                    p.addScore(basePoint * -2);
                    if (currRound.partner == currRound.picker) p.addScore(basePoint * -2); //played alone
                } else if (p.isOnPartnerTeam()) p.addScore(basePoint * -1);//partner
                else {
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
     *
     * @param basePoint lowester point value. 1 by default.
     */
    private void awardLeasterPoints(int basePoint) {
        Player winner = currRound.getLeasterWinner();
        winner.incrGameWon();
//        obs.log(this.getClass(), LogType.INFO, "PlayerData " + winner.getUsername() + " won leaster with " + winner.getPoints() + " points");
        for (Player p : players) {
            if (winner == p) {
                p.addScore(basePoint * 4);
            } else {
                p.addScore(basePoint * -1);
            }
        }
    }

    private void checkScoresBalance() {
        //checks that scores are balanced
        int score = 0;
        for (Player p : players) {
            score += p.getScore();
        }
        if (score != 0) {
//            obs.log(this.getClass(), LogType.ERROR, "GAME SCORES DO NOT BALANCE OUT TO ZERO:" + score);
            throw new IllegalStateException();
        }
        int points = 0;
        for (Player p : players) {
            points += p.getPoints();
        }
        if (points != 120 && !currRound.isLeaster()) {//leaster is dumb
//            obs.log(this.getClass(), LogType.ERROR, "TOTAL NOT EQUAL TO 120 POINTS" + points);
            throw new IllegalStateException();
        }

    }


    /* printers */


    /**
     * prints points for each player in current round
     */
    public void printPoints() {
        String toLog = "";
        for (Player p : players)
            toLog += " | " + p.getUsername() + ": " + p.getPoints();
//        obs.log(this.getClass(), LogType.INFO, toLog);
    }

    //double checks round points for round total to 120
    //prints overall game scores of each player

    /**
     * prints overall game scores for each player
     * exits if points do not add up to 120 in round or
     * total game points do not balance to zero
     */
    public void printScores() {
        String toLog = "";
        for (Player p : players) {
            if(p.isNonAIPlayer())
                toLog += p.getUsername() + ": " + p.getScore(); //do not want "|" on first go around
            else
                toLog += " | " + p.getUsername() + ": " + p.getScore();
        }
//        obs.displayMessage(toLog + "\n \n" + getRoundDetails(), "Round Scores");
    }

    /**
     * prints out details of round
     */

    public String getRoundDetails() {
        String toLog = "";
        toLog += "Partner team: \t";
        for (Player p : players) {
            if (p.isOnPartnerTeam()) toLog += " " + p.getUsername() +  " (" + p.getPoints() + " points) | ";
        }
        toLog+="\n";
        toLog += "Pon-partner team: ";
        for (Player p : players) {
            if (!p.isOnPartnerTeam()) toLog += " " + p.getUsername() + " (" + p.getPoints() + " points) | ";
        }
        if (currRound.isLeaster()) return toLog; //no teams for leaster
        toLog += "\n \n";
        if (!currRound.isLeaster()) {
            toLog += "Shnider Reached: \t" + currRound.shniderReached() + "\n" +
                    "Bitz And Punish: \t" + blitzers + "\n" +
                    "Trickless: \t" + currRound.trickless() + "\n";
        }
        return toLog;
    }


    /*getters and setters*/


    public void setLeaster() {
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

    public int getLeasterCount() {
        return leasterCount;
    }

    public int roundsPlayed() {
        return scoreboard.size();
    }

    public Player[] getNonStaticPlayers() {
        return players;
    }

    public List<Round> getListOfRound() {
        return scoreboard;
    }

    /**
     * @return playerStats of user in format {{name,stat}{name2,stat2}...}
     */
    public String[][] getPlayerStats() {

        String[] categories = {
                "Total Rounds Played",
                "Total Leaster Games Played",
                "Percentage of Games Played Leaster",
                "Overall Score",
                "Win Percentage",
                "Percentage Picked Up",
                "Percentage Played Alone",
                "Total Points"

        };

        Player user = null;
        for (Player p : players) {
            if (p.isNonAIPlayer()) {
                user = p;
                break;
            }
        }
        if (user == null) {
//            obs.log(this.getClass(), LogType.ERROR, "Could not find non-aid player " +
//                    "in retrieving player statistics");
            return new String[][]{{""}, {""}}; //return empty stats

        }

        //stats
        int roundsPlayed = roundsPlayed() - 1;
        String[] stats = {
                roundsPlayed + 1 + "",
                this.getLeasterCount() + "",
                (float) getLeasterCount() / roundsPlayed * 100 + "%",
                user.getScore() + "",
                (float) user.getGamesWon() / roundsPlayed * 100 + "%",
                (float) user.getNumberPickedUp() / roundsPlayed * 100 + "%",
                (float) user.getNPlayAlone() / user.getNAbleToPlayerAlone() * 100 + "%",
                user.getTotalPoints() + ""

        };

        if(categories.length != stats.length){
//            obs.log(this.getClass(), LogType.ERROR, "Size of PlayerData Stats and Categories not equal," +
//                    "initializing empty array..");
            return new String[][]{{""}, {""}}; //return empty stats
        }

        String[][] toReturn = new String[categories.length][stats.length];
        for(int i = 0;i< categories.length;i++){
            toReturn[i] = new String[]{categories[i],stats[i]};
        }

        return toReturn;

    }

    public int currRound(){
        return roundNumber;
    }


}
