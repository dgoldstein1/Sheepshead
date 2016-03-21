package Model;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Dave on 9/16/2015.
 *
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

    /**
     * initializes game by setting up scoreboard, dealer, and table
     *
     * @param printAll   should the actions of this game be printed to console?
     * @param realPlayer is a real player playing with this game?
     */
    public Game(boolean printAll, boolean realPlayer) {
        handSize = 6;
        table = new Table(printAll);
        bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        initPlayers(realPlayer);
        scoreboard = new ScoreBoard(players);
        dealer = new Dealer(players, table);
        this.printAll = printAll;
    }

    /**
     * initializes players into players[]
     *
     * @param realPlayer is a real player playing this game
     */
    private void initPlayers(boolean realPlayer) {
        Player[] players = new Player[5];
        int playersCreated = 1;
        //initialize non-AI player
        if (realPlayer) {
            String playerName = getPlayerInput("--> Enter Player name: \n", false);
            players[0] = new Player(playerName, 0, table, true, Trait.Normal_Player);
            playersCreated++;
        }

        //initalize AI players
        Trait t = Trait.TEST;
        players[playersCreated - 1] = new Player(t.toString(),playersCreated,table,false, t);
        playersCreated++;
        while (playersCreated <= 5) {
            players[playersCreated - 1] = new Player("Player " + playersCreated, playersCreated, table, false, Trait.Normal_Player);
            playersCreated++;
        }
        this.players = players;
        startRound = players[0];
    }


    /**
     * playes one round (six hands)
     * called by run
     */
    public void playRound(int pickUpPercentage) {
        if (printAll) System.out.println("---start round---");
        scoreboard.newRound();
        dealer.dealCards(handSize);
        setTeams();
        updateTeams();

        for(Player p : players){
            if(p.getUsername().equals("TEST")) {
                p.setPercentagePickUp(pickUpPercentage);
                p.recordHandStrength();
            }
        }

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
            } else if (p.chooseToPickUp()) {//ai player
                scoreboard.setPicker(p); //note picker must come before partner
                return;
            }
        }
        //blind not picked up by any player
        if(printAll) System.out.println("Blind not picked up: Leaster will be played!!");
        scoreboard.setLeaster();
        table.setLeaster();



    }

    //updates partners on scoreboard
    //sets partner and non-partner team in players[]
    private void updateTeams(){
        for (Player p : players) {
            if (p.checkIsPartner()) scoreboard.setPartner(p);
            if (p.hasBlitzers()) scoreboard.setBlitzers(true);
        }
    }

    /**
     * asks player to pick up
     * if true, asks for cards to bury and if want to play alone
     * @return true if picks up, false otherwise
     */
    boolean askPlayerToPickUp(Player p){
        if (printAll) { //shows player hand to see if want to pick up
            System.out.println("Your current hand: \n");
            p.printHand(false);
            System.out.print("\n");
        }
        if (getPlayerInput("--> pick up blind? y/n: \n", true).equals("y")) {//player chooses to pick up
            p.pickUpBlind();

            if(p.getHand().contains(24)){//picked up and contains j of d
                p.incrAbleToPlayAlone();
                if(getPlayerInput("--> would you like to call up? \n", true).equals("y")){//call up
                    p.setNotPlayAlone();
                } else{ //not calling up
                    p.setPlayAlone();
                }
            }

            System.out.println("--> your hand is: \n");
            p.printHand(false);
            Card c1 = askPlayerCard(p,"--> Choose first card to bury: \n",true);
            p.getHand().remove(c1);
            System.out.print("\t buried card: ");c1.printCard();
            Card c2 = askPlayerCard(p,"--> Choose second card to bury: \n",true);
            p.getHand().remove(c2);
            System.out.print("\t buried card: ");c2.printCard();
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
        for (Player p : players) {
            if (p.isPlayer()) {//ask non-AI to play card until valid
                System.out.println("--> Your hand is: \n");
                p.printHand(true);
                p.playCard(askPlayerCard(p, "--> Choose card to play: \n", false));
            } else p.playCard();
        }
        Player winner = table.getWinner();
        HandHistory hand = table.endHand();
        scoreboard.addHand(hand, printAll); //adds hand including points
        return winner;
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
     * @param prompt prompt to be displayed
     * @param buryCard is this asking for a card to be buried
     * @param p player playing card
     * @return card played
     */
    private Card askPlayerCard(Player p,String prompt,boolean buryCard) {
        String in = getPlayerInput(prompt, false);
        Card toPlay = dealer.getCardID(in);
        try{
            if (toPlay.id() == -1) {//no card found
                throw new IOException("ILLEGAL INPUT / NO CARD FOUND");
            }
            else if(!p.getHand().contains(toPlay)) //card not in hand
                throw new IOException("Card not in hand");

            else if(!buryCard){//card played in game
                if(!table.validMove(toPlay,p.getHand()))
                    throw new IOException();
            }
        } catch(IOException e){
            if(e.getMessage()!=null)
                System.out.println("\t" + e.getMessage() + " for input " + in);
            return askPlayerCard(p, prompt, buryCard);
        }
        return toPlay;
    }


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
    public void stats(){
        System.out.println("overall stats: \n");
        System.out.println("\t| rounds played: " + scoreboard.roundsPlayed());
        System.out.println("\t| leaster games played: " + scoreboard.getLeasterCount());
        System.out.println("\t| leaster percentage: " + (float) scoreboard.getLeasterCount() /  scoreboard.roundsPlayed() * 100 + "%\n");
        System.out.println("player specific stats: \n" + "\t-----");
        for(Player p: players){
            p.printDetailedStats(scoreboard.roundsPlayed());
            System.out.println("\t-----");
        }

    }

    /**
     * prints stats of Test player in format:
     * %pick up, Win %, Total Points, %played Alone
     */
    public void pintTestStats(){
        for(Player p : players){
            if(p.getUsername().equals("TEST")){
                p.printTestStats(scoreboard.roundsPlayed());
            }
        }
    }


    public void printResults(){
        scoreboard.printScores();
    }


}
