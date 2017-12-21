package interfaces;

import monitors.Room;

/**
 *
 * @author mota
 */
public interface ILogger {

    /**
     * Log the final status of the Heist.
     *
     */
    void reportFinalStatus();

    /**
     * Log the initial status of the Heist.
     *
     */
    void reportInitialStatus();

    /**
     * Log the status of everything in the General Repository of Information.
     *
     */
    void reportStatus();

    /**
     * Set an Assault Party in the General Repository of Information.
     *
     * @param id
     * @param elements
     * @param positions
     * @param roomID
     */
    void setAssaultParty(int id, int[] elements, int[] positions, int roomID);

    /**
     * Set an Assault Thief in the General Repository of Information.
     *
     */
    void setAssaultThief();

    /**
     * Set the Master Thief in the General Repository of Information.
     *
     */
    void setMasterThief();

    /**
     * Set the Museum in the General Repository of Information.
     *
     * @param rooms
     */
    void setMuseum(Room[] rooms);

    /**
     * Set the number of Paintings in the General Repository of Information.
     *
     * @param nPaintings
     */
    void setnPaintings(int nPaintings);

    /**
     * Start a file where all the operations will be written along with the
     * changes to the main attributes of the Heist.
     *
     */
    void startLog();

}
