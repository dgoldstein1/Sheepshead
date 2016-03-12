import Model.Game;

/**
 * Created by Dave on 9/16/2015.
 */
public class SheepsheadRun {

    public static void main(String[] args) {
        boolean printAll=false,realPlayer=false;

        System.out.println("\t%pick up\t%win\tPoints\t%played alone\n");
        int games = 100000;
        Game g;
        g = new Game(printAll,realPlayer);
        for(int i=0;i<games;i++){
            g.playRound(-1);
        }

        g.pintTestStats();



    }


}
