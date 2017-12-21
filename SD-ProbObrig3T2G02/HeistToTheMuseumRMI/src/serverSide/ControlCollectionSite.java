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
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import auxiliary.time.Room;

/**
 * This data type represents the Control and Collection Site of the Heist to the
 * Museum Problem.
 *
 * @author António Mota
 * @author Marcos Pires
 */
public class ControlCollectionSite implements ControlCollectionSiteInterface {

    /**
     * Needed to know which parties have arrived
     */
    private int[] partiesArrived;
    /**
     * Represents the status of each room (occupied or clear)
     */
    private boolean[] roomOcupied;
    /**
     * Represents the emptiness of each room (empty or still with paintings)
     */
    private boolean[] emptyRooms;
    /**
     * Total number of collected paintings
     */
    private int nPaintings;
    /**
     * Next room to rob
     */
    private int nextRoom;
    /**
     * Next party to prepare
     */
    private int nextParty;

    /**
     * Flag for operations sendAssaultParty and prepareExcursion
     */
    private int partyReady;
    /**
     * Flag for operations sendAssaultParty and prepareExcursion
     */
    private int good = 0;
    /**
     * Flag for operations sendAssaultParty and prepareExcursion referring to
     * whether an assault party has been sent or not.
     */
    private boolean sentAssaultParty;

    /**
     * Flag for operations handACanvas, collectCanvas and takeARest.
     */
    private boolean rest;
    /**
     * Flag for operations handACanvas, collectCanvas and takeARest. Boolean
     * value which meaning refers to whether the Assault Thief current Thread is
     * ready or not.
     */
    private boolean ready;
    /**
     * Flag for operations handACanvas, collectCanvas and takeARest. Boolean
     * value which meaning refers to whether an assault thief had a canvas to
     * give when the collectCanvas method is called.
     */
    private boolean collectCanvas;
    /**
     *
     */
    private String rmiRegHostName = "localhost";
    private int rmiRegPortNumb = 22350;

    private TimeStamp ts;

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
            csi = (ConcentrationSiteInterface) registry.lookup(csEntryBase);
        } catch (RemoteException e) {
            System.out.println("Exception thrown while locating concentration site: " + e.getMessage() + "!");
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("ConcentrationSite is not registered: " + e.getMessage() + "!");
            System.exit(1);
        }

        try {
            Registry registry = LocateRegistry.getRegistry(rmiRegHostName, rmiRegPortNumb);
            api[0] = (AssaultPartyInterface) registry.lookup(ass0EntryBase);
        } catch (RemoteException e) {
            System.out.println("Exception thrown while locating assault party 0: " + e.getMessage() + "!");
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("AssaultParty0 is not registered: " + e.getMessage() + "!");
            System.exit(1);
        }

        try {
            Registry registry = LocateRegistry.getRegistry(rmiRegHostName, rmiRegPortNumb);
            api[1] = (AssaultPartyInterface) registry.lookup(ass1EntryBase);
        } catch (RemoteException e) {
            System.out.println("Exception thrown while locating assault party 1: " + e.getMessage() + "!");
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("AssaultParty1 is not registered: " + e.getMessage() + "!");
            System.exit(1);
        }
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
     * @param ts
     * @return ID of the operation to execute, 0 - takeARest, 1 -
     * prepareAssaultParty and 2 - sumUpResults
     * @throws java.rmi.RemoteException
     */
    @Override
    public synchronized TimeStamp appraiseSit(int status, int nAssaultThievesCS, TimeStamp ts) throws RemoteException {
        System.out.println("appraiseSit");
        
        
        this.ts.update(ts);
        reportStatusMT(status);

        nextParty = nextEmptyParty();

        this.ts.update(nextEmptyRoom(ts));
        nextRoom = this.ts.getInteger();

        if (nextParty == -1) {
            this.ts.setInteger(0);                                                // takeARest()
        }
        if (nAssaultThievesCS >= MAX_ASSAULT_PARTY_THIEVES) {
            if (nextRoom != -1) {
                this.ts.setInteger(1);// prepareAssaultParty()
            } else if (!checkEmptyRoom() && nAssaultThievesCS == THIEVES_NUMBER) {
                this.ts.setInteger(2);                                            // sumUpResults()
            } else {
                this.ts.setInteger(0);                                            // takeARest()
            }
        } else {
            this.ts.setInteger(0);                                                // takeARest()        
        }
        return this.ts.clone();
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
     * @param ts
     * @return
     */
    @Override
    public synchronized TimeStamp prepareExcursion(int thiefID, int status, int maxDisp, int partyID, int hasCanvas, TimeStamp ts) {
        this.ts.update(ts);
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

        return this.ts.clone();
    }

    /**
     * Master Thief sends a ready Assault Party. The Master Thief sets it's
     * status to DECIDING_WHAT_TO_DO.
     *
     * @param status Master thief status
     * @param ts
     * @return
     * @throws java.rmi.RemoteException
     */
    @Override
    public synchronized TimeStamp sendAssaultParty(int status, TimeStamp ts) throws RemoteException {
        this.ts.update(ts);
        //reportStatusMT(status);

        while (partyReady != MAX_ASSAULT_PARTY_THIEVES) {
            try {
                wait();

            } catch (InterruptedException e) {

            }
        }

        this.ts.update(nextEmptyRoom(ts));
        roomOcupied[this.ts.getInteger()] = true;
        sentAssaultParty = true;
        partyReady = 0;

        notifyAll();

        return this.ts.clone();
    }

    /**
     * Master Thief blocks until an Assault Thief executes handACanvas and
     * AmINeeded. The Master Thief sets its status to WAITING_FOR_GROUP_ARRIVAL.
     *
     * @param status Master thief status
     * @param ts
     * @return
     * @throws java.rmi.RemoteException
     */
    @Override
    public synchronized TimeStamp takeARest(int status, TimeStamp ts) throws RemoteException {
        this.ts.update(ts);
        reportStatusMT(status);

        rest = true;

        notifyAll();

        while (!collectCanvas) {
            try {
                if (rest) {
                    wait();
                } else {
                    break;
                }
            } catch (InterruptedException e) {

            }
        }

        while (!ready) {
            try {
                if (rest) {
                    wait();
                } else {
                    break;
                }
            } catch (InterruptedException e) {

            }
        }

        ready = false;
        collectCanvas = false;

        notifyAll();
        return this.ts.clone();
    }

    /**
     * Master Thief blocks until an Assault Thief executes handACanvas and
     * AmINeeded. The Master Thief sets its status to WAITING_FOR_GROUP_ARRIVAL.
     *
     */
    @Override
    public synchronized TimeStamp gotEndOp(TimeStamp ts) {
        this.ts.update(ts);

        rest = false;
        notifyAll();

        return this.ts.clone();
    }

    /**
     * Get next Party available for assignment.
     *
     * @return nextParty - Next Party available for assignment
     */
    @Override
    public TimeStamp getNextParty(TimeStamp ts) {
        this.ts.update(ts);

        this.ts.setInteger(nextParty);

        return this.ts.clone();
    }

    /**
     * Discover next empty Assault Party.
     *
     * @return the ID of the Assault Party or -1 if there is no empty Assault.
     * Party
     */
    public synchronized int nextEmptyParty() {
        callInterfaces();
        
        System.out.println("nextEmptyParty");
        try {
            this.ts.update(api[0].isEmptyAP(this.ts.clone()));
        } catch (RemoteException ex) {
            Logger.getLogger(ControlCollectionSite.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (this.ts.isBool() == true) {
            return 0;
        }

        try {
            this.ts.update(api[1].isEmptyAP(this.ts.clone()));
        } catch (RemoteException ex) {
            Logger.getLogger(ControlCollectionSite.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (this.ts.isBool() == true) {
            return 1;
        }

        return -1;
    }

    /**
     * Checks whether there are still rooms with paintings or not
     *
     * @return Returns true if there are still rooms with paintings, false
     * otherwise
     */
    public synchronized boolean checkEmptyRoom() {
        System.out.println(Arrays.toString(emptyRooms));
        for (int i = 0; i < ROOMS_NUMBER; i++) {
            if (!emptyRooms[i]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks for an unoccupied and empty Room.
     *
     * @param ts
     * @return Returns the first available Room ID to raid.
     */
    @Override
    public synchronized TimeStamp nextEmptyRoom(TimeStamp ts) {
        this.ts.update(ts);

        for (int i = 0; i < ROOMS_NUMBER; i++) {
            if (!emptyRooms[i] && !roomOcupied[i]) {
                this.ts.setInteger(i);
                return this.ts.clone();
            }
        }
        this.ts.setInteger(-1);

        return this.ts.clone();
    }

    /**
     * Operation to wake Master Thief that the Assault Thief current Thread is
     * ready.
     *
     * @param ts
     */
    @Override
    public synchronized TimeStamp isReady(TimeStamp ts) {
        this.ts.update(ts);

        ready = true;
        notifyAll();

        return this.ts.clone();
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
     * @param ts
     * @return True if operation is sucessful or false if otherwise
     */
    @Override
    public synchronized TimeStamp handCanvas(int thiefID, int status, int maxDisp, int partyID, int hasCanvas, TimeStamp ts) {
        callInterfaces();
        this.ts.update(ts);

        reportStatusAT(thiefID, status, maxDisp, partyID, hasCanvas);

        while (!rest) {
            try {
                wait();
            } catch (InterruptedException e) {

            }
        }

        rest = false;

        try {
            this.ts.update(api[partyID].getRoomID(this.ts.clone()));
        } catch (RemoteException ex) {
            Logger.getLogger(ControlCollectionSite.class.getName()).log(Level.SEVERE, null, ex);
        }

        int roomID = this.ts.getInteger();

        if (hasCanvas == 0) {
            emptyRooms[roomID] = true;
        } else if (hasCanvas == 1) {
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
            try {
                this.ts.update(api[partyID].getPartyThieves(this.ts.clone()));
            } catch (RemoteException ex) {
                Logger.getLogger(ControlCollectionSite.class.getName()).log(Level.SEVERE, null, ex);
            }

            int[] pthieves = this.ts.getInteger_array();

            if (pthieves[i] == thiefID) {
                try {
                    this.ts.update(api[partyID].setPartyThieves(this.ts.clone(), i, -1));
                } catch (RemoteException ex) {
                    Logger.getLogger(ControlCollectionSite.class.getName()).log(Level.SEVERE, null, ex);
                }         
            }
        }

        notifyAll();

        return this.ts.clone();
    }

    /**
     * Master Thief collects a canvas. The Master Thief sets its status to
     * DECIDING_WHAT_TO_DO.
     *
     * @param status Master thief status
     * @param ts
     * @return
     * @throws java.rmi.RemoteException
     */
    @Override
    public synchronized TimeStamp collectCanvas(int status, TimeStamp ts) throws RemoteException {
        this.ts.update(ts);
        reportStatusMT(status);
        collectCanvas = false;

        notifyAll();
        return this.ts.clone();
    }

    /**
     * Master Thief presents the final heist report. The Master Thief sets its
     * status to PRESENTING_THE_REPORT.
     *
     * @param status Master thief status
     * @param ts
     * @return
     */
    @Override
    public synchronized TimeStamp sumUpResults(int status, TimeStamp ts) {
        reportStatusMT(status);
        System.out.println("Got " + nPaintings + " paintings!");
        this.ts.update(ts);
        return this.ts.clone();
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
    @Override
    public TimeStamp getNextRoom(TimeStamp ts) {
        this.ts.update(ts);

        this.ts.setInteger(nextRoom);
        return this.ts.clone();
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
        /* Message inMessage, outMessage;
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
         */
        return false;
    }

    /**
     *
     * @return true if successfully logged status or false if otherwise
     */
    private boolean reportStatusMT(int status) {
        /*Message inMessage, outMessage;
        ClientCom con = new ClientCom(map[0].get("Logger"), Integer.parseInt(map[1].get("Logger")));

        if (!con.open()) {
            return false;
        }
        outMessage = new Message(REP_STATUS_MT, status);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();
        con.close();

        return inMessage.getType() == ACK;*/
        return true;
    }

    @Override
    public void signalShutdown() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
