package clientSide;

import static auxiliary.constants.Constants.MAX_ASSAULT_PARTIES;
import static auxiliary.constants.States.*;
import auxiliary.time.TimeStamp;
import interfaces.*;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author António Mota
 * @author Marcos Pires
 */
public class MasterThief extends Thread {

    /**
     * Current status of the Master Thief
     *
     * @serialField status
     */
    private int status;

    private final LoggerInterface li;
    private final MuseumInterface mi;
    private final ConcentrationSiteInterface csi;
    private final ControlCollectionSiteInterface ccsi;
    private final AssaultPartyInterface[] api;
    private final TimeStamp ts_my;
    private TimeStamp ts_rcv;
    private int mt_id;
    /**
     * Data type representing the master thief of a Heist to a museum
     *
     * @param li
     * @param mi
     * @param csi
     * @param api
     * @param ccsi
     */
    public MasterThief(LoggerInterface li, MuseumInterface mi, ConcentrationSiteInterface csi, ControlCollectionSiteInterface ccsi, AssaultPartyInterface[] api) {
        this.li = li;
        this.mi = mi;
        this.csi = csi;
        this.ccsi = ccsi;
        this.api = new AssaultPartyInterface[MAX_ASSAULT_PARTIES];
        for (int i = 0; i < MAX_ASSAULT_PARTIES; i++) {
            this.api[i] = api[i];
        }

        this.setName("Master Thief");
        ts_my = new TimeStamp();
        ts_rcv = new TimeStamp();
        status = PLANNING_THE_HEIST;
        mt_id = 0;
    }

    /**
     * Life cycle of the Master thief
     */
    @Override
    public void run() {
        boolean heistend = false;
        try {
            System.out.println("Start of Operations!");
            this.ts_my.increment(mt_id);
            csi.startOfOperations(ts_my.clone(), status);
            this.ts_my.update(ts_rcv);
        } catch (RemoteException ex) {
            Logger.getLogger(MasterThief.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (!heistend) {
            try {
                System.out.println("AppraiseSit!");
                switch (ccsi.appraiseSit(status, csi.getnAssaultThievesCS(ts_my.clone()).getInteger(), ts_my.clone()).getInteger()) {
                    case 1:
                        try {
                            System.out.println("PrepareAssaultParty!");
                            this.ts_my.increment(mt_id);
                            ts_rcv = csi.prepareAssaultParty(status, ts_my.clone());
                            this.ts_my.update(ts_rcv);
                        } catch (RemoteException ex) {
                            Logger.getLogger(MasterThief.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        try {
                            System.out.println("sendAssaultParty!");
                            this.ts_my.increment(mt_id);
                            ts_rcv = ccsi.sendAssaultParty(status, ts_my.clone());
                            this.ts_my.update(ts_rcv);
                        } catch (RemoteException ex) {
                            Logger.getLogger(MasterThief.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;
                    case 0:
                        try {
                            System.out.println("TakeARest!");
                            this.ts_my.increment(mt_id);
                            ts_rcv = ccsi.takeARest(status, ts_my.clone());
                            this.ts_my.update(ts_rcv);
                        } catch (RemoteException ex) {
                            Logger.getLogger(MasterThief.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        try {
                            System.out.println("collectCanvas!");
                            this.ts_my.increment(mt_id);
                            ts_rcv = ccsi.collectCanvas(status, ts_my.clone());
                            this.ts_my.update(ts_rcv);
                        } catch (RemoteException ex) {
                            Logger.getLogger(MasterThief.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        break;
                    case 2:
                        try {
                            System.out.println("SumUpResults!");
                            this.ts_my.increment(mt_id);
                            ts_rcv = ccsi.sumUpResults(status, ts_my.clone());
                            this.ts_my.update(ts_rcv);
                        } catch (RemoteException ex) {
                            Logger.getLogger(MasterThief.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        heistend = true;
                        break;
                }
            } catch (RemoteException ex) {
                Logger.getLogger(MasterThief.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
