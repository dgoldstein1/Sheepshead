import Model.Game;

/**
 * Created by Dave on 9/16/2015.
 */
public class SheepsheadRun {
    public static void main(String[] args) {
        boolean printAll=false,realPlayer=false;
        Game g = new Game(printAll,realPlayer);
        int games = 100000;
        for(int i=0;i<games;i++){
            g.playRound();
        }
        g.stats();
    }


}
