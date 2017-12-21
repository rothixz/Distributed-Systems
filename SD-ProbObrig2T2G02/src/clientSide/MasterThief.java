package clientSide;

import static auxiliary.constants.States.*;
import auxiliary.messages.Message;
import static auxiliary.messages.Message.*;
import genclass.GenericIO;
import java.util.HashMap;

/**
 *
 * @author António Mota
 * @author Marcos Pires
 */
public class MasterThief extends Thread {

    private int status;

    private String serverHostName = null;
    private HashMap<String, String>[] map;

    /**
     *
     * @param hostName
     * @param map
     */
    public MasterThief(String hostName, HashMap<String, String>[] map) {
        status = PLANNING_THE_HEIST;

        serverHostName = hostName;
        this.map = map;
    }

    /**
     *
     */
    @Override
    public void run() {
        boolean heistend = false;
        startOfOperations();
        while (!heistend) {
            switch (appraiseSit(getnAssaultThievesCS())) {
                case 1:
                    prepareAssaultParty();
                    sendAssaultParty();
                    break;
                case 0:
                    takeARest();
                    collectCanvas();
                    break;
                case 2:
                    sumUpResults();
                    heistend = true;
                    break;
            }
        }
    }

    private boolean startOfOperations() {
        status = DECIDING_WHAT_TO_DO;

        Message inMessage, outMessage;
        ClientCom con = new ClientCom(map[0].get("ConcentrationSite"), Integer.parseInt(map[1].get("ConcentrationSite")));
        if (!con.open()) {
            return false;
        }
        outMessage = new Message(STARTOP, status);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();
        if (inMessage.getType() != ACK) {
            GenericIO.writelnString("Thread " + getName() + ": Tipo inválido!");
            GenericIO.writelnString(inMessage.toString());
            System.exit(1);
        }

        return false;
    }

    private int getnAssaultThievesCS() {
        Message inMessage, outMessage;
        ClientCom con = new ClientCom(map[0].get("ConcentrationSite"), Integer.parseInt(map[1].get("ConcentrationSite")));

        if (!con.open()) {
            return -1;
        }
        outMessage = new Message(GET_ASSAULT_THIEVES_CS, status);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();
        if (inMessage.getType() != ACK) {
            GenericIO.writelnString("Thread " + getName() + ": Tipo inválido!");
            GenericIO.writelnString(inMessage.toString());
            System.exit(1);
        }
        con.close();

        return inMessage.getInteger0();
    }

    private int appraiseSit(int nAssaultThievesCS) {
        status = DECIDING_WHAT_TO_DO;

        Message inMessage, outMessage;
        ClientCom con;

        con = new ClientCom(map[0].get("ControlCollectionSite"), Integer.parseInt(map[1].get("ControlCollectionSite")));
        if (!con.open()) {
            return -1;
        }
        outMessage = new Message(APPRAISE_SIT, status, nAssaultThievesCS);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();
        if (inMessage.getType() != ACK) {
            GenericIO.writelnString("Thread " + getName() + ": Tipo inválido!");
            GenericIO.writelnString(inMessage.toString());
            System.exit(1);
        }
        con.close();

        return inMessage.getInteger0();
    }

    private boolean prepareAssaultParty() {
        status = ASSEMBLING_A_GROUP;

        Message inMessage, outMessage;
        ClientCom con = new ClientCom(map[0].get("ConcentrationSite"), Integer.parseInt(map[1].get("ConcentrationSite")));

        if (!con.open()) {
            return false;
        }
        outMessage = new Message(PREPARE_AP, status);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();
        if (inMessage.getType() != ACK) {
            GenericIO.writelnString("Thread " + getName() + ": Tipo inválido!");
            GenericIO.writelnString(inMessage.toString());
            System.exit(1);
        }
        con.close();

        return false;
    }

    private boolean sendAssaultParty() {
        status = DECIDING_WHAT_TO_DO;

        Message inMessage, outMessage;
        ClientCom con = new ClientCom(map[0].get("ControlCollectionSite"), Integer.parseInt(map[1].get("ControlCollectionSite")));
        if (!con.open()) {
            return false;
        }
        outMessage = new Message(SENDAP, status);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();
        if (inMessage.getType() != ACK) {
            GenericIO.writelnString("Thread " + getName() + ": Tipo inválido!");
            GenericIO.writelnString(inMessage.toString());
            System.exit(1);
        }

        return true;
    }

    private boolean takeARest() {
        status = WAITING_FOR_GROUP_ARRIVAL;

        Message inMessage, outMessage;
        ClientCom con = new ClientCom(map[0].get("ControlCollectionSite"), Integer.parseInt(map[1].get("ControlCollectionSite")));
        if (!con.open()) {
            return false;
        }
        outMessage = new Message(TAKE_A_REST, status);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();
        if (inMessage.getType() != ACK) {
            GenericIO.writelnString("Thread " + getName() + ": Tipo inválido!");
            GenericIO.writelnString(inMessage.toString());
            System.exit(1);
        }

        return true;
    }

    private boolean collectCanvas() {
        status = DECIDING_WHAT_TO_DO;

        Message inMessage, outMessage;
        ClientCom con = new ClientCom(map[0].get("ControlCollectionSite"), Integer.parseInt(map[1].get("ControlCollectionSite")));
        if (!con.open()) {
            return false;
        }
        outMessage = new Message(COLLECT_CANVAS, status);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();
        if (inMessage.getType() != ACK) {
            GenericIO.writelnString("Thread " + getName() + ": Tipo inválido!");
            GenericIO.writelnString(inMessage.toString());
            System.exit(1);
        }

        return true;
    }

    private boolean sumUpResults() {
        status = PRESENTING_THE_REPORT;

        Message inMessage, outMessage;
        ClientCom con = new ClientCom(map[0].get("ControlCollectionSite"), Integer.parseInt(map[1].get("ControlCollectionSite")));
        if (!con.open()) {
            return false;
        }
        outMessage = new Message(SUM_UP_RESULTS, status);
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
