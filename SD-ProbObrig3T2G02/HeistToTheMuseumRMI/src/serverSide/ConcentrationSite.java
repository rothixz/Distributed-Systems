package serverSide;

import static auxiliary.constants.Constants.*;
import auxiliary.memFIFO.MemFIFO;
import auxiliary.time.TimeStamp;
import interfaces.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;
import auxiliary.time.Room;

/**
 * This data type represents the Concentration Site of the Heist to the Museum
 * Problem.
 *
 * @author António Mota
 * @author Marcos Pires
 */
public class ConcentrationSite implements ConcentrationSiteInterface {

    /**
     * Waiting queue for ready assault thieves
     */
    private MemFIFO waitQueue;
    /**
     * Waiting queue for ready assault thieves displacement
     */
    private MemFIFO waitQueueDisp;
    /**
     * Number of assault thieves in the concentration site
     */
    private int nAssaultThievesCS;
    /**
     * Support variable which meaning is the ID of an assault party currently
     * being handled
     */
    private int partyID;

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

    public ConcentrationSite() {
        waitQueue = new MemFIFO(THIEVES_NUMBER);
        waitQueueDisp = new MemFIFO(THIEVES_NUMBER);
        nAssaultThievesCS = 0;
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
     * Master Thief blocks until the number of Assault Thieves in the
     * Concentration Site is equal to the number of total Assault Thieves of the
     * heist. The status of the Master Thief is changed to DECIDING_WHAT_TO_DO
     * in the end of the operation.
     *
     * @param status Master thief status
     */
    @Override
    public synchronized TimeStamp startOfOperations(TimeStamp ts, int status) {
        System.out.println("lol\n\n");
        this.ts.update(ts);

        reportStatusMT(status);

        while (nAssaultThievesCS != THIEVES_NUMBER) {
            try {
                wait();
            } catch (InterruptedException e) {

            }
        }

        return this.ts.clone();
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
     * @param ts
     * @return partyID - ID of the party assigned to the thief
     */
    @Override
    public synchronized TimeStamp amINeeded(int thiefID, int status, int maxDisp, int partyID, int hasCanvas, TimeStamp ts) throws RemoteException {
	    callInterfaces();
        this.ts.update(ts);
        //reportStatusAT(thiefID, status, maxDisp, partyID, hasCanvas);

        nAssaultThievesCS++;
        waitQueue.write(thiefID);
        waitQueueDisp.write(maxDisp);

        this.ts.update(ccsi.isReady(this.ts.clone()));

        notifyAll();

        while (!inParty(thiefID)) {
            try {
                this.ts.update(ccsi.nextEmptyRoom(this.ts.clone()));

                if (this.ts.getInteger() == -1 && nAssaultThievesCS == THIEVES_NUMBER) {
                    this.ts.update(ccsi.gotEndOp(this.ts.clone()));

                    notifyAll();

                    return this.ts.clone();
                }
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ConcentrationSite.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        this.ts.setInteger(partyID);

        return this.ts.clone();
    }

    /**
     * Checks if the Assault Thief current thread is in the Assault Party.
     *
     * @param thiefID ID of the assault thief
     * @return True, if the Assault Thief current thread is in the Assault Party
     * or false if otherwise.
     */
    public synchronized boolean inParty(int thiefID) throws RemoteException {
        callInterfaces();
        for (int i = 0; i < MAX_ASSAULT_PARTIES; i++) {
            this.ts.update(api[i].getPartyThieves(this.ts.clone()));

            int[] pthieves = this.ts.getInteger_array();
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
     * @param ts
     * @return True if the operation was sucessful or false if otherwise
     * @throws java.rmi.RemoteException
     */
    @Override
    public synchronized TimeStamp prepareAssaultParty(int status, TimeStamp ts) throws RemoteException {
        callInterfaces();
        this.ts.update(ts);

        reportStatusMT(status);
        
        this.ts.update(ccsi.getNextParty(ts).clone());
        partyID = this.ts.getInteger();
        
        for (int i = 0; i < MAX_ASSAULT_PARTY_THIEVES; i++) {
            this.ts.update(api[partyID].addThief(this.ts.clone(), (int) waitQueue.read(), (int) waitQueueDisp.read()));
            nAssaultThievesCS--;
        }
        
        this.ts.update(api[partyID].setFirst(this.ts.clone()));

        notifyAll();
        
        this.ts.update(ccsi.getNextRoom(this.ts.clone()));
        int roomID = this.ts.getInteger();
        
        this.ts.update(api[partyID].setRoom(this.ts, roomID));

        return this.ts.clone();
    }

    /**
     * Get the number of Assault Thieves in Concentration Site.
     *
     * @param ts
     * @return Number of Assault Thieves in Concentration Site
     */
    @Override
    public TimeStamp getnAssaultThievesCS(TimeStamp ts) {
        this.ts.update(ts);
        
        this.ts.setInteger(nAssaultThievesCS);
        
        return this.ts.clone();
    }

    /**
     *
     * @return true if successfully logged status or false if otherwise
     */
    private boolean reportStatusAT(int thiefID, int status, int maxDisp, int partyID, int hasCanvas) {
        /*Message inMessage, outMessage;
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
         */
        return false;
    }

    /**
     *
     * @return true if successfully logged status or false if otherwise
     */
    private boolean reportStatusMT(int status) {
        /* Message inMessage, outMessage;
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
         */
        return false;
    }

    @Override
    public void signalShutdown() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
