package auxiliary.time;

import static auxiliary.constants.Constants.*;
import java.io.Serializable;

/**
 * This data type represents a room of the Heist to the Museum Problem.
 *
 * @author António Mota
 * @author Marcos Pires
 */
public class Room implements Serializable {

    public int id;
    public int nPaintings;
    public int distOutside;

    /**
     *
     * @param id ID of the Room
     */
    public Room(int id) {
        this.id = id;
        nPaintings = (int) (Math.random() * (MAX_PAINTINGS - MIN_PAINTINGS) + 1) + MIN_PAINTINGS;
        distOutside = (int) (Math.random() * (MAX_DIST_OUTSIDE - MIN_DIST_OUTSIDE) + 1) + MIN_DIST_OUTSIDE;
    }

    /**
     *
     * @return id - ID of the room
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @return nPaitings - Number of Paintings of the room
     */
    public int getNPaintings() {
        return nPaintings;
    }

    /**
     *
     * @return distOutside - Distance to the outside of the room
     */
    public int getDistOutside() {
        return distOutside;
    }

    /**
     *
     * @param nPaintings Number of Paintings of the room
     */
    public void setnPaintings(int nPaintings) {
        this.nPaintings = nPaintings;
    }

    /**
     *
     * @param distOutside Distance to the outside of the room
     */
    public void setDistOutside(int distOutside) {
        this.distOutside = distOutside;
    }
}
