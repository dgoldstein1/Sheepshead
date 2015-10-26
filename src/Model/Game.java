package Model;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
            String playerName = getPlayerInput("Enter Player name: ", false);
            players[0] = new Player(playerName, 0, table, true,printAll,AIPersonality.NONE);
            playersCreated++;
        }
        //initalize AI players
        players[playersCreated - 1] = new Player("Sticky Fingers",playersCreated,table,false,printAll,AIPersonality.STICKY_FINGERS);
        playersCreated++;
        while (playersCreated <= 5) {
            players[playersCreated - 1] = new Player("Player " + playersCreated, playersCreated, table, false,printAll,AIPersonality.NONE);
            playersCreated++;
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

        //play hands in round
        for (int hand = 0; hand < 6; hand++) {
            Player winner = playHand();
            shiftPlayers(winner);//sets winner as new leader
            if (printAll) scoreboard.printPoints();
        }
        endRound();
        dealer.collectCards();
        shiftPlayers(null);
        if (printAll) System.out.println("---end round---\n");
    }

    /**
     * choose who picks up blind
     * based on this, sets partner and non-partner team in players[]
     */
    private void setTeams() {
        boolean pickedUp = false;
        while (!pickedUp) {
            for (Player p : players) {
                if (p.isPlayer()) {//non ai player
                    if (printAll) {
                        System.out.println("Your current hand: \n");
                        p.printHand();
                    }
                    if (getPlayerInput("pickup blind? y/n: ", true).equals("y")) {//player choses to pick up
                        scoreboard.setPicker(p);
                        p.pickUpBlind();
                        p.printHand();
                        Card c1 = askPlayerCard("Choose first card to bury: ", p);
                        Card c2 = askPlayerCard("Choose second card to bury: ", p);
                        p.buryCards(c1, c2);
                        pickedUp = true;
                    }
                } else if (p.chooseToPickUp()) {//ai player
                    scoreboard.setPicker(p); //note picker must come before partner
                    pickedUp = true;
                    break;
                }
            }
            if (!pickedUp) { //blind not picked up by any player
                if(printAll) System.out.println("Blind not picked up: Leaster will be played!!");
                scoreboard.setLeaster();
                table.setLeaster();
                pickedUp=true;
            }
        }
        //updates partners on scoreboard
        for (Player p : players) {
            if (p.checkIsPartner()) scoreboard.setPartner(p);
            if (p.hasBlitzers()) scoreboard.setBlitzers(true);
        }
    }

    /**
     * resets player variables
     * shifts players one seat over for new round
     */
    private void endRound() {
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
                System.out.println("Your hand is: \n");
                p.printHand();
                Card c = askPlayerCard("Choose card to play: ", p);
                while (!p.playCard(c))
                    c = askPlayerCard("Choose card to play: ", p);
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
            System.out.println("error reading input");
            e.printStackTrace();
            System.exit(1);
        }
        if (input == null) {//no string entered
            System.out.println("invalid input");
            getPlayerInput(prompt, yesOrNo);
        }
        if (yesOrNo) {//y/n prompt
            if (!input.equals("y") && !input.equals("n")) {
                System.out.println(input);
                System.out.println("invalid string (not 'y' or 'n')");
                getPlayerInput(prompt, yesOrNo);
            }

        }
        return input;
    }

    /**
     * asks player to play a card
     * checks if valid card
     * checks if in player hand
     *
     * @param prompt prompt to be displayed
     * @param p      player chose from players[]
     * @return card played
     */
    private Card askPlayerCard(String prompt, Player p) {
        String in = getPlayerInput(prompt, false);
        Card toPlay = dealer.getCardID(in);
        if (toPlay.getId() == -1) {
            System.out.println("invalid input");
            askPlayerCard(prompt, p);
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
            p.printHand();
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



    public void printResults(){
        scoreboard.printScores();
    }


}
