package server;

import client.options.LogType;
import server.model.Game;

import java.io.IOException;

/**
 * Created by dave on 2/27/17.
 */
public class SheepHub extends Hub{
    Game g;

    /**
     * Creates a Hub listening on a specified port, and starts a thread for
     * processing messages that are received from clients.
     *
     * @param port the port on which the server will listen.
     * @throws IOException if it is not possible to create a listening socket on the specified port.
     */

    public SheepHub(int port) throws IOException {
        super(port);
//        g = new Game(obs,"server");
    }

    public void log(Class c, LogType lt, String s){
        System.out.println("[" + c.toString() + "] (" + lt.toString() + ") " + s);
    }
}
