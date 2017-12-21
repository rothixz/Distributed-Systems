package serverSide.ConcentrationSite;

import static auxiliary.constants.Constants.*;
import auxiliary.memFIFO.MemFIFO;
import auxiliary.messages.Message;
import static auxiliary.messages.Message.*;
import clientSide.ClientCom;
import java.util.HashMap;

/**
 * This data type represents the Concentration Site of the Heist to the Museum
 * Problem.
 *
 * @author António Mota
 * @author Marcos Pires
 */
public class ConcentrationSite {

    private MemFIFO waitQueue;                     // Waiting queue for ready assault thieves 
    private MemFIFO waitQueueDisp;                 //´Waiting queue for ready assault thieves displacement   
    private int nAssaultThievesCS;                 // Number of assault thieves in the concentration site
    private int partyID;
    private String hostname = CONFIG_PC_NAME;
    private final HashMap<String, String>[] map;

    public ConcentrationSite() {
        waitQueue = new MemFIFO(THIEVES_NUMBER);
        waitQueueDisp = new MemFIFO(THIEVES_NUMBER);
        nAssaultThievesCS = 0;

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
     * Master Thief blocks until the number of Assault Thieves in the
     * Concentration Site is equal to the number of total Assault Thieves of the
     * heist. The status of the Master Thief is changed to DECIDING_WHAT_TO_DO
     * in the end of the operation.
     *
     * @param status Master thief status
     */
    public synchronized void startOfOperations(int status) {
        reportStatusMT(status);

        while (nAssaultThievesCS != THIEVES_NUMBER) {
            try {
                wait();
            } catch (InterruptedException e) {

            }
        }
    }

    /**
     * Resets the Assault Thief current thread, adds it's reference to the
     * waiting queue and blocks it until the Master Thief executes
     * prepareAssaultParty or the heist ends. The status of the Assault Thief
     * current thread is changed to OUTSIDE in the end of the operation.
     *
     * @param thiefID ID of the assault thief
     * @param status Status of the thief
     * @param maxDisp Maximum Displacement of the thief
     * @param partyID ID of the party assigned to the thief
     * @param hasCanvas Represents what the thief is carrying, 0 - empty handed,
     * @return partyID - ID of the party assigned to the thief
     */
    public synchronized int amINeeded(int thiefID, int status, int maxDisp, int partyID, int hasCanvas) {
        reportStatusAT(thiefID, status, maxDisp, partyID, hasCanvas);

        nAssaultThievesCS++;
        waitQueue.write(thiefID);
        waitQueueDisp.write(maxDisp);

        Message inMessage, outMessage;
        ClientCom con = new ClientCom(map[0].get("ControlCollectionSite"), Integer.parseInt(map[1].get("ControlCollectionSite")));
        if (!con.open()) {
            return -1;
        }
        outMessage = new Message(Message.ISREADY);
        con.writeObject(outMessage);
        inMessage = (Message) con.readObject();
        con.close();

        notifyAll();

        while (!inParty(thiefID)) {
            try {
                con = new ClientCom(map[0].get("ControlCollectionSite"), Integer.parseInt(map[1].get("ControlCollectionSite")));
                if (!con.open()) {
                    return -1;
                }
                outMessage = new Message(Message.NEXT_EMPTY_ROOM);
                con.writeObject(outMessage);
                inMessage = (Message) con.readObject();
                con.close();

                if (inMessage.getInteger0() == -1 && nAssaultThievesCS == THIEVES_NUMBER) {
                    return -1;
                }
                wait();
            } catch (InterruptedException e) {

            }
        }

        return this.partyID;
    }

    /**
     * Checks if the Assault Thief current thread is in the Assault Party.
     *
     * @param thiefID ID of the assault thief
     * @return True, if the Assault Thief current thread is in the Assault Party
     * or false if otherwise.
     */
    public synchronized boolean inParty(int thiefID) {
        for (int i = 0; i < MAX_ASSAULT_PARTIES; i++) {
            Message inMessage, outMessage;
            ClientCom con = new ClientCom(map[0].get("AssaultParty" + String.valueOf(i)), Integer.parseInt(map[1].get("AssaultParty" + String.valueOf(i))));
            if (!con.open()) {
                return false;
            }
            outMessage = new Message(Message.GET_PTHIEVES);
            con.writeObject(outMessage);
            inMessage = (Message) con.readObject();
            con.close();

            int[] pthieves = inMessage.getPartyThieves();
            for (int j = 0; j < MAX_ASSAULT_PARTY_THIEVES; j++) {
                if (pthieves[j] == thiefID) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Master Thief prepares and Assault Party. It adds elements to the Assault
     * Party and then assigns it a room and the turn of the element to first
     * crawl in. The status of the Master Thief is changed to ASSEMBLING_A_GROUP
     * in the end of the operation.
     *
     * @param status Master thief status
     * @return True if the operation was sucessful or false if otherwise
     */
    public synchronized boolean prepareAssaultParty(int status) {
        reportStatusMT(status);

        Message inMessage, outMessage;
        ClientCom con = new ClientCom(map[0].get("ControlCollectionSite"), Integer.parseInt(map[1].get("ControlCollectionSite")));
        if (!con.open()) {
            return false;
        }
        outMessage = new Message(Message.GET_NEXT_AP);
        con.writeObject(outMessage);
        inMessage = (Message) con.readObject();
        con.close();

        partyID = inMessage.getInteger0();
        for (int i = 0; i < MAX_ASSAULT_PARTY_THIEVES; i++) {
            con = new ClientCom(map[0].get("AssaultParty" + String.valueOf(partyID)), Integer.parseInt(map[1].get("AssaultParty" + String.valueOf(partyID))));
            if (!con.open()) {
                return false;
            }
            outMessage = new Message(Message.ADDTHIEF, (int) waitQueue.read(), (int) waitQueueDisp.read());
            con.writeObject(outMessage);
            inMessage = (Message) con.readObject();
            con.close();

            nAssaultThievesCS--;
        }

        con = new ClientCom(map[0].get("AssaultParty" + String.valueOf(partyID)), Integer.parseInt(map[1].get("AssaultParty" + String.valueOf(partyID))));
        if (!con.open()) {
            return false;
        }
        outMessage = new Message(Message.SETFIRST);
        con.writeObject(outMessage);
        inMessage = (Message) con.readObject();
        con.close();

        notifyAll();
        con = new ClientCom(map[0].get("ControlCollectionSite"), Integer.parseInt(map[1].get("ControlCollectionSite")));
        if (!con.open()) {
            return false;
        }
        outMessage = new Message(Message.GET_NEXT_ROOM);
        con.writeObject(outMessage);
        inMessage = (Message) con.readObject();
        con.close();

        int roomID = inMessage.getInteger0();

        con = new ClientCom(map[0].get("AssaultParty" + String.valueOf(partyID)), Integer.parseInt(map[1].get("AssaultParty" + String.valueOf(partyID))));
        if (!con.open()) {
            return false;
        }
        outMessage = new Message(Message.SET_AP_ROOM, roomID);
        con.writeObject(outMessage);
        inMessage = (Message) con.readObject();
        con.close();

        return true;
    }

    /**
     * Get the number of Assault Thieves in Concentration Site.
     *
     * @return Number of Assault Thieves in Concentration Site
     */
    public int getnAssaultThievesCS() {
        return nAssaultThievesCS;
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

        if (inMessage.getType() == ACK) {
            return true;                                                // operação bem sucedida - corte efectuado
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
        outMessage = new Message(REP_STATUS_MT, status);        // pede a realização do serviço
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();
        con.close();

        if (inMessage.getType() == ACK) {
            return true;                                                // operação bem sucedida - corte efectuado
        }

        return false;
    }
}
