package interfaces;

/**
 *
 * @author mota
 */
public interface IControlCollectionSite {

    /**
     *
     * Master Thief appraises the situation and returns the next operation to
     * execute. The Master Thief sets its status to DECIDING_WHAT_TO_DO before
     * returning the operation.
     *
     * @param nAssaultThievesCS
     * @return ID of the operation to execute.
     */
    int appraiseSit(int nAssaultThievesCS);

    /**
     * Master Thief collects a canvas. The Master Thief sets its status to
     * DECIDING_WHAT_TO_DO.
     *
     */
    void collectCanvas();

    /**
     * Get next Party available for assignment.
     *
     * @return nextParty
     */
    int getNextParty();

    /**
     * Get the ID of the next Room to be assigned.
     *
     * @return
     */
    int getNextRoom();

    /**
     * Get the number of paitings collected to the moment.
     *
     * @return Returns the number of paitings collected to the moment.
     */
    int getnPaintings();

    /**
     * Assault thieves hands a canvas to the Master Thief or shows up empty
     * handed.
     *
     * @param roomID ID of the room from which the Assault Thief current thread
     * has returned from.
     */
    void handCanvas();

    /**
     * Checks if the Assault Thief current thread is in the Assault Party.
     *
     * @return True, if the Assault Thief current thread is in the Assault Party
     * or false if otherwise.
     */
    boolean inParty();

    /**
     * Operation to wake Master Thief that the Assault Thief current Thread is
     * ready.
     *
     */
    void isReady();

    /**
     * Discover next empty Assault Party.
     *
     * @return the ID of the Assault Party or -1 if there is no empty Assault.
     * Party
     */
    int nextEmptyParty();

    /**
     * Checks for an unoccupied and empty Room.
     *
     * @return Returns the first available Room ID to raid.
     */
    int nextEmptyRoom();

    /**
     *
     * Assault Thief current thread sets its partyID to the assigned Assault
     * Party. The thread wakes the Master Thief if it's the last element of the
     * Assault Party to execute this operation and blocks until the Master Thief
     * finalizes executing sendAssaultParty. The Assault Thief sets its status
     * to WAITING_FOR_SENT_ASSAULT_PARTY.
     */
    void prepareExcursion();

    /**
     * Master Thief sends a ready Assault Party. The Master Thief sets it's
     * status to DECIDING_WHAT_TO_DO.
     */
    void sendAssaultParty();

    /**
     * Set the next Assault Party ID to be assigned.
     *
     * @param nextParty
     */
    void setNextParty(int nextParty);

    /**
     * Set the next Room ID to be assigned.
     *
     * @param nextRoom
     */
    void setNextRoom(int nextRoom);

    /**
     * Master Thief presents the final heist report. The Master Thief sets its
     * status to PRESENTING_THE_REPORT.
     */
    void sumUpResults();

    /**
     * Master Thief blocks until an Assault Thief executes handACanvas and
     * AmINeeded. The Master Thief sets its status to WAITING_FOR_GROUP_ARRIVAL.
     */
    void takeARest();
    
}
