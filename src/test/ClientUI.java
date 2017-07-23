package test;

import main.client.game_display.GamePanel;
import main.client.game_display.UIController;
import main.protocols.GameState;
import main.protocols.PlayerData;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by davidgoldstein on 7/22/17.
 * Tests initialization of client UI
 */
public class ClientUI {
    UIController clientUI;
    GameState mockState;

    @Before
    // creates dummy data in defaultState
    public void setup() {
        ArrayList<PlayerData> players = new ArrayList<PlayerData>();
        for (int i = 0; i < 5; i++) {
            players.add(new PlayerData(i + "", 10, 5, i, false, false));
        }
        ArrayList<Integer> cardsOnTable = new ArrayList<Integer>(6){{
            add(14); add(15); add(4); add(8); add(13);
        }};
        ArrayList<Integer> cardsInHand = new ArrayList<Integer>(6){{
            add(2); add(5); add(7); add(3); add(9); add(11);
        }};
        mockState = new GameState(players, cardsOnTable,cardsInHand, GameState.State.INIT);
    }

    @After
    public void shutdown() {
        if (clientUI.frame != null) clientUI.frame.dispose();
        clientUI = null;
        mockState = null;
    }

    @Test
    // frame is NOT initialized when gameState is empty
    public void frameIsNullOnStart() {
        clientUI = new UIController(new GameState());
        Assert.assertNull(clientUI.frame);
    }

    @Test
    // frame initializes on dummy data
    public void frameInitializesOnData() throws InterruptedException, NoSuchFieldException {
        clientUI = new UIController(mockState);
        Assert.assertNotNull(clientUI.frame);
    }

    @Test
    // frame displaying correct mock
    public void frameDisplayingCorrectCards() {
        clientUI = new UIController(mockState);
        GameState displayedState = clientUI.getDataCurrentlyDisplayed();
        Assert.assertEquals(mockState.cardsInHand, displayedState.cardsInHand);
        Assert.assertEquals(mockState.cardsOnTable, displayedState.cardsOnTable);
        Assert.assertEquals(mockState.players, displayedState.players);
        Assert.assertEquals(mockState.state, displayedState.state);
    }


}
