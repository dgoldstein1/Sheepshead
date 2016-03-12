import Model.Game;

/**
 * Created by Dave on 9/16/2015.
 */
public class SheepsheadRun {
    public static void main(String[] args) {
        boolean printAll=false,realPlayer=false;
        Game g = new Game(printAll,realPlayer);
        int games = 10000;


        for(int j = 0 ; j <= 100 ; j+=5){
            for(int i=0;i<games;i++){
                g.playRound(j * 2);
            }
            System.out.print(j + "\t"); g.pintTestStats();
        }

    }


}
