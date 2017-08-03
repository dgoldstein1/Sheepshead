package test;

import main.client.SheepClient;
import main.protocols.GameState;
import main.server.SheepHub;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Tests Communication between client and server
 */
public class ClientUIServerConnection {
    ArrayList<SheepClient> sheepClients;
    SheepHub sheepHub;
    int port;
    final int maxPlayers = 2;

    @Before
    /**
     * initialize server and add clients
     * test that number of clients added is same as number in server
     */
    public void setUp() throws IOException, InterruptedException {
        port = 8080;
        sheepHub = new SheepHub(port);
        sheepClients = new ArrayList();

        //add players and assert correct number were added
        for(int i = 0 ; i < maxPlayers ; i ++) sheepClients.add(new SheepClient("localhost", port));
        Assert.assertEquals(maxPlayers,sheepHub.getPlayerList().length);
    }

    @After
    public void shutDown() {
        for (SheepClient c : sheepClients) c.disconnect();
        sheepHub.shutDownHub();
    }


    @Test
    /**
     * send / receive message successful
     */
    public void sendReceiveMessages() throws InterruptedException {
        System.out.println("\n---------- TESTING sendReceiveMessages() ---------- \n");
        Thread.sleep(100);
        String testMessage = "Test";
        sheepClients.get(0).send(testMessage);


        // check that all players have received one message
        // and that the first message is the test message
        Thread.sleep(100);
        for (SheepClient client : sheepClients) {
            Assert.assertEquals(testMessage, (client.messagesReceived.peek()).message.toString());
        }

    }

    @Test
    /**
     * Tests that gameState is being sent and received
     */
    public void gameStateReceived() throws InterruptedException {
        System.out.println("\n---------- TESTING sendReceiveGameState ----------\n");
        Thread.sleep(100);

        GameState emptyGameState = new GameState();
        sheepHub.sendGameState(emptyGameState);

        Thread.sleep(1000);
        // assert that ui is not initialized when passed empty state
        // but user settings are
        for(SheepClient c : sheepClients) {
            Assert.assertNull(c.UI.frame); //frame has not been created
            Assert.assertNotNull(c.UI.usersettings);
        }
    }

}