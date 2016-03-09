package Controller;

import Model.Card;
import View.LogType;
import View.SoundEffect;

/**
 * Created by Dave on 11/2/2015.
 */
public interface ModelObserver {

    boolean refreshView();
    String yOrN(String prompt);
    Card getPlayerCard(String prompt);
    int displayMessage(String s, String title);
    void log(Class c, LogType type, String s);
    void startDebugger();
    void closeDebugger();
    void playSound(SoundEffect se);

}
