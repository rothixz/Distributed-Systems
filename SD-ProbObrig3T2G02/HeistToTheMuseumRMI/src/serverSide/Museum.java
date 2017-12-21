package serverSide;

import static auxiliary.constants.Constants.*;
import auxiliary.time.TimeStamp;
import auxiliary.time.Room;
import interfaces.MuseumInterface;
import java.rmi.RemoteException;

/**
 * This data type represents the Museum of the Heist to the Museum Problem.
 *
 * @author António Mota
 * @author Marcos Pires
 */
public class Museum implements MuseumInterface {

    /**
     * List of Rooms of the museum
     */
    private Room[] rooms;
    /**
     * Name of Machine with the configurations for the other servers
     */
    private String hostname = CONFIG_PC_NAME;
    private TimeStamp ts;

    /**
     * HashMap array containing configurations for the other servers
     */

    public Museum() {
        this.rooms = new Room[ROOMS_NUMBER];

        for (int i = 0; i < ROOMS_NUMBER; i++) {
            this.rooms[i] = new Room(i);
        }

        reportStatus();
        ts = new TimeStamp();
    }

    /**
     *
     * @param thiefID ID of the assault thief
     * @param nRoom ID of the room the thief is raiding
     * @param status Status of the thief
     * @param maxDisp Maximum Displacement of the thief
     * @param partyID ID of the party assigned to the thief
     * @param hasCanvas Represents what the thief is carrying, 0 - empty handed,
     * 1 - got 1 canvas or -1 default value
     * @param ts
     * @return Returns 1 if thief was successful in retrieving a canvas or 0 if
     * otherwise
     */
    @Override
    public synchronized TimeStamp rollACanvas(TimeStamp ts, int thiefID, int status, int maxDisp, int partyID, int hasCanvas, int nRoom) {
        this.ts.update(ts);
        reportStatusAT(thiefID, status, maxDisp, partyID, hasCanvas);

        for (int i = 0; i < ROOMS_NUMBER; i++) {
            System.out.println("room " + i + " --- " + rooms[i].nPaintings);
        }
        int nPaintings = rooms[nRoom].getNPaintings();
        if (nPaintings > 0) {
            rooms[nRoom].setnPaintings(nPaintings - 1);

            this.ts.setInteger(1);
        } else {
            this.ts.setInteger(1);
        }
        return this.ts.clone();
    }

    /**
     *
     * @param roomID ID of the room
     * @return Returns the room with the given ID
     */
    @Override
    public TimeStamp getRoom(TimeStamp ts, int roomID) {
        this.ts.update(ts);

        this.ts.setRoom(rooms[roomID]);

        return this.ts.clone();
    }

    /**
     *
     * @return true if successfully logged status or false if otherwise
     */
    private boolean reportStatus() {

        return true;
    }

    /**
     *
     * @return true if successfully logged status or false if otherwise
     */
    private boolean reportStatusAT(int thiefID, int status, int maxDisp, int partyID, int hasCanvas) {

        return true;
    }

    @Override
    public void signalShutdown() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
