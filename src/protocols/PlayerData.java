package protocols;

import java.io.Serializable;

/**
 * Created by dave on 2/21/17.
 */
public class PlayerData implements Serializable {

    public String name;
    public int points,nCards,id;
    public boolean partner,picker;

    public PlayerData(String name, int points, int nCards, int id, boolean partner, boolean picker){

        this.name = name;
        this.points = points;
        this.nCards = nCards;
        this.id = id;
        this.partner = partner;
        this.picker = picker;
    }
}
