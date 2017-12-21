package threads;

import static auxiliary.constants.Constants.*;
import interfaces.*;

/**
 *
 * @author António Mota
 * @author Marcos Pires
 */
public class MasterThief extends Thread {

    private int status;

    private final IControlCollectionSite ccs;
    private final IConcentrationSite cs;
    private final IMuseum museum;

    /**
     *
     * @param ccs
     * @param cs
     * @param museum
     */
    public MasterThief(IControlCollectionSite ccs, IConcentrationSite cs, IMuseum museum) {
        status = PLANNING_THE_HEIST;

        this.ccs = ccs;
        this.cs = cs;
        this.museum = museum;
    }

    /**
     *
     */
    @Override
    public void run() {
        boolean heistend = false;
        museum.totalPaintings();
        cs.startOfOperations();
        while (!heistend) {
            switch (ccs.appraiseSit(cs.getnAssaultThievesCS())) {
                case 1:
                    cs.prepareAssaultParty();
                    ccs.sendAssaultParty();
                    break;
                case 0:
                    ccs.takeARest();
                    ccs.collectCanvas();
                    break;
                case 2:
                    ccs.sumUpResults();
                    heistend = true;
                    break;
            }
        }

        System.out.println("Master Thief has finished!");
    }

    /**
     * Sets new status to Master thief
     *
     * @param status Next status of master thief
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     *
     * @return Returns Current status of Master thief
     */
    public int getStatus() {
        return status;
    }
}
