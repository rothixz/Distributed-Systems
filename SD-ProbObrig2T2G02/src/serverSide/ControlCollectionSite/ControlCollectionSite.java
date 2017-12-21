package serverSide.ControlCollectionSite;

import static auxiliary.constants.Constants.*;
import auxiliary.messages.Message;
import static auxiliary.messages.Message.*;
import clientSide.ClientCom;
import java.util.HashMap;

/**
 * This data type represents the Control and Collection Site of the Heist to the
 * Museum Problem.
 *
 * @author António Mota
 * @author Marcos Pires
 */
public class ControlCollectionSite {

    private int[] partiesArrived;           // Needed to know which parties have arrived
    private boolean[] roomOcupied;          // Represents the status of each room (occupied or clear)
    private boolean[] emptyRooms;           // Represents the emptyness of each room (empty or still with paintings)
    private int nPaintings;                 // Total number of collected paintings
    private int nextRoom;                   // Next room to rob
    private int nextParty;                  // Next party to prepare

    // Flags for operations sendAssaultParty and prepareExcursion
    private int partyReady;
    private int good = 0;
    private boolean sentAssaultParty;

    // Flags for operations handACanvas, collectCanvas and takeARest
    private boolean rest;
    private boolean ready;
    private boolean collectCanvas;

    private HashMap<String, String>[] map;
    private String hostname = CONFIG_PC_NAME;

    public ControlCollectionSite() {
        roomOcupied = new boolean[ROOMS_NUMBER];
        emptyRooms = new boolean[ROOMS_NUMBER];
        nPaintings = 0;
        sentAssaultParty = false;
        collectCanvas = false;
        partiesArrived = new int[MAX_ASSAULT_PARTY_THIEVES];
        rest = false;
        ready = false;
        nextRoom = -1;
        nextParty = -1;
        partyReady = 0;

        for (int i = 0; i < MAX_ASSAULT_PARTY_THIEVES; i++) {
            partiesArrived[i] = 0;
        }
        for (int i = 0; i < ROOMS_NUMBER; i++) {
            emptyRooms[i] = false;
        }

        Message inMessage, outMessage;
        ClientCom con = new ClientCom(hostname, 22226);
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
    }

    /**
     *
     * Master Thief appraises the situation and returns the next operation to
     * execute. The Master Thief sets its status to DECIDING_WHAT_TO_DO before
     * returning the operation.
     *
     * @param status Status of the master thief
     * @param nAssaultThievesCS Number of assault thieves in the concentration
     * site
     * @return ID of the operation to execute, 0 - takeARest, 1 -
     * prepareAssaultParty and 2 - sumUpResults
     */
    public synchronized int appraiseSit(int status, int nAssaultThievesCS) {
        reportStatusMT(status);

        nextParty = nextEmptyParty();
        nextRoom = nextEmptyRoom();

        if (nextParty == -1) {
            return 0;                                                // takeARest()
        }
        if (nAssaultThievesCS >= MAX_ASSAULT_PARTY_THIEVES) {
            if (nextRoom != -1) {
                return 1;                                            // prepareAssaultParty()
            } else if (nAssaultThievesCS != THIEVES_NUMBER) {
                return 0;                                            // takeARest()
            } else {
                return 2;                                            // sumUpResults()
            }
        } else {
            return 0;                                                // takeARest()        
        }
    }

    /**
     *
     * Assault Thief current thread sets its partyID to the assigned Assault
     * Party. The thread wakes the Master Thief if it's the last element of the
     * Assault Party to execute this operation and blocks until the Master Thief
     * finalizes executing sendAssaultParty. The Assault Thief sets its status
     * to WAITING_FOR_SENT_ASSAULT_PARTY.
     *
     * @param thiefID ID of the assault thief
     * @param status Status of the thief
     * @param maxDisp Maximum Displacement of the thief
     * @param partyID ID of the party assigned to the thief
     * @param hasCanvas Represents what the thief is carrying, 0 - empty handed,
     */
    public synchronized void prepareExcursion(int thiefID, int status, int maxDisp, int partyID, int hasCanvas) {
        reportStatusAT(thiefID, status, maxDisp, partyID, hasCanvas);

        partyReady++;

        if (partyReady == MAX_ASSAULT_PARTY_THIEVES) {
            notifyAll();
        }

        while (!sentAssaultParty) {
            try {
                wait();
            } catch (InterruptedException e) {

            }
        }

        good++;

        if (good == MAX_ASSAULT_PARTY_THIEVES) {
            sentAssaultParty = false;
            good = 0;
        }
    }

    /**
     * Master Thief sends a ready Assault Party. The Master Thief sets it's
     * status to DECIDING_WHAT_TO_DO.
     *
     * @param status Master thief status
     */
    public synchronized void sendAssaultParty(int status) {
        reportStatusMT(status);

        while (partyReady != MAX_ASSAULT_PARTY_THIEVES) {
            try {
                wait();

            } catch (InterruptedException e) {

            }
        }

        int roomToSend = nextEmptyRoom();
        roomOcupied[roomToSend] = true;
        sentAssaultParty = true;
        partyReady = 0;

        notifyAll();
    }

    /**
     * Master Thief blocks until an Assault Thief executes handACanvas and
     * AmINeeded. The Master Thief sets its status to WAITING_FOR_GROUP_ARRIVAL.
     *
     * @param status Master thief status
     */
    public synchronized void takeARest(int status) {
        reportStatusMT(status);

        rest = true;

        notifyAll();

        while (!collectCanvas) {
            try {
                wait();
            } catch (InterruptedException e) {

            }
        }

        while (!ready) {
            try {
                wait();
            } catch (InterruptedException e) {

            }
        }

        ready = false;
        collectCanvas = false;

        notifyAll();
    }

    /**
     * Get next Party available for assignment.
     *
     * @return nextParty - Next Party available for assignment
     */
    public int getNextParty() {
        return nextParty;
    }

    /**
     * Discover next empty Assault Party.
     *
     * @return the ID of the Assault Party or -1 if there is no empty Assault.
     * Party
     */
    public synchronized int nextEmptyParty() {
        Message inMessage, outMessage;
        ClientCom con = new ClientCom(map[0].get("AssaultParty0"), Integer.parseInt(map[1].get("AssaultParty0")));
        if (!con.open()) {
            return -1;
        }
        outMessage = new Message(Message.IS_EMPTY_AP);
        con.writeObject(outMessage);
        inMessage = (Message) con.readObject();
        con.close();
        if (inMessage.isB() == true) {
            return 0;
        }

        con = new ClientCom(map[0].get("AssaultParty1"), Integer.parseInt(map[1].get("AssaultParty1")));
        if (!con.open()) {
            return -1;
        }
        outMessage = new Message(Message.IS_EMPTY_AP);
        con.writeObject(outMessage);
        inMessage = (Message) con.readObject();
        con.close();
        if (inMessage.isB() == true) {
            return 1;
        }

        return -1;
    }

    /**
     * Checks for an unoccupied and empty Room.
     *
     * @return Returns the first available Room ID to raid.
     */
    public synchronized int nextEmptyRoom() {
        for (int i = 0; i < ROOMS_NUMBER; i++) {
            if (!emptyRooms[i] && !roomOcupied[i]) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Operation to wake Master Thief that the Assault Thief current Thread is
     * ready.
     *
     */
    public synchronized void isReady() {
        ready = true;

        notifyAll();
    }

    /**
     * Assault thieves hands a canvas to the Master Thief or shows up empty
     * handed.
     *
     *
     * @param thiefID ID of the assault thief
     * @param status Status of the thief
     * @param maxDisp Maximum Displacement of the thief
     * @param partyID ID of the party assigned to the thief
     * @param hasCanvas Represents what the thief is carrying, 0 - empty handed,
     * @return True if operation is sucessful or false if otherwise
     */
    public synchronized boolean handCanvas(int thiefID, int status, int maxDisp, int partyID, int hasCanvas) {
        reportStatusAT(thiefID, status, maxDisp, partyID, hasCanvas);

        while (!rest) {
            try {
                wait();
            } catch (InterruptedException e) {

            }
        }

        rest = false;

        Message inMessage, outMessage;
        ClientCom con = new ClientCom(map[0].get("AssaultParty" + String.valueOf(partyID)), Integer.parseInt(map[1].get("AssaultParty" + String.valueOf(partyID))));
        if (!con.open()) {
            return false;
        }
        outMessage = new Message(Message.GET_ROOM_ID);
        con.writeObject(outMessage);
        inMessage = (Message) con.readObject();
        con.close();

        int roomID = inMessage.getInteger0();

        if (hasCanvas == 0) {
            emptyRooms[roomID] = true;
        }
        if (hasCanvas == 1) {
            nPaintings++;
        }

        partiesArrived[partyID]++;
        if (partiesArrived[partyID] == MAX_ASSAULT_PARTY_THIEVES) {
            partiesArrived[partyID] = 0;
            roomOcupied[roomID] = false;
        }

        collectCanvas = true;

        // Reset Party
        for (int i = 0; i < MAX_ASSAULT_PARTY_THIEVES; i++) {
            con = new ClientCom(map[0].get("AssaultParty" + String.valueOf(partyID)), Integer.parseInt(map[1].get("AssaultParty" + String.valueOf(partyID))));
            if (!con.open()) {
                return false;
            }
            outMessage = new Message(Message.GET_PTHIEVES);
            con.writeObject(outMessage);
            inMessage = (Message) con.readObject();
            con.close();

            int[] pthieves = inMessage.getPartyThieves();

            if (pthieves[i] == thiefID) {
                con = new ClientCom(map[0].get("AssaultParty" + String.valueOf(partyID)), Integer.parseInt(map[1].get("AssaultParty" + String.valueOf(partyID))));
                if (!con.open()) {
                    return false;
                }
                outMessage = new Message(Message.SET_PTHIEVES, i, -1);
                con.writeObject(outMessage);
                inMessage = (Message) con.readObject();
                con.close();
            }
        }

        notifyAll();

        return true;
    }

    /**
     * Master Thief collects a canvas. The Master Thief sets its status to
     * DECIDING_WHAT_TO_DO.
     *
     * @param status Master thief status
     */
    public synchronized void collectCanvas(int status) {
        reportStatusMT(status);
        collectCanvas = false;

        notifyAll();

    }

    /**
     * Master Thief presents the final heist report. The Master Thief sets its
     * status to PRESENTING_THE_REPORT.
     *
     * @param status Master thief status
     */
    public synchronized void sumUpResults(int status) {
        reportStatusMT(status);
        System.out.println("Got " + nPaintings + " paintings!");
    }

    /**
     * Set the next Assault Party ID to be assigned.
     *
     * @param nextParty Next party ID to be assigned
     */
    public void setNextParty(int nextParty) {
        this.nextParty = nextParty;
    }

    /**
     * Set the next Room ID to be assigned.
     *
     * @param nextRoom Next room ID to be assigned
     */
    public void setNextRoom(int nextRoom) {
        this.nextRoom = nextRoom;
    }

    /**
     * Get the ID of the next Room to be assigned.
     *
     * @return nextRoom - next Room ID to be assigned
     */
    public int getNextRoom() {
        return nextRoom;
    }

    /**
     * Get the number of paintings collected to the moment.
     *
     * @return nPaintings - Number of paintings collected to the moment.
     */
    public int getnPaintings() {
        return nPaintings;
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
        outMessage = new Message(REP_STATUS_AT, thiefID, status, maxDisp, partyID, hasCanvas);     
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();
        con.close();

        if (inMessage.getType() == ACK) {
            return true;                                          
        }

        return false;
    }

    /**
     *
     * @return true if successfully logged status or false if otherwise
     */
    private boolean reportStatusMT(int status) {
        Message inMessage, outMessage;
        ClientCom con = new ClientCom(map[0].get("Logger"), Integer.parseInt(map[1].get("Logger")));

        if (!con.open()) {
            return false;
        }
        outMessage = new Message(REP_STATUS_MT, status);      
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();
        con.close();

        return inMessage.getType() == ACK;
    }
}
