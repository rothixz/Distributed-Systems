package serverSide.Museum;

import static auxiliary.constants.Constants.*;
import auxiliary.messages.Message;
import static auxiliary.messages.Message.*;
import clientSide.ClientCom;
import java.util.HashMap;

/**
 * This data type represents the Museum of the Heist to the Museum Problem.
 *
 * @author António Mota
 * @author Marcos Pires
 */
public class Museum {
    private Room[] rooms;
    private String hostname = CONFIG_PC_NAME;
    private final HashMap<String, String>[] map;

    public Museum() {
        this.rooms = new Room[ROOMS_NUMBER];

        for (int i = 0; i < ROOMS_NUMBER; i++) {
            this.rooms[i] = new Room(i);
        }

        Message inMessage, outMessage;
        ClientCom con = new ClientCom(hostname, 22226);    // canal de comunicação
        while (!con.open()) {
            try {
                Thread.sleep((long) (1000));
            } catch (InterruptedException e) {
            }
        }
        outMessage = new Message(Message.GET_CONFIGS);
        con.writeObject(outMessage);
        inMessage = (Message) con.readObject();
        con.close();
        map = inMessage.getMap();

        reportStatus();
    }

    /**
     *
     * @param thiefID ID of the assault thief
     * @param nRoom ID of the room the thief is raiding
     * @param status Status of the thief
     * @param maxDisp Maximum Displacement of the thief
     * @param partyID ID of the party assigned to the thief
     * @param hasCanvas Represents what the thief is carrying, 0 - empty handed,
     * 1 - got 1 canvas or -1 default value
     * @return Returns 1 if thief was successful in retrieving a canvas or 0 if
     * otherwise
     */
    public synchronized int rollACanvas(int thiefID, int status, int maxDisp, int partyID, int hasCanvas, int nRoom) {
        reportStatusAT(thiefID, status, maxDisp, partyID, hasCanvas);

        for (int i = 0; i < ROOMS_NUMBER; i++) {
            System.out.println("room " + i + " --- " + rooms[i].nPaintings);
        }
        int nPaintings = rooms[nRoom].getNPaintings();
        if (nPaintings > 0) {
            rooms[nRoom].setnPaintings(nPaintings - 1);
            return 1;
        } else {
            return 0;
        }
    }

    /**
     *
     * @param roomID ID of the room
     * @return Returns the room with the given ID
     */
    public Room getRoom(int roomID) {
        return rooms[roomID];
    }

    /**
     *
     * @return true if successfully logged status or false if otherwise
     */
    private boolean reportStatus() {
        Message inMessage, outMessage;
        ClientCom con = new ClientCom(map[0].get("Logger"), Integer.parseInt(map[1].get("Logger")));

        if (!con.open()) {
            return false;
        }

        outMessage = new Message(REP_MUSEUM, rooms);        // pede a realização do serviço
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();
        con.close();

        return inMessage.getType() == ACK;
    }

    /**
     *
     * @return true if successfully logged status or false if otherwise
     */
    private boolean reportStatusAT(int thiefID, int status, int maxDisp, int partyID, int hasCanvas) {
        Message inMessage, outMessage;
        ClientCom con = new ClientCom(map[0].get("Logger"), Integer.parseInt(map[1].get("Logger")));

        if (!con.open()) {
            return false;
        }
        outMessage = new Message(REP_STATUS_AT, thiefID, status, maxDisp, partyID, hasCanvas);        // pede a realização do serviço
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();
        con.close();

        return inMessage.getType() == ACK;
    }
}
