import Model.Game;

/**
 * Created by Dave on 9/16/2015.
 */
public class SheepsheadRun {

    public static void main(String[] args) {
        Game g;
        boolean printAll=false,realPlayer=false;
        int games = 100000;

        //bins: 0-1420 strength

        System.out.print("playing 100000 games...");
        g = new Game(printAll,realPlayer);
        for(int i=0;i<games;i++){
            g.playRound(-1);
        }
        System.out.println("done \n");
        System.out.println("%pick up\t%win\tPoints\t%played alone");
        g.pintTestStats();
        g.printResults();



    }


}
