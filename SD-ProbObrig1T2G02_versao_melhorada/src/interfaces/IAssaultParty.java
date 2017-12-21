package interfaces;

import threads.AssaultThief;

/**
 *
 * @author mota
 */
public interface IAssaultParty {

    /**
     * Add an Assault Thief to the Assault Party
     *
     * @param thief
     * @return True, if the operation was sucessful or false if otherwise
     */
    boolean addThief(AssaultThief thief);

    /**
     *
     * Simulates the movement crawlIn of the Assault Thief current thread.
     *
     *
     */
    void crawlIn();

    /**
     *
     * Simulates the movement crawlOut of the Assault Thief current thread.
     *
     */
    void crawlOut();

    /**
     * +
     * Get distance from the Outside to the Room assigned to this Assault Party
     *
     * @return Returns distance from the Outside to the Room assigned to this
     * Assault Party
     */
    int getDistOutsideRoom();

    /**
     * Get index of current Assault Thief thread in the partyThieves array
     *
     * @return Returns the index of the current Assault Thief thread in the
     * partyThieves array
     */
    int getIndexParty();

    /**
     * Get Assault Thieves ID of the current Assault Party.
     *
     * @return Returns array partyThieves ID of the current Assault Party
     */
    int[] getPartyThieves();

    /**
     * Get Assault Thieves maximum displacement of the current Assault Party.
     *
     * @return Returns array partyThieves ID of the current Assault Party
     */
    int[] getPartyThievesMaxDisp();

    /**
     * Get Assault Thieves positions of the current Assault Party.
     *
     * @return Returns array partyThieves ID of the current Assault Party
     */
    int[] getPartyThievesPos();

    /**
     * Get the roomID assigned to the current Assault Party.
     *
     * @return Returns roomIDy
     */
    int getRoomID();

    /**
     *
     * Simulates the operation reverseDirection of the Assault Party. The
     * Assault Thief current thread blocks until the last element of the Assault
     * Party executes this action.
     *
     */
    void reverseDirection();

    /**
     * Set the turn to crawl of the element of index 0 to true.
     *
     */
    void setFirst();

    /**
     * Set the ID of an Assault Thief in the current Assault Party.
     *
     * @param i
     * @param value
     */
    void setPartyThieves(int i, int value);

    /**
     *
     * Set a roomID to this Assault Party.
     *
     * @param roomID
     */
    void setRoom(int roomID);
    
}
