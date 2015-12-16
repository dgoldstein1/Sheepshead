package Model;

import Controller.GameObserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;

/**
 * Created by Dave on 9/16/2015.
 */
public class Game {
    private Player[] players;
    private int handSize;
    public Table table;
    private Dealer dealer;
    private ScoreBoard scoreboard;
    private boolean printAll;
    private BufferedReader bufferedReader;
    private Player startRound;
    private GameObserver obs;
    private int currPlayerTurn;

    /**
     * initializes game by setting up scoreboard, dealer, and table
     * automatically set to have real player
     *
     * @param printAll   should the actions of this game be printed to console?
     */
    public Game(boolean printAll, GameObserver obs) {
        handSize = 6;
        table = new Table(printAll);
        bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        initPlayers();
        scoreboard = new ScoreBoard(players);
        dealer = new Dealer(players, table);
        this.printAll = printAll;
        this.obs = obs;
    }

    /**
     * initializes players into players[]
     *
     */
    private void initPlayers() {
        Player[] players = new Player[5];;
        //initialize non-AI player. Always first in array
        String playerName = "David";
        players[0] = new Player(playerName, 0, table, true, Trait.Normal_Player);
        players[0].setNonAIPlayer();


        //initalize AI players
        Trait[] traits = Trait.values();
        Trait t;
        int i;
        for (int playersCreated = 1; playersCreated < 5; playersCreated++) {
            i = (int) ((Math.random()) * (Trait.values().length));
            t = traits[i];
            players[playersCreated] = new Player(t.toString() + " " + playersCreated, playersCreated, table, false, t);
        }
        this.players = players;
        startRound = players[0];
    }


    /**
     * playes one round (six hands)
     * called by run
     */
    public void playRound() {
        if (printAll) System.out.println("---start round---");
        scoreboard.newRound();
        dealer.dealCards(handSize);
        setTeams();
        updateTeams();

        //play hands in round
        for (int hand = 0; hand < 6; hand++) {
            Player winner = playHand();
            shiftPlayers(winner);//sets winner as new leader
            if (printAll) scoreboard.printPoints();
        }
        endRound();
        dealer.collectCards();
        if (printAll) System.out.println("---end round---\n");
    }



    /**
     * choose who picks up blind
     * based on this,
     */
    private void setTeams() {
        for (Player p : players) {
            if (p.isPlayer() && askPlayerToPickUp(p)) {//non ai player and chooses to pick up
                scoreboard.setPicker(p);
                return;
            } else if (!p.isNonAIPlayer() && p.chooseToPickUp()) {//ai player and chooses to pick up
                scoreboard.setPicker(p); //note picker must come before partner
                return;
            }
        }
        //blind not picked up by any player
        System.out.println("Blind not picked up: Leaster will be played!!");
        scoreboard.setLeaster();
        table.setLeaster();
    }

    /**
     * updates partners on scoreboard
     / sets partner and non-partner team in players[]
     */
    private void updateTeams() {
        for (Player p : players) {
            if (p.checkIsPartner()) scoreboard.setPartner(p);
            if (p.hasBlitzers()) scoreboard.setBlitzers(true);
        }
    }

    /**
     * asks player to pick up
     * if true, asks for cards to bury and if want to play alone
     *
     * @return true if picks up, false otherwise
     */
    private boolean askPlayerToPickUp(Player p) {
        if (getPlayerInput("--> pick up blind? y/n: \n", true).equals("y")) {//player chooses to pick up
            p.pickUpBlind();

            if (p.getHand().contains(24)) {//picked up and contains j of d
                p.incrAbleToPlayAlone();
                if (getPlayerInput("--> would you like to call up? y/n \n", true).equals("y")) {//call up
                    p.setNotPlayAlone();
                } else { //not calling up
                    p.setPlayAlone();
                }
            }

            Card c1 = askPlayerCard(p, "--> Choose first card to bury: \n", true);
            p.getHand().remove(c1);
            System.out.print("\t buried card: ");
            c1.printCard();
            Card c2 = askPlayerCard(p, "--> Choose second card to bury: \n", true);
            p.getHand().remove(c2);
            System.out.print("\t buried card: ");
            c2.printCard();
            p.buryCards(c1, c2);
            return true;
        }
        return false;
    }

    /**
     * resets player variables
     * shifts players one seat over for new round
     */
    private void endRound() {
        shiftPlayers(null);
        scoreboard.awardPoints(printAll); //tallies scores and ends round
        if (printAll) {
            scoreboard.printRoundDetails();
            scoreboard.printTeams();
            scoreboard.printScores();
        }
        for (Player p : players) {
            p.endRound();
        }
    }

    /**
     * shifts players[] so that players[0] = firstPlayer
     * or shifts so that new leader is one past old leader
     *
     * @param firstPlayer player to be first player list. If null, automatically shifts one past leader of last round
     * @return success
     */
    private boolean shiftPlayers(Player firstPlayer) {
        Player[] shift = new Player[5];
        boolean shiftNewRound = firstPlayer == null;
        if (shiftNewRound) {
            firstPlayer = startRound;
        }
        for (int j = 0; j < players.length; j++) {//specific shift
            if (players[j].equals(firstPlayer)) { //find target player
                if (shiftNewRound) {
                    j = (j + 1) % 5;  //shift one past who started round last time
                    startRound = players[j]; //set new start round player
                }
                for (int i = 0; i < 5; i++) { //shift players based on target j
                    shift[i] = players[j];
                    j = (j + 1) % 5;
                }
                players = shift;
                return true;
            }
        }

        return false; //player not found
    }

    /**
     * plays one hand (six cards)
     * inits end sequences in table
     *
     * @return Player winner
     */
    private Player playHand() {
        for (int i = 0 ; i < 5 ; i++) {
            currPlayerTurn = i;
            Player p = players[i];
            if (p.isPlayer()) {//ask non-AI to play card until valid
                p.playCard(askPlayerCard(p, "--> Choose card to play: \n", false));
            } else{
                //wait before playing card (emulate thinking)
                playerPause(0);
                p.playCard();
                playerPause(0);
            }
        }
        Player winner = table.getWinner();
        HandHistory hand = table.endHand();
        scoreboard.addHand(hand, printAll); //adds hand including points
        playerPause(0);//pause so player can see cards played
        return winner;
    }

    /**
     * used after game has stopped and is expecting user input
     * @param toBePlayed card to be played by user on return
     */
    private void finishHand(Card toBePlayed){

    }

    /**
     * internal method used to make game pause to make card player visible
     */
    private void playerPause(int time){
        if(time < 0) throw new IndexOutOfBoundsException("negative time set in PLAYER PAUSE");
        try{
            Thread.sleep(700);
        } catch (Exception e){
            System.out.println("PROBLEM WITH PLAYER PAUSE");
            System.exit(1);
        }
    }

    /**
     * internal class
     * reads  input from player
     *
     * @param prompt  prompt to be displayed before player input
     * @param yesOrNo is the expected input a "y/n" prompt
     * @return input
     */
    private String getPlayerInput(String prompt, boolean yesOrNo) {
        String input = null;
        try {
            System.out.print(prompt);
            input = bufferedReader.readLine();
        } catch (IOException e) {
            System.out.println("ERROR READING INPUT");
            e.printStackTrace();
            System.exit(1);
        }
        if (input == null) {//no string entered
            System.out.println("NO STRING ENTERED");
            return getPlayerInput(prompt, yesOrNo);
        }
        if (yesOrNo) {//y/n prompt
            if (!input.equals("y") && !input.equals("n")) {
                System.out.println(input);
                System.out.println("INVALID STRING (not 'y' or 'n')");
                return getPlayerInput(prompt, false);
            }

        }
        return input;
    }

    /**
     * asks player to play a card
     * checks if valid card
     *
     * @param prompt   prompt to be displayed
     * @param buryCard is this asking for a card to be buried
     * @param p        player playing card
     * @return card played
     */
    private Card askPlayerCard(Player p, String prompt, boolean buryCard) {
        String in = getPlayerInput(prompt, false);
        Card toPlay = dealer.getCardID(in);
        try {
            if (toPlay.id() == -1) {//no card found
                throw new IOException("ILLEGAL INPUT / NO CARD FOUND");
            } else if (!p.getHand().contains(toPlay)) //card not in hand
                throw new IOException("Card not in hand");

            else if (!buryCard) {//card played in game
                if (!table.validMove(toPlay, p.getHand()))
                    throw new IOException("illegal move");
            }
        } catch (IOException e) {
            if (e.getMessage() != null)
                System.out.println("\t" + e.getMessage() + " for input " + in);
            return askPlayerCard(p, prompt, buryCard);
        }
        return toPlay;
    }


    /*printers*/

    /**
     * prints out cards held by each player
     */
    private void printPlayerCards() {
        for (Player p : players) {
            System.out.print("---" + p.getUsername() + "---\n");
            if (p.isOnPartnerTeam()) System.out.println(p.getUsername() + " is partner");
            if (p.pickedUp()) System.out.println(p.getUsername() + " picked up");
            p.printHand(true);
            System.out.println("");
        }
    }

    /**
     * prints out stats of game
     * called by run
     */
    public void stats() {
        System.out.println("overall stats: \n");
        System.out.println("\t| rounds played: " + scoreboard.roundsPlayed());
        System.out.println("\t| leaster games played: " + scoreboard.getLeasterCount());
        System.out.println("\t| leaster percentage: " + (float) scoreboard.getLeasterCount() / scoreboard.roundsPlayed() * 100 + "%\n");
        System.out.println("player specific stats: \n" + "\t-----");
        for (Player p : players) {
            p.printDetailedStats(scoreboard.roundsPlayed());
            System.out.println("\t-----");
        }

    }

    public void printResults() {
        scoreboard.printScores();
    }

    /*getters */

    public ScoreBoard getScoreboard() {
        return scoreboard;
    }

    public Player[] getPlayers() {
        return players;
    }

    //return Player[] from scoreboard that does not rotate
    public Player[] getStaticPlayers(){return scoreboard.getNonStaticPlayers();}

    public Table getTable(){
        return table;
    }

    public Player getNonAiPlayer() throws IllegalStateException {
        for(Player p : getPlayers()){
            if(p.isNonAIPlayer()){
                return p;
            }
        }
        throw new IllegalStateException("COULD NOT FIND NON-AI PLAYER");

    }

    /*in from view*/

    public void playerCardPushed(int cardID){
/* todo
        //check if real player turn?
        if(!players[currPlayerTurn].isNonAIPlayer())
            return;

        Card c = dealer.getCard(cardID);
*/

    }



}
