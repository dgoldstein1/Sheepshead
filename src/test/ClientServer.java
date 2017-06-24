package test;

import main.client.SheepClient;
import main.server.Hub;
import main.server.SheepHub;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Tests Communication between client and server
 */
public class ClientServer {
    ArrayList<SheepClient> sheepClients;
    SheepHub sheepHub;
    int port;
    final int maxPlayers = 5;

    @Before
    public void setUp() throws IOException {
        port = 8080;
        sheepHub = new SheepHub(port);
        sheepClients = new ArrayList();
    }

    @Test
    public void testConnectivity() throws NoSuchMethodException, IOException {

        //add one player
        sheepClients.add(new SheepClient("localhost", port));
        Assert.assertEquals(1,sheepHub.getPlayerList().length);

        //add multiple players
        for(int i = 1 ; i < maxPlayers ; i ++) {
            sheepClients.add(new SheepClient("localhost", port));
        }
        Assert.assertEquals(5,sheepHub.getPlayerList().length);

    }

}