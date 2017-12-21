package threads;

import interfaces.*;
import static auxiliary.constants.Constants.*;

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
    private int position;
    private int hasCanvas;

    private final IConcentrationSite cs;
    private final IControlCollectionSite ccs;
    private final IAssaultParty[] assparties;
    private final IMuseum museum;

    /**
     *
     * @param thiefID
     * @param position
     * @param cs
     * @param ccs
     * @param assparties
     * @param museum
     */
    public AssaultThief(int thiefID, int position, IConcentrationSite cs, IControlCollectionSite ccs, IAssaultParty[] assparties, IMuseum museum) {
        this.thiefID = thiefID;
        status = OUTSIDE;
        maxDisp = (int) (Math.random() * (THIEVES_MAX_DISPLACEMENT + 1 - THIEVES_MIN_DISPLACEMENT)) + THIEVES_MIN_DISPLACEMENT;
        partyID = -1;
        this.position = position;
        hasCanvas = 0;

        this.cs = cs;
        this.ccs = ccs;
        this.assparties = assparties;
        this.museum = museum;
    }

    /**
     *
     */
    @Override
    public void run() {
        while (cs.amINeeded()) {
            ccs.prepareExcursion();
            while (position != assparties[partyID].getDistOutsideRoom()) {
                assparties[partyID].crawlIn();
            }
            hasCanvas = museum.rollACanvas(assparties[getPartyID()].getRoomID());
            assparties[getPartyID()].reverseDirection();
            while (position != 0) {
                assparties[getPartyID()].crawlOut();
            }
            ccs.handCanvas();
        }
    }

    /**
     *
     * @return Returns ID of current Assault Thief thread
     */
    public int getThiefID() {
        return thiefID;
    }

    /**
     * Sets status of this thief
     *
     * @param status new status of this thief
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     *
     * @return Returns status of this thief
     */
    public int getStatus() {
        return status;
    }

    /**
     *
     * @return Returns maximum displacement of the current Assault Thief thread.
     */
    public int getMaxDisp() {
        return maxDisp;
    }

    /**
     * Assign to this thief an assault party
     *
     * @param partyID ID of assigned assault party
     */
    public void setPartyID(int partyID) {
        this.partyID = partyID;
    }

    /**
     *
     * @return Returns this thief's assault party
     */
    public int getPartyID() {
        return partyID;
    }

    /**
     * Sets new position of this thief
     *
     * @param position New position of the current Assault Thief thread
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     *
     * @return Returns position of the current Assault Thief thread
     */
    public int getPosition() {
        return position;
    }

    /**
     *
     * @param hasCanvas
     */
    public void setHasCanvas(int hasCanvas) {
        this.hasCanvas = hasCanvas;
    }

    /**
     *
     * @return Returns Boolean value that asserts if thief has a canvas or not
     */
    public int getHasCanvas() {
        return hasCanvas;
    }
}
