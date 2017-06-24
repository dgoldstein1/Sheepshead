package main.client;

import main.client.options.LogType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by davidgoldstein on 6/24/17.
 * Client override of Client.java to Communicate with SheepHub
 */
public class SheepClient extends Client {
    private static final Logger LOGGER = Logger.getLogger( SheepClient.class.getName() );

    /**
     * Creates main.client which connect to specified host / port
     * @param hostname
     * @param port
     * @throws IOException
     */
    public SheepClient(String hostname, int port) throws IOException {
        super(hostname, port);
    }

    // ---------------- Methods that override Client.java --------------------------

    protected void messageReceived(Object message){
        LOGGER.log(Level.INFO,"Received Message ",message);
    }

    protected void playerConnected(int newPlayerId){ }

    protected void playerDisconnected(int departingPlayerID) { }

    protected void connectionClosedByError(String message) { }

    protected void serverShutdown(String message) { }

    protected void extraHandshake(ObjectInputStream in, ObjectOutputStream out) throws IOException { }

}
