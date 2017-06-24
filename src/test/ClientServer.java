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
    final int maxPlayers = 2;

    @Before
    /**
     * initialize server and add clients
     * test that number of clients added is same as number in server
     */
    public void setUp() throws IOException {
        port = 8080;
        sheepHub = new SheepHub(port);
        sheepClients = new ArrayList();

        //add multiple players
        for(int i = 0 ; i < maxPlayers ; i ++) {
            sheepClients.add(new SheepClient("localhost", port));
        }
        Assert.assertEquals(maxPlayers,sheepHub.getPlayerList().length);
    }

    @Test
    /**
     * send / receive message successful
     */
    public void testConnectivity() throws NoSuchMethodException, IOException, InterruptedException {
        //send basic messages
        Thread.sleep(100); //test thread is ahead of Client / Server threads
        sheepClients.get(0).send("test");


        //powerdown
        for (SheepClient sheepClient : sheepClients) {
            sheepClient.disconnect();
        }
        sheepHub.shutDownHub();
    }

}