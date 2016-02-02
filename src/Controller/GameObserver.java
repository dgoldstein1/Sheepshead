package Controller;

import Model.Card;

/**
 * Created by Dave on 11/2/2015.
 */
public interface GameObserver {

    public boolean refreshView();
    public String yOrN(String prompt);
    public Card getPlayerCard(String prompt);

}
