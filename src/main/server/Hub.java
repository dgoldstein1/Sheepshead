package main.server;

import main.protocols.DisconnectMessage;
import main.protocols.ForwardedMessage;
import main.protocols.ResetSignal;
import main.protocols.StatusMessage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Taken from http://math.hws.edu/javanotes/source/chapter12/netgame/common/Hub.java
 */
public class Hub {

    /**
     *  A map that associates player names with the connections to each player.
     */
    private TreeMap<Integer, ConnectionToClient> playerConnections;

    /**
     * A queue of messages received from clients.  When a method is received,
     * it is placed in this queue.  A separate thread takes messages from the
     * queue and processes them (in the order in which they were received).
     */
    private LinkedBlockingQueue<Message> incomingMessages;

    /**
     * If the autoreset property is set to true, then the ObjectOutputStreams that are
     * used for transmitting messages to clients is reset before each object is sent.
     */
    private volatile boolean autoreset;

    private ServerSocket serverSocket;  // Listens for connections.
    private Thread serverThread;        // Accepts connections on serverSocket
    volatile private boolean shutdown;  // Set to true when the Hub is not listening.

    private int nextClientID = 1;  // The id number that will be assigned to
    // the next main.client that connects.

    private static final Logger LOGGER = Logger.getLogger( Hub.class.getName() );


    /**
     * Creates a Hub listening on a specified port, and starts a thread for
     * processing messages that are received from clients.
     * @param port  the port on which the main.server will listen.
     * @throws IOException if it is not possible to create a listening socket on the specified port.
     */
    public Hub(int port) throws IOException {
        playerConnections = new TreeMap<Integer, ConnectionToClient>();
        incomingMessages = new LinkedBlockingQueue<Message>();
        serverSocket = new ServerSocket(port);
        LOGGER.log(Level.INFO, "Listening for main.client connections on port " + port);
        serverThread = new ServerThread();
        serverThread.start();
        Thread readerThread = new Thread(){
            public void run() {
                while (true) {
                    try {
                        Message msg = incomingMessages.take();
                        messageReceived(msg.playerConnection, msg.message);
                    }
                    catch (Exception e) {
                        LOGGER.log(Level.SEVERE,"Exception while handling received message:", e);
                        e.printStackTrace();
                    }
                }
            }
        };
        readerThread.setDaemon(true);
        readerThread.start();
    }


    /**
     * This method is called when a message is received from one of the
     * connected players.  The method in this class simply wraps the message,
     * along with the ID of the sender of the message, into a message of type
     * ForwardedMessage and then sends that ForwardedMessage to all connected
     * players, including the one who sent the original message.  This
     * behavior will often be overridden in subclasses.
     * @param playerID  The ID number of the player who sent the message.
     * @param message The message that was received from the player.
     */
    protected void messageReceived(int playerID, Object message) {
        LOGGER.log(Level.INFO, " [Server] Received message " + message + " from " + playerID);
        sendToAll(new ForwardedMessage(playerID,message));
    }


    /**
     * This method is called just after a player has connected.
     * Note that getPlayerList() can be called to get a list
     * of connected players.  The method in this class does nothing.
     * @param playerID the ID number of the new player.
     */
    protected void playerConnected(int playerID) {
    }


    /**
     * This method is called just after a player has disconnected.
     * Note that getPlayerList() can be called to get a list
     * of connected players.  The method in this class does nothing.
     * @param playerID the ID number of the new player.
     */
    protected void playerDisconnected(int playerID) {
    }

    /**
     * This method is called after a connection request has been received to do
     * extra checking or set up before the connection is fully established.
     * It is called after the playerID has been transmitted to the main.client.
     * If this method throws an IOException, then the connection is closed
     * and the player is never added to the list of players.  The method in
     * this class does nothing.   The main.client and the hub must both be programmed
     * with the same handshake protocol.
     * @param playerID the ID number of the player who is connecting.
     * @param in a stream from which messages from the main.client can be read.
     * @param out a stream to which message to the main.client can be written.  After writing
     *    a message to this stream, it is important to call out.flush() to make sure
     *    that the message is actually transmitted.
     * @throws IOException should be thrown if some error occurs that should
     * prevent the connection from being established.
     */
    protected void extraHandshake(int playerID, ObjectInputStream in,
                                  ObjectOutputStream out) throws IOException {
    }


    /**
     * Gets a list of ID numbers of currently connected clients.
     * @return an array containing the ID numbers of all the connected clients.
     * The array is newly created each time this method is called.
     */
    synchronized public int[] getPlayerList() {
        int[] players = new int[playerConnections.size()];
        int i = 0;
        for (int p : playerConnections.keySet())
            players[i++] = p;
        return players;
    }


    /**
     * Stops listening, without disconnecting any currently connected clients.
     * You might do this, for example, if some maximum number of player connections has
     * been reached, as when a game only allows two players.
     */
    public void shutdownServerSocket() {
        if (serverThread == null)
            return;
        incomingMessages.clear();
        shutdown = true;
        try {
            serverSocket.close();
        }
        catch (IOException e) {
        }
        serverThread = null;
        serverSocket = null;
    }


    /**
     * Restarts listening and accepting new clients.  This would only be used if
     * the shutDownHub() method has been called previously.
     * @param port the port on which the main.server should listen.
     * @throws IOException if it is impossible to create a listening socket on the specified port.
     */
    public void restartServer(int port) throws IOException {
        if (serverThread != null && serverThread.isAlive())
            throw new IllegalStateException("Server is already listening for connections.");
        shutdown = false;
        serverSocket = new ServerSocket(port);
        serverThread = new ServerThread();
        serverThread.start();
    }


    /**
     *  Disconnects all currently connected clients and stops accepting new main.client
     *  requests.  It is still possible to restart listening after this method has
     *  been called, by calling the restartServer() method.
     */
    public void shutDownHub() {
        shutdownServerSocket();
        sendToAll(new DisconnectMessage("*shutdown*"));
        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException e) {
        }
        for (ConnectionToClient pc : playerConnections.values())
            pc.close();
    }


    /**
     * Sends a specified non-null Object as a message to all connected main.client.
     * @param message the message to be sent to all connected clients.  This object must
     * implement the Serializable interface.  Messages must not be null.
     */
    synchronized public void sendToAll(Object message) {
        if (message == null)
            throw new IllegalArgumentException("Null cannot be sent as a message.");
        if ( ! (message instanceof Serializable) )
            throw new IllegalArgumentException("Messages must implement the Serializable interface.");
        for (ConnectionToClient pc : playerConnections.values())
            pc.send(message);
    }


    /**
     * Sends a specified non-null Object as a message to one connected main.client.
     * @param recipientID The ID number of the player to whom the message is
     * to be sent.  If there is no such player, then the method returns the
     * value false.
     * @param message the message to be sent to all connected clients.  This object must
     * implement the Serializable interface.  Messages must not be null.
     * @return true if the specified recipient exists, false if not.
     */
    synchronized public boolean sendToOne(int recipientID, Object message) {
        if (message == null)
            throw new IllegalArgumentException("Null cannot be sent as a message.");
        if ( ! (message instanceof Serializable) )
            throw new IllegalArgumentException("Messages must implement the Serializable interface.");
        ConnectionToClient pc = playerConnections.get(recipientID);
        if (pc == null)
            return false;
        else {
            pc.send(message);
            return true;
        }
    }


    /**
     * Resets all output streams, after any messages currently in the output queue
     * have been sent.  The stream only needs to be reset in one case:  If the same
     * object is transmitted more than once, and changes have been made to it
     * between transmissions.  The reason for this is that ObjectOutputStreams are
     * optimized for sending objects that don't change -- if the same object is sent
     * twice it will not actually be transmitted the second time, unless the stream
     * has been reset in the meantime.
     */
    public void resetOutput() {
        ResetSignal rs = new ResetSignal();
        for (ConnectionToClient pc : playerConnections.values())
            pc.send(rs); // A ResetSignal in the output stream is seen as a signal to reset
    }


    /**
     * If the autoreset property is set to true, then all output streams will be reset
     * before every object transmission.  Use this if the same object is going to be
     * continually changed and retransmitted.  See the resetOutput() method for more
     * information on resetting the output stream.  The default value is false.
     */
    public void setAutoreset(boolean auto) {
        autoreset = auto;
    }

    /**
     * Returns the value of the autoreset property.
     */
    public boolean getAutoreset() {
        return autoreset;
    }


    //------------------------- private implementation part ---------------------------------------


    synchronized private void messageReceived(ConnectionToClient fromConnection, Object message) {
        // Note: DisconnectMessage is handled in the ConnectionToClient class.
        int sender = fromConnection.getPlayer();
        messageReceived(sender,message);
    }


    synchronized private void acceptConnection(ConnectionToClient newConnection) {
        int ID = newConnection.getPlayer();
        playerConnections.put(ID,newConnection);
        StatusMessage sm = new StatusMessage(ID,true,getPlayerList());
        sendToAll(sm);
        playerConnected(ID);
        LOGGER.log(Level.INFO, "[Server] Connection accepted from main.client number " + ID);
    }

    synchronized private void clientDisconnected(int playerID) {
        if (playerConnections.containsKey(playerID)) {
            playerConnections.remove(playerID);
            StatusMessage sm = new StatusMessage(playerID,false,getPlayerList());
            sendToAll(sm);
            playerDisconnected(playerID);
            LOGGER.log(Level.INFO, "[Server] Connection with main.client number " + playerID + " closed by DisconnectMessage from main.client.");
        }
    }

    synchronized private void connectionToClientClosedWithError( ConnectionToClient playerConnection, String message ) {
        int ID = playerConnection.getPlayer();
        if (playerConnections.remove(ID) != null) {
            StatusMessage sm = new StatusMessage(ID,false,getPlayerList());
            sendToAll(sm);
        }
    }

    private class Message {
        ConnectionToClient playerConnection;
        Object message;
    }

    private class ServerThread extends Thread {  // Listens for connection requests from clients.
        public void run() {
            try {
                while ( ! shutdown ) {
                    Socket connection = serverSocket.accept();
                    if (shutdown) {
                        LOGGER.log(Level.SEVERE, "[Server] Listener socket has shut down.");
                        break;
                    }
                    new ConnectionToClient(incomingMessages,connection);
                }
            }
            catch (Exception e) {
                if (shutdown)
                    LOGGER.log(Level.SEVERE, "[Server] Listener socket has shut down.");
                else
                    LOGGER.log(Level.SEVERE, "[Server] Listener socket has been shut down by error: " + e);
            }
        }
    }


    private class ConnectionToClient { // Handles communication with one main.client.

        private int playerID;  // The ID number for this player.
        private BlockingQueue<Message> incomingMessages;
        private LinkedBlockingQueue<Object> outgoingMessages;
        private Socket connection;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private volatile boolean closed;  // Set to true when connection is closing normally.
        private Thread sendThread; // Handles setup, then handles outgoing messages.
        private volatile Thread receiveThread; // Created only after connection is open.

        ConnectionToClient(BlockingQueue<Message> receivedMessageQueue, Socket connection)  {
            this.connection = connection;
            incomingMessages = receivedMessageQueue;
            outgoingMessages = new LinkedBlockingQueue<Object>();
            sendThread =  new SendThread();
            sendThread.start();
        }

        int getPlayer() {
            return playerID;
        }

        void close() {
            closed = true;
            sendThread.interrupt();
            if (receiveThread != null)
                receiveThread.interrupt();
            try {
                connection.close();
            }
            catch (IOException e) {
            }
        }

        void send(Object obj) { // Just drop message into message output queue.
            if (obj instanceof DisconnectMessage) {
                // A signal to close the connection;
                // discard other waiting messages, if any.
                outgoingMessages.clear();
            }
            outgoingMessages.add(obj);
        }

        private void closedWithError(String message) {
            connectionToClientClosedWithError(this, message);
            close();
        }

        /**
         * Handles the "handshake" that occurs before the connection is opened.
         * Once that's done, it creates a thread for receiving incoming messages,
         * and goes into an infinite loop in which it transmits outgoing messages.
         */
        private class SendThread extends Thread {
            public void run() {
                try {
                    out = new ObjectOutputStream(connection.getOutputStream());
                    in = new ObjectInputStream(connection.getInputStream());
                    String handle = (String)in.readObject(); // first input must be "Hello Hub"
                    if ( ! "Hello Hub".equals(handle) )
                        throw new Exception("Incorrect hello string received from main.client.");
                    synchronized(Hub.this) {
                        playerID = nextClientID++; // Get a player ID for this player.
                    }
                    out.writeObject(playerID);  // send playerID to the main.client.
                    out.flush();
                    extraHandshake(playerID,in,out);  // Does any extra stuff before connection is fully established.
                    acceptConnection(ConnectionToClient.this);
                    receiveThread = new ReceiveThread();
                    receiveThread.start();
                }
                catch (Exception e) {
                    try {
                        closed = true;
                        connection.close();
                    }
                    catch (Exception e1) {
                    }
                    LOGGER.log(Level.INFO, "[Server] Error while setting up connection: " + e);
                    e.printStackTrace();
                    return;
                }
                try {
                    while ( ! closed ) {  // Get messages from outgoingMessages queue and send them.
                        try {
                            Object message = outgoingMessages.take();
                            if (message instanceof ResetSignal)
                                out.reset();
                            else {
                                if (autoreset)
                                    out.reset();
                                out.writeObject(message);
                                out.flush();
                                if (message instanceof DisconnectMessage) // A signal to close the connection.
                                    close();
                            }
                        }
                        catch (InterruptedException e) {
                            // should mean that connection is closing
                        }
                    }
                }
                catch (IOException e) {
                    if (! closed) {
                        closedWithError("[Server] Error while sending data to main.client.");
                        LOGGER.log(Level.INFO, "[Server] Hub send thread terminated by IOException: " + e);
                    }
                }
                catch (Exception e) {
                    if (! closed) {
                        closedWithError("[Server] Internal Error: Unexpected exception in output thread: " + e);
                        LOGGER.log(Level.SEVERE, "[Server] Unexpected error shuts down hub's send thread:");
                        e.printStackTrace();
                    }
                }
            }
        }

        /**
         * The ReceiveThread reads messages transmitted from the main.client.  Messages
         * are dropped into an incomingMessages queue, which is shared by all clients.
         * If a DisconnectMessage is received, however, it is a signal from the
         * main.client that the main.client is disconnecting.
         */
        private class ReceiveThread extends Thread {
            public void run() {
                try {
                    while ( ! closed ) {
                        try {
                            Object message = in.readObject();
                            Message msg = new Message();
                            msg.playerConnection = ConnectionToClient.this;
                            msg.message = message;
                            if ( ! (message instanceof DisconnectMessage) )
                                incomingMessages.put(msg);
                            else {
                                closed = true;
                                outgoingMessages.clear();
                                out.writeObject("*goodbye*");
                                out.flush();
                                clientDisconnected(playerID);
                                close();
                            }
                        }
                        catch (InterruptedException e) {
                            // should mean that connection is closing
                        }
                    }
                }
                catch (IOException e) {
                    if (! closed) {
                        closedWithError("[Server] Error while reading data from main.client.");
                        LOGGER.log(Level.SEVERE, "[Server] Hub receive thread terminated by IOException: " + e);
                    }
                }
                catch (Exception e) {
                    if ( ! closed ) {
                        closedWithError("[Server] Internal Error: Unexpected exception in input thread: " + e);
                        LOGGER.log(Level.SEVERE, " [Server] Unexpected error shuts down hub's receive thread:");
                        e.printStackTrace();
                    }
                }
            }
        }

    }  // end nested class ConnectionToClient


}