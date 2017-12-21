package clientSide;

import static auxiliary.constants.Constants.*;
import static auxiliary.constants.States.*;
import auxiliary.time.TimeStamp;
import genclass.GenericIO;
import interfaces.AssaultPartyInterface;
import interfaces.ConcentrationSiteInterface;
import interfaces.ControlCollectionSiteInterface;
import interfaces.LoggerInterface;
import interfaces.MuseumInterface;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author António Mota
 * @author Marcos Pires
 */
public class AssaultThief extends Thread {

    /**
     * ID of the thief
     *
     * @serialField thiefID
     */
    private final int thiefID;
    /**
     * Current status of this thief
     *
     * @serialField status
     */
    private int status;
    /**
     * Maximum displacement that this thief can move: each time this thief
     * moves, he can not exceed this value
     *
     * @serialField maxDisp
     */
    private final int maxDisp;
    /**
     * ID of Assault Party to which this thief is currently assigned to
     *
     * @serialField partyID
     */
    private int partyID;
    /**
     * Boolean value which meaning refers to weather or not this thief currently
     * possesses a painting or not
     *
     * @serialField hasCanvas
     */
    private int hasCanvas;

    private final LoggerInterface li;
    private final MuseumInterface mi;
    private final ConcentrationSiteInterface csi;
    private final ControlCollectionSiteInterface ccsi;
    private final AssaultPartyInterface[] api;
    private final TimeStamp ts_my;
    private TimeStamp ts_rcv;

    /**
     * This data type represents an assault thief, member of a heist crew
     *
     * @param thiefID ID of this thief
     *
     */
    public AssaultThief(int thiefID, LoggerInterface li, MuseumInterface mi, ConcentrationSiteInterface csi, ControlCollectionSiteInterface ccsi, AssaultPartyInterface[] api) {
        super("Thief_" + thiefID);

        this.li = li;
        this.mi = mi;
        this.csi = csi;
        this.ccsi = ccsi;
        this.api = new AssaultPartyInterface[MAX_ASSAULT_PARTIES];

        this.thiefID = thiefID;
        status = OUTSIDE;
        maxDisp = (int) (Math.random() * (THIEVES_MAX_DISPLACEMENT + 1 - THIEVES_MIN_DISPLACEMENT)) + THIEVES_MIN_DISPLACEMENT;
        partyID = -1;
        hasCanvas = 0;
        ts_my = new TimeStamp();
        ts_rcv = new TimeStamp();
    }

    /**
     * Life cycle of the Assault Thief
     */
    @Override
    @SuppressWarnings("empty-statement")
    public void run() {

        try {
            System.out.println("AmINeeded!");
            while (csi.amINeeded(thiefID, status, maxDisp, partyID, hasCanvas, ts_my.clone()).getInteger() != -1) {
                try {
                    System.out.println("prepareExcursion!");
                    this.ts_my.increment(this.thiefID);
                    ts_rcv = ccsi.prepareExcursion(thiefID, status, maxDisp, partyID, hasCanvas, ts_my.clone());
                    this.ts_my.update(ts_rcv);
                } catch (RemoteException ex) {
                    Logger.getLogger(AssaultThief.class.getName()).log(Level.SEVERE, null, ex);
                }
                while (api[partyID].crawlIn(ts_my.clone(), thiefID, status, maxDisp, partyID, hasCanvas).isBool());
                
                ts_my.update(api[partyID].getRoomID(ts_my.clone()));
                
                mi.rollACanvas(ts_my.clone(), thiefID, status, maxDisp, partyID, hasCanvas, ts_my.getInteger());
                api[partyID].reverseDirection(ts_my.clone(), thiefID, status, maxDisp, partyID, hasCanvas);
                while (api[partyID].crawlOut(ts_my.clone(), thiefID, status, maxDisp, partyID, hasCanvas).isBool());

                try {
                    System.out.println("HandCanvas!");
                    this.ts_my.increment(this.thiefID);
                    ts_rcv = ccsi.handCanvas(thiefID, status, maxDisp, partyID, hasCanvas, ts_my.clone());
                    this.ts_my.update(ts_rcv);
                } catch (RemoteException ex) {
                    Logger.getLogger(AssaultThief.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        } catch (RemoteException ex) {
            Logger.getLogger(AssaultThief.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
