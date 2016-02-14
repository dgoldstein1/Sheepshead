package Controller;

import Model.Card;
import View.LogType;

/**
 * Created by Dave on 11/2/2015.
 */
public interface GameObserver {

    public boolean refreshView();
    public String yOrN(String prompt);
    public Card getPlayerCard(String prompt);
    public int displayMessage(String s);
    public void log(Class c, LogType type, String s);
    public void startDebugger();
    public void closeDebugger();

}
