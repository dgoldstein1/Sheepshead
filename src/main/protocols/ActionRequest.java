package main.protocols;

import main.client.Client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by davidgoldstein on 8/3/17.
 * Requests action from user to server. Server returns updated game state as response
 */
public final class ActionRequest implements Serializable {

    private static final Logger LOGGER = Logger.getLogger( Client.class.getName() );

    public ActionType type;
    public int playerId;
    public Object data;

    public ActionRequest(ActionType type, int playerId, Object data) {
        this.type = type;
        this.playerId = playerId;

        //validate that object is serializable
        try {
            new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(data);
            this.data = data;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unserializable object passed to action request");
            this.data = null;
        }

    }

    /**
     * returns request info in string format
     * @return
     */
    public String toString() {
        return "{{playerID : "+playerId + "},{ActionType : " + type.toString() + "},{data : " + data.toString()+"}}";
    }

    public enum ActionType {CARD_PLAYED}
}
