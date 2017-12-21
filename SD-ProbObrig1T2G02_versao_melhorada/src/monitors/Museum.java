package monitors;

import interfaces.IMuseum;
import interfaces.ILogger;
import interfaces.*;
import static auxiliary.constants.Constants.*;
import threads.*;

/**
 * import interfaces.ILogger;
 *
 * @author António Mota
 * @author Marcos Pires
 */
public class Museum implements IMuseum {

    private Room[] rooms;                   // Array of rooms of the museum

    // Monitors
    private ILogger log;

    /**
     *
     * @param log
     */
    public Museum(ILogger log) {
        rooms = new Room[ROOMS_NUMBER];

        this.log = log;

        log.setMuseum(rooms);
        for (int i = 0; i < ROOMS_NUMBER; i++) {
            rooms[i] = new Room(i);
        }
    }

    /**
     *
     * @param nRoom ID of the room where the thieves are
     * @return Returns 1 if thief was successful in retrieving a canvas, 0 if
     * not
     */
    @Override
    public synchronized int rollACanvas(int nRoom) {
        AssaultThief thief = (AssaultThief) Thread.currentThread();

        thief.setStatus(AT_A_ROOM);

        int nPaintings = rooms[nRoom].getNPaintings();
        
        if (nPaintings > 0) {
            rooms[nRoom].setnPaintings(nPaintings - 1);
            
            log.setMuseum(rooms);
            log.setAssaultThief();
            log.reportStatus();
            
            return 1;
        } else {
            log.setMuseum(rooms);
            log.setAssaultThief();
            log.reportStatus();
            
            return 0;
        }
    }

    /**
     *
     * @param roomID ID of a room
     * @return Returns the room with the given ID
     */
    @Override
    public Room getRoom(int roomID) {
        return rooms[roomID];
    }

    /**
     *
     */
    @Override
    public void totalPaintings() {
        int count = 0;

        for (int i = 0; i < ROOMS_NUMBER; i++) {
            count += rooms[i].getNPaintings();
        }
    }
}
