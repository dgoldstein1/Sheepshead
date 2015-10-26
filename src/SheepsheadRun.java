import Model.Game;

/**
 * Created by Dave on 9/16/2015.
 */
public class SheepsheadRun {
    public static void main(String[] args) {
        Game g = new Game(false,false);
        int games = 100;
        for(int i=0;i<games;i++){
            g.playRound();
        }
        g.stats();
    }


}
