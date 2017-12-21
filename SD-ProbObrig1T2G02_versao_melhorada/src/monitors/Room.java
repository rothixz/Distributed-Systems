package monitors;

import static auxiliary.constants.Constants.*;

/**
 *
 * @author António Mota
 * @author Marcos Pires
 */
public class Room {

    int id;                           // ID of the room
    int nPaintings;                   // Number of paintings of the room
    int distOutside;                  // Distance from the room to the outside

    /**
     *
     * @param id
     */
    public Room(int id) {
        this.id = id;
        nPaintings = (int) (Math.random() * (MAX_PAINTINGS - MIN_PAINTINGS) + 1) + MIN_PAINTINGS;
        distOutside = (int) (Math.random() * (MAX_DIST_OUTSIDE - MIN_DIST_OUTSIDE) + 1) + MIN_DIST_OUTSIDE;
    }

    /**
     * Get ID of the Room.
     *
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Set the number of paintings of the Room.
     * 
     * @param nPaintings
     */
    public void setnPaintings(int nPaintings) {
        this.nPaintings = nPaintings;
    }

    /**
     * Get the number of paintings of the Room.
     *
     * @return nPaintings
     */
    public int getNPaintings() {
        return nPaintings;
    }

    /**
     * Get the distance from the Room to the outside.
     * 
     * @return distOutside
     */
    public int getDistOutside() {
        return distOutside;
    }
}
