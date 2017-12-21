package clientSide;

import static auxiliary.constants.Constants.*;
import static auxiliary.constants.States.*;
import auxiliary.messages.*;
import static auxiliary.messages.Message.*;
import genclass.GenericIO;
import java.util.HashMap;

/**
 *
 * @author António Mota
 * @author Marcos Pires
 */
public class AssaultThief extends Thread {

    private final int thiefID;
    private int status;
    private final int maxDisp;
    private int partyID;
    private int hasCanvas;

    private int nIter;
    private String serverHostName = null;
    private int serverPort;
    private HashMap<String, String>[] map;

    /**
     *
     * @param thiefID
     * @param nIter
     * @param hostName
     * @param serverPort
     * @param map
     */
    public AssaultThief(int thiefID, int nIter, String hostName, int serverPort, HashMap<String, String>[] map) {
        super("Thief_" + thiefID);

        this.thiefID = thiefID;
        status = OUTSIDE;
        maxDisp = (int) (Math.random() * (THIEVES_MAX_DISPLACEMENT + 1 - THIEVES_MIN_DISPLACEMENT)) + THIEVES_MIN_DISPLACEMENT;
        partyID = -1;
        hasCanvas = 0;

        this.nIter = nIter;
        serverHostName = hostName;
        this.serverPort = serverPort;
        this.map = map;
    }

    /**
     *
     */
    @Override
    @SuppressWarnings("empty-statement")
    public void run() {
        while (amINeeded() != -1) {
            prepareExcursion();
            while (crawlIn());
            rollACanvas(getRoomID());
            reverseDirection();
            while (crawlOut());
            handCanvas();
        }
    }

    private int amINeeded() {
        partyID = -1;
        hasCanvas = -1;
        status = OUTSIDE;

        Message inMessage, outMessage;
        ClientCom con = new ClientCom(map[0].get("ConcentrationSite"), Integer.parseInt(map[1].get("ConcentrationSite")));

        if (!con.open()) {
            return -1;
        }

        outMessage = new Message(AMINEEDED, thiefID, status, maxDisp, partyID, hasCanvas);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();
        if (inMessage.getType() != ACK) {
            GenericIO.writelnString("Thread " + getName() + ": Tipo inválido!");
            GenericIO.writelnString(inMessage.toString());
            System.exit(1);
        }
        con.close();

        if (inMessage.getInteger0() != -1) {
            this.partyID = inMessage.getInteger0();
            this.hasCanvas = 0;
        }

        return inMessage.getInteger0();
    }

    private boolean prepareExcursion() {
        status = WAITING_SEND_ASSAULT_PARTY;

        Message inMessage, outMessage;
        ClientCom con = new ClientCom(map[0].get("ControlCollectionSite"), Integer.parseInt(map[1].get("ControlCollectionSite")));
        if (!con.open()) {
            return false;
        }
        outMessage = new Message(PREPAREEXCURSION, thiefID, status, maxDisp, partyID, hasCanvas);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();
        if (inMessage.getType() != ACK) {
            GenericIO.writelnString("Thread " + getName() + ": Tipo inválido!");
            GenericIO.writelnString(inMessage.toString());
            System.exit(1);
        }

        return false;
    }

    private int getDistOutsideRoom() {
        Message inMessage, outMessage;
        ClientCom con = new ClientCom(map[0].get("AssaultParty" + String.valueOf(partyID)), Integer.parseInt(map[1].get("AssaultParty" + String.valueOf(partyID))));
        if (!con.open()) {
            return -1;
        }
        outMessage = new Message(GET_DIST_OUTSIDE);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();
        if (inMessage.getType() != ACK) {
            GenericIO.writelnString("Thread " + getName() + ": Tipo inválido!");
            GenericIO.writelnString(inMessage.toString());
            System.exit(1);
        }

        return inMessage.getInteger0();
    }

    private boolean crawlIn() {
        status = CRAWLING_INWARDS;

        Message inMessage, outMessage;
        ClientCom con = new ClientCom(map[0].get("AssaultParty" + String.valueOf(partyID)), Integer.parseInt(map[1].get("AssaultParty" + String.valueOf(partyID))));
        if (!con.open()) {
            return false;
        }
        outMessage = new Message(CRAWL_IN, thiefID, status, maxDisp, partyID, hasCanvas);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();
        if (inMessage.getType() != ACK) {
            GenericIO.writelnString("Thread " + getName() + ": Tipo inválido!");
            GenericIO.writelnString(inMessage.toString());
            System.exit(1);
        }

        return false;
    }

    private int getRoomID() {
        Message inMessage, outMessage;
        ClientCom con = new ClientCom(map[0].get("AssaultParty" + String.valueOf(partyID)), Integer.parseInt(map[1].get("AssaultParty" + String.valueOf(partyID))));
        if (!con.open()) {
            return -1;
        }
        outMessage = new Message(GET_ROOM_ID);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();
        if (inMessage.getType() != ACK) {
            GenericIO.writelnString("Thread " + getName() + ": Tipo inválido!");
            GenericIO.writelnString(inMessage.toString());
            System.exit(1);
        }

        return inMessage.getInteger0();
    }

    private boolean rollACanvas(int roomID) {
        status = AT_A_ROOM;

        Message inMessage, outMessage;
        ClientCom con = new ClientCom(map[0].get("Museum"), Integer.parseInt(map[1].get("Museum")));
        if (!con.open()) {
            return false;
        }
        outMessage = new Message(ROLL_A_CANVAS, thiefID, status, maxDisp, partyID, hasCanvas, roomID);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();
        if (inMessage.getType() != ACK) {
            GenericIO.writelnString("Thread " + getName() + ": Tipo inválido!");
            GenericIO.writelnString(inMessage.toString());
            System.exit(1);
        }

        hasCanvas = inMessage.getInteger0();

        return true;
    }

    private boolean reverseDirection() {
        status = CRAWLING_OUTWARDS;

        Message inMessage, outMessage;
        ClientCom con = new ClientCom(map[0].get("AssaultParty" + String.valueOf(partyID)), Integer.parseInt(map[1].get("AssaultParty" + String.valueOf(partyID))));
        if (!con.open()) {
            return false;
        }
        outMessage = new Message(REVERSE_DIRECTION, thiefID, status, maxDisp, partyID, hasCanvas);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();
        if (inMessage.getType() != ACK) {
            GenericIO.writelnString("Thread " + getName() + ": Tipo inválido!");
            GenericIO.writelnString(inMessage.toString());
            System.exit(1);
        }
        
        return true;
    }

    private boolean crawlOut() {
        status = OUTSIDE;

        Message inMessage, outMessage;
        ClientCom con = new ClientCom(map[0].get("AssaultParty" + String.valueOf(partyID)), Integer.parseInt(map[1].get("AssaultParty" + String.valueOf(partyID))));
        if (!con.open()) {
            return false;
        }
        outMessage = new Message(CRAWL_OUT, thiefID, status, maxDisp, partyID, hasCanvas);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();
        if (inMessage.getType() != ACK) {
            GenericIO.writelnString("Thread " + getName() + ": Tipo inválido!");
            GenericIO.writelnString(inMessage.toString());
            System.exit(1);
        }
        
        return false;
    }

    private boolean handCanvas() {
        status = OUTSIDE;

        Message inMessage, outMessage;
        ClientCom con = new ClientCom(map[0].get("ControlCollectionSite"), Integer.parseInt(map[1].get("ControlCollectionSite")));
        if (!con.open()) {
            return false;
        }
        outMessage = new Message(HAND_CANVAS, thiefID, status, maxDisp, partyID, hasCanvas);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();
        if (inMessage.getType() != ACK) {
            GenericIO.writelnString("Thread " + getName() + ": Tipo inválido!");
            GenericIO.writelnString(inMessage.toString());
            System.exit(1);
        }

        return true;
    }
}
