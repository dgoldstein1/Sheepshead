package main.client;

import main.client.game_display.UIController;
import main.protocols.ForwardedMessage;
import main.protocols.GameState;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by davidgoldstein on 6/24/17.
 * ClientUI override of ClientUI.java to Communicate with SheepHub
 */
public class SheepClient extends Client {
    private static final Logger LOGGER = Logger.getLogger( SheepClient.class.getName() );
    public UIController UI;

    public Stack<ForwardedMessage> messagesReceived = new Stack();

    /**
     * Creates main.client which connect to specified host / port
     * @param hostname
     * @param port
     * @throws IOException
     */
    public SheepClient(String hostname, int port) throws IOException {
        super(hostname, port);
    }

    // ---------------- Methods that override ClientUI.java --------------------------

    @Override
    /**
     * Receives chat messages from other players and
     * adds them to messagesRecieved stack
     */
    protected void messageReceived(ForwardedMessage message){
        messagesReceived.push(message);
    }

    @Override
    /**
     * receives gameState from server and updates the display.
     * If the game state is null, initializes game state whatever is passed
     */
    protected void receiveGameState(GameState gameState) {
        if (UI == null) UI = new UIController(gameState, this);
        else if (!UI.refresh(gameState)) LOGGER.log(Level.SEVERE, "[ClientUI " + this.getID() + "] could not update UI.");
    }

    @Override
    protected void playerConnected(int newPlayerId){ }

    @Override
    protected void playerDisconnected(int departingPlayerID) { }

    @Override
    protected void connectionClosedByError(String message) { }

    @Override
    protected void serverShutdown(String message) { }

    @Override
    protected void extraHandshake(ObjectInputStream in, ObjectOutputStream out) throws IOException { }

}
