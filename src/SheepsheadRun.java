import Model.Game;

/**
 * Created by Dave on 9/16/2015.
 */
public class SheepsheadRun {

    public static void main(String[] args) {
        Game g;
        boolean printAll=false,realPlayer=false;
        int games = 10000;
        System.out.println("\t%pick up\t%win\tPoints\t%played alone\n");

        //bins: 0-1420 strength

        g = new Game(printAll,realPlayer);
        for(int i=0;i<games;i++){
            g.playRound(-1);
        }



    }


}
