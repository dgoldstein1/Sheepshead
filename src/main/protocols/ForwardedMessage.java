package main.protocols;

import java.io.Serializable;

/**
 * Represents a message that was received by the Hub from
 * one clients and that is being forwarded to all clients.
 * A ForwardedMessage includes the message that was sent
 * by a main.client to the Hub and the ID number of the main.client
 * who sent it.  The default action of a Hub -- defined
 * in the messageReceived(playerID,message) method of
 * that class -- is to wrap the message in a ForwardedMessage
 * and send the ForwardedMessage to all connected main.client,
 * including the main.client who sent the original message.
 * When an application uses a subclass of Hub, it is
 * likely to override that behavior.
 */
public class ForwardedMessage implements Serializable {

    public final Object message;  // Original message from a main.client.
    public final int senderID;    // The ID of the main.client who sent that message.

    /**
     * Create a ForwadedMessage to wrap a message sent by a main.client.
     * @param senderID  the ID number of the original sender.
     * @param message  the original message.
     */
    public ForwardedMessage(int senderID, Object message) {
        this.senderID = senderID;
        this.message = message;
    }

}