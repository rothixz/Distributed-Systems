package serverSide;

import static auxiliary.constants.Constants.*;
import auxiliary.time.TimeStamp;
import interfaces.AssaultPartyInterface;
import interfaces.ConcentrationSiteInterface;
import interfaces.ControlCollectionSiteInterface;
import interfaces.LoggerInterface;
import interfaces.MuseumInterface;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import auxiliary.time.Room;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This data type represents the Assault Party of the Heist to the Museum
 * Problem.
 *
 * @author António Mota
 * @author Marcos Pires
 */
public class AssaultParty implements AssaultPartyInterface {

    /**
     * ID of the assault party
     */
    private int id;
    /**
     * ID of room assigned
     */
    private int roomID;
    /**
     * Elements assigned
     */
    private int[] partyThieves;
    /**
     * Position of the elements
     */
    private int[] partyThievesPos;
    /**
     * Maximum displacement of the elements
     */
    private int[] partyThievesMaxDisp;
    /**
     * Turns of the elements
     */
    private boolean[] myTurn;
    /**
     * Elements in room
     */
    private boolean[] inRoom;
    /**
     * Number of assault thieves in room
     */
    private int nThievesRoom;
    /**
     * Counter to inform all elements are Ready to crawlOut()
     */
    private int reverse;

    private TimeStamp ts;

    private String rmiRegHostName = "localhost";
    private int rmiRegPortNumb = 22350;

    LoggerInterface li = null;
    MuseumInterface mi = null;
    ConcentrationSiteInterface csi = null;
    ControlCollectionSiteInterface ccsi = null;
    AssaultPartyInterface[] api = new AssaultPartyInterface[MAX_ASSAULT_PARTIES];

    String loggerEntryBase = "LoggerInt";
    String museumEntryBase = "MuseumInt";
    String csEntryBase = "ConcentrationSiteInt";
    String ccsEntryBase = "ControlCollectionSiteInt";
    String ass0EntryBase = "AssaultParty0Int";
    String ass1EntryBase = "AssaultParty1Int";

    /**
     *
     * @param id ID of the assault party
     */
    public AssaultParty(int id) {
        this.id = id;
        roomID = -1;
        partyThieves = new int[MAX_ASSAULT_PARTY_THIEVES];
        partyThievesPos = new int[MAX_ASSAULT_PARTY_THIEVES];
        partyThievesMaxDisp = new int[MAX_ASSAULT_PARTY_THIEVES];
        myTurn = new boolean[MAX_ASSAULT_PARTY_THIEVES];
        inRoom = new boolean[MAX_ASSAULT_PARTY_THIEVES];
        nThievesRoom = 0;
        reverse = 0;

        // Empty assault party
        for (int i = 0; i < MAX_ASSAULT_PARTY_THIEVES; i++) {
            partyThieves[i] = -1;
            partyThievesPos[i] = -1;
            partyThievesMaxDisp[i] = -1;
            myTurn[i] = false;
            inRoom[i] = false;
        }

        ts = new TimeStamp();
    }
    
    public void callInterfaces(){
        try {
            Registry registry = LocateRegistry.getRegistry(rmiRegHostName, rmiRegPortNumb);
            li = (LoggerInterface) registry.lookup(loggerEntryBase);
        } catch (RemoteException e) {
            System.out.println("Exception thrown while locating logger: " + e.getMessage() + "!");
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("Logger is not registered: " + e.getMessage() + "!");
            System.exit(1);
        }
        try {
            Registry registry = LocateRegistry.getRegistry(rmiRegHostName, rmiRegPortNumb);
            mi = (MuseumInterface) registry.lookup(museumEntryBase);
        } catch (RemoteException e) {
            System.out.println("Exception thrown while locating museum: " + e.getMessage() + "!");
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("Museum is not registered: " + e.getMessage() + "!");
            System.exit(1);
        }

        try {
            Registry registry = LocateRegistry.getRegistry(rmiRegHostName, rmiRegPortNumb);
            ccsi = (ControlCollectionSiteInterface) registry.lookup(ccsEntryBase);
        } catch (RemoteException e) {
            System.out.println("Exception thrown while locating concentration site: " + e.getMessage() + "!");
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("ControlCollectionSite is not registered: " + e.getMessage() + "!");
            System.exit(1);
        }

    }

    /**
     *
     * Simulates the movement crawlIn of the Assault Thief current thread.
     *
     *
     * @param thiefID ID of the assault thief
     * @param status Status of the thief
     * @param maxDisp Maximum Displacement of the thief
     * @param partyID ID of the party assigned to the thief
     * @param hasCanvas Represents what the thief is carrying, 0 - empty handed,
     * @param ts
     * @return True if operation is sucessful or false if otherwise
     */
    @Override
    public synchronized TimeStamp crawlIn(TimeStamp ts, int thiefID, int status, int maxDisp, int partyID, int hasCanvas) {
        this.ts.update(ts);
        reportStatusAT(thiefID, status, maxDisp, partyID, hasCanvas);

        while (partyThievesPos[getIndexParty(thiefID)] != getDistOutsideRoom()) {
            while (!myTurn[getIndexParty(thiefID)]) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    System.out.println(ex.getMessage());
                }
            }

            int myIndex = getIndexParty(thiefID);
            int myPos = partyThievesPos[myIndex];
            int myAgility = partyThievesMaxDisp[myIndex];
            int[] assaultThievesPos = new int[MAX_ASSAULT_PARTY_THIEVES - 1];

            int count = 0;
            int i = 0;
            for (i = 0; i < MAX_ASSAULT_PARTY_THIEVES; i++) {
                if (i != myIndex) {
                    assaultThievesPos[count] = partyThievesPos[i];
                    count++;
                }
            }

            Arrays.sort(assaultThievesPos);

            // Predict maximum displacement
            for (i = myAgility; i > 0; i--) {
                boolean tooFarOrOcupada = false;
                // Array que vai ter myPos no inicio e assaultThievesPos de seguida
                int[] posAfterMove = new int[assaultThievesPos.length + 1];
                posAfterMove[0] = myPos + i;
                System.arraycopy(assaultThievesPos, 0, posAfterMove, 1, assaultThievesPos.length);
                Arrays.sort(posAfterMove);

                for (int j = 0; j < posAfterMove.length - 1; j++) {
                    if ((posAfterMove[j + 1] - posAfterMove[j] > THIEVES_MAX_DISTANCE) || (posAfterMove[j + 1] - posAfterMove[j] == 0 && (posAfterMove[j + 1] != 0 && posAfterMove[j + 1] != getDistOutsideRoom()))) { //ultima condicao deve ser alterada
                        tooFarOrOcupada = true;
                        break;
                    }
                }

                // Set new position
                if ((!tooFarOrOcupada)) {
                    if (myPos + i >= getDistOutsideRoom()) {
                        partyThievesPos[myIndex] = getDistOutsideRoom();
                        nThievesRoom++;
                        inRoom[myIndex] = true;
                        reportStatusAP();
                    } else {
                        partyThievesPos[myIndex] = myPos + i;
                        reportStatusAP();
                    }

                    break;
                }
            }

            // Check if it is possible to move even further
            boolean canMoveAgain = false;
            if (!(myPos == partyThievesPos[myIndex] || inRoom[myIndex])) {
                for (i = partyThievesMaxDisp[myIndex]; i > 0; i--) {
                    boolean tooFarOrOcupada = false;
                    int[] posAfterMove = new int[assaultThievesPos.length + 1];
                    posAfterMove[0] = myPos + i;
                    System.arraycopy(assaultThievesPos, 0, posAfterMove, 1, assaultThievesPos.length);
                    Arrays.sort(posAfterMove);

                    for (int j = 0; j < posAfterMove.length - 1 && posAfterMove[j] != 0; j++) {
                        if ((posAfterMove[j + 1] - posAfterMove[j] > THIEVES_MAX_DISTANCE) || (posAfterMove[j + 1] - posAfterMove[j] == 0 && (posAfterMove[j + 1] != 0 && posAfterMove[j + 1] != getDistOutsideRoom()) && !(nThievesRoom == MAX_ASSAULT_PARTY_THIEVES - 1))) { //ultima condicao deve ser alterada
                            tooFarOrOcupada = true;
                            break;
                        }
                    }

                    if ((!tooFarOrOcupada)) {
                        canMoveAgain = true;
                        break;
                    }
                }
                // Didn't get to room or can't walk further
            } else if (!canMoveAgain) {
                myTurn[myIndex] = false;

                int min = ROOM_MAX_DISTANCE;
                int minIndex = -1;

                for (int x = 0; x < MAX_ASSAULT_PARTY_THIEVES; x++) {
                    if (min >= partyThievesPos[x]) {
                        min = partyThievesPos[x];
                        minIndex = x;
                    }
                }

                if (minIndex == myIndex) {
                    boolean changed = true;
                    while (changed) {
                        changed = false;
                        for (int x = 0; x < MAX_ASSAULT_PARTY_THIEVES; x++) {

                            if (partyThievesPos[minIndex] + 1 == partyThievesPos[x] && partyThievesMaxDisp[minIndex] == 2) {
                                if (partyThievesPos[minIndex] + 1 == getDistOutsideRoom()) {
                                    continue;
                                }
                                minIndex = x;
                                changed = true;
                            }
                        }
                    }

                }
                myTurn[minIndex] = true;

                notifyAll();
            }
        }
        this.ts.setBool(true);
        
        return this.ts.clone();
    }

    /**
     *
     * Simulates the operation reverseDirection of the Assault Party. The
     * Assault Thief current thread blocks until the last element of the Assault
     * Party executes this action.
     *
     * @param thiefID ID of the assault thief
     * @param status Status of the thief
     * @param maxDisp Maximum Displacement of the thief
     * @param partyID ID of the party assigned to the thief
     * @param hasCanvas Represents what the thief is carrying, 0 - empty handed,
     * @param ts
     * @return
     */
    @Override
    public synchronized TimeStamp reverseDirection(TimeStamp ts, int thiefID, int status, int maxDisp, int partyID, int hasCanvas) {
        this.ts.update(ts);
        reportStatusAT(thiefID, status, maxDisp, partyID, hasCanvas);

        myTurn[getIndexParty(thiefID)] = false;
        inRoom[getIndexParty(thiefID)] = false;

        if (nThievesRoom == MAX_ASSAULT_PARTY_THIEVES) {
            nThievesRoom = 0;
        }

        myTurn[0] = true;

        reverse++;
        while (reverse % MAX_ASSAULT_PARTY_THIEVES != 0 && reverse != 0) {
            try {
                wait();
            } catch (InterruptedException ex) {
                System.out.println(ex.getMessage());
            }
        }

        notifyAll();
        return this.ts.clone();
    }

    /**
     *
     * Simulates the movement crawlOut of the Assault Thief current thread.
     *
     * @param thiefID ID of the assault thief
     * @param status Status of the thief
     * @param maxDisp Maximum Displacement of the thief
     * @param partyID ID of the party assigned to the thief
     * @param hasCanvas Represents what the thief is carrying, 0 - empty handed,
     * @param ts
     * @return True if operation is sucessful or false if otherwise
     */
    @Override
    public synchronized TimeStamp crawlOut(TimeStamp ts, int thiefID, int status, int maxDisp, int partyID, int hasCanvas) {
        this.ts.update(ts);
        reportStatusAT(thiefID, status, maxDisp, partyID, hasCanvas);

        while (partyThievesPos[getIndexParty(thiefID)] != 0) {
            while (!myTurn[getIndexParty(thiefID)]) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    System.out.println(ex.getMessage());
                }
            }

            int myIndex = getIndexParty(thiefID);
            int myPos = partyThievesPos[myIndex];
            int myAgility = partyThievesMaxDisp[myIndex];
            int[] assaultThievesPos = new int[MAX_ASSAULT_PARTY_THIEVES - 1];

            int count = 0;
            int i = 0;
            for (i = 0; i < MAX_ASSAULT_PARTY_THIEVES; i++) {
                if (i != myIndex) {
                    assaultThievesPos[count] = partyThievesPos[i];
                    count++;
                }
            }

            Arrays.sort(assaultThievesPos);

            /// Predict maximum displacement
            for (i = myAgility; i > 0; i--) {
                boolean tooFarOrOcupada = false;
                // Array que vai ter myPos no inicio e assaultThievesPos de seguida
                int[] posAfterMove = new int[assaultThievesPos.length + 1];
                posAfterMove[0] = myPos - i;
                System.arraycopy(assaultThievesPos, 0, posAfterMove, 1, assaultThievesPos.length);
                Arrays.sort(posAfterMove);

                for (int j = 0; j < posAfterMove.length - 1; j++) {
                    if ((posAfterMove[j + 1] - posAfterMove[j] > THIEVES_MAX_DISTANCE) || (posAfterMove[j + 1] - posAfterMove[j] == 0 && (posAfterMove[j + 1] != getDistOutsideRoom() && posAfterMove[j + 1] != 0) && !(nThievesRoom == MAX_ASSAULT_PARTY_THIEVES - 1))) {
                        tooFarOrOcupada = true;
                        break;
                    }
                }

                // Set new position
                if ((!tooFarOrOcupada)) {
                    if (myPos - i <= 0) {
                        partyThievesPos[myIndex] = 0;
                        nThievesRoom++;
                        inRoom[myIndex] = true;
                        reportStatusAP();

                    } else {
                        partyThievesPos[myIndex] = myPos - i;
                        reportStatusAP();
                    }

                    break;
                }
            }

            // Check if it is possible to move even further
            boolean canMoveAgain = false;
            if (!(myPos == partyThievesPos[myIndex] || inRoom[myIndex])) {
                for (i = partyThievesMaxDisp[myIndex]; i > 0; i--) {
                    boolean tooFarOrOcupada = false;
                    int[] posAfterMove = new int[assaultThievesPos.length + 1];
                    posAfterMove[0] = myPos - i;
                    System.arraycopy(assaultThievesPos, 0, posAfterMove, 1, assaultThievesPos.length);
                    Arrays.sort(posAfterMove);

                    for (int j = 0; j < posAfterMove.length - 1 && posAfterMove[j] != 0; j++) {
                        if ((posAfterMove[j + 1] - posAfterMove[j] > THIEVES_MAX_DISTANCE) || (posAfterMove[j + 1] - posAfterMove[j] == 0 && (posAfterMove[j + 1] != getDistOutsideRoom() && posAfterMove[j + 1] != 0) && !(nThievesRoom == MAX_ASSAULT_PARTY_THIEVES - 1))) {
                            tooFarOrOcupada = true;
                            break;
                        }
                    }

                    if ((!tooFarOrOcupada)) {
                        canMoveAgain = true;
                        break;
                    }
                }
                // Didn't get to room or can't walk further
            } else if (!canMoveAgain) {
                myTurn[myIndex] = false;

                int max = 0;
                int maxIndex = -1;

                for (int x = 0; x < MAX_ASSAULT_PARTY_THIEVES; x++) {
                    if (max <= partyThievesPos[x]) {
                        max = partyThievesPos[x];
                        maxIndex = x;
                    }
                }

                if (maxIndex == myIndex) {
                    boolean changed = true;
                    while (changed) {
                        changed = false;
                        for (int x = 0; x < MAX_ASSAULT_PARTY_THIEVES; x++) {
                            if (partyThievesPos[maxIndex] - 1 == partyThievesPos[x] && partyThievesMaxDisp[maxIndex] == 2) {
                                if (partyThievesPos[maxIndex] - 1 == 0) {
                                    continue;
                                }
                                maxIndex = x;
                                changed = true;
                            }
                        }
                    }
                }

                myTurn[maxIndex] = true;

                notifyAll();
            }
        }

        this.ts.setBool(true);
        return this.ts.clone();
    }

    /**
     * Add an Assault Thief to the Assault Party.
     *
     * @param thiefID ID of the assault thief
     * @param maxDisp Maximum Displacement of the thief
     * @return True, if the operation was successful or false if otherwise
     */
    @Override
    public synchronized TimeStamp addThief(TimeStamp ts, int thiefID, int maxDisp) {
        this.ts.update(ts);

        for (int i = 0; i < MAX_ASSAULT_PARTY_THIEVES; i++) {
            if (partyThieves[i] == -1) {
                partyThieves[i] = thiefID;
                partyThievesPos[i] = 0;
                partyThievesMaxDisp[i] = maxDisp;

                reportStatusAP();

                this.ts.setBool(true);
                return this.ts.clone();
            }
        }

        this.ts.setBool(true);
        return this.ts.clone();
    }

    /**
     * Get index of current Assault Thief thread in the partyThieves array.
     *
     * @param thiefID ID of the assault thief
     * @return Returns the index of the current Assault Thief thread in the
     * partyThieves array
     */
    public synchronized int getIndexParty(int thiefID) {

        for (int i = 0; i < MAX_ASSAULT_PARTY_THIEVES; i++) {
            if (partyThieves[i] == thiefID) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Set the turn to crawl of the element of index 0 to true.
     *
     */
    @Override
    public TimeStamp setFirst(TimeStamp ts) {
        this.ts.update(ts);

        myTurn[0] = true;

        return this.ts.clone();
    }

    /**
     * Set the ID of an Assault Thief in the current Assault Party.
     *
     * @param i index on the party array
     * @param value ID of the thief to set in the party
     */
    @Override
    public TimeStamp setPartyThieves(TimeStamp ts, int i, int value) {
        this.ts.update(ts);
        
        partyThieves[i] = value;

        return this.ts.clone();
    }

    /**
     * Get Assault Thieves ID of the current Assault Party.
     *
     * @return partyThieves - array of the current assault party elements ID
     */
    @Override
    public TimeStamp getPartyThieves(TimeStamp ts) {
        this.ts.update(ts);

        this.ts.setInteger_array(partyThieves);

        return this.ts.clone();
    }

    /**
     * Get Assault Thieves positions of the current Assault Party.
     *
     * @return partyThievesPos - array of the current assault party elements
     * positions
     */
    public int[] getPartyThievesPos() {
        return partyThievesPos;
    }

    /**
     *
     * @return True if the party is empty or false if otherwise
     */
    @Override
    public TimeStamp isEmptyAP(TimeStamp ts) {
        this.ts.update(ts);

        for (int i = 0; i < MAX_ASSAULT_PARTY_THIEVES; i++) {
            if (partyThieves[i] != -1) {
                this.ts.setBool(false);
                return this.ts.clone();
            }
        }

        this.ts.setBool(true);
        return this.ts.clone();
    }

    /**
     * Get Assault Thieves maximum displacement of the current Assault Party.
     *
     * @return partyThievesMaxDisp - array of the current assault party elements
     * maximum displacement
     */
    public int[] getPartyThievesMaxDisp() {
        return partyThievesMaxDisp;
    }

    /**
     *
     * Set a roomID to this Assault Party.
     *
     * @param roomID ID of the room the be assigned to the party
     */
    @Override
    public TimeStamp setRoom(TimeStamp ts, int roomID) {
        this.ts.update(ts);

        this.roomID = roomID;

        return this.ts.clone();
    }

    /**
     * Get the roomID assigned to the current Assault Party.
     *
     * @param ts
     * @return roomID - ID of the room the be assigned to the party
     */
    @Override
    public TimeStamp getRoomID(TimeStamp ts) {
        this.ts.update(ts);

        this.ts.setInteger(roomID);

        return this.ts.clone();
    }

    /**
     * +
     * Get distance from the Outside to the Room assigned to this Assault Party
     *
     * @return distance from the Outside to the Room assigned to this party
     */
    public int getDistOutsideRoom() {
        callInterfaces();
        try {
            this.ts.update(mi.getRoom(this.ts.clone(), roomID));
        } catch (RemoteException ex) {
            Logger.getLogger(AssaultParty.class.getName()).log(Level.SEVERE, null, ex);
        }

        return this.ts.getRoom().getDistOutside();
    }

    /**
     *
     * @return true if successfully logged status or false if otherwise
     */
    private boolean reportStatusAP() {
        /*Message inMessage, outMessage;
        ClientCom con = new ClientCom(map[0].get("Logger"), Integer.parseInt(map[1].get("Logger")));

        if (!con.open()) {
            return false;
        }
        outMessage = new Message(REP_STATUS_AP, id, partyThieves, partyThievesPos, roomID);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();
        con.close();

        if (inMessage.getType() == ACK) {
            return true;
        }
         */
        return false;
    }

    /**
     *
     * @return true if successfully logged status or false if otherwise
     */
    private boolean reportStatusAT(int thiefID, int status, int maxDisp, int partyID, int hasCanvas) {
        /* Message inMessage, outMessage;
        ClientCom con = new ClientCom(map[0].get("Logger"), Integer.parseInt(map[1].get("Logge
        if (inMessage.getType() r")));

        if (!con.open()) {
            return false;
        }
        outMessage = new Message(REP_STATUS_AT, thiefID, status, maxDisp, partyID, hasCanvas);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();
        con.close();

        if (inMessage.getType() == ACK) {
            return true;
        }*/

        return false;
    }

    @Override
    public void signalShutdown() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
