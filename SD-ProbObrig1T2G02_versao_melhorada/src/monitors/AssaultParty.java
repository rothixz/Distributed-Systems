package monitors;

import interfaces.ILogger;
import interfaces.IAssaultParty;
import static auxiliary.constants.Constants.*;
import interfaces.*;
import threads.AssaultThief;
import java.util.Arrays;

/**
 *
 * @author António Mota
 * @author Marcos Pires
 */
public class AssaultParty implements IAssaultParty {

    private int id;                         // ID of the assault party
    private int roomID;                     // ID of room assigned
    private int[] partyThieves;             // Elements assigned
    private int[] partyThievesPos;          // Position of the elements
    private int[] partyThievesMaxDisp;      // Maximum displacement of the elements
    private boolean[] myTurn;               // Turns of the elements
    private boolean[] inRoom;               // Elements in room
    private int nThievesRoom;               // Number of assault thieves in room
    private int reverse;                    // Counter to inform all elements are Ready to crawlOut()

    // Monitors
    private final IMuseum museum;
    private final ILogger log;

    /**
     *
     * @param id
     * @param museum
     * @param log
     */
    public AssaultParty(int id, IMuseum museum, ILogger log) {
        this.id = id;
        roomID = -1;
        partyThieves = new int[MAX_ASSAULT_PARTY_THIEVES];
        partyThievesPos = new int[MAX_ASSAULT_PARTY_THIEVES];
        partyThievesMaxDisp = new int[MAX_ASSAULT_PARTY_THIEVES];
        myTurn = new boolean[MAX_ASSAULT_PARTY_THIEVES];
        inRoom = new boolean[MAX_ASSAULT_PARTY_THIEVES];
        nThievesRoom = 0;
        reverse = 0;

        this.museum = museum;
        this.log = log;

        // Empty assault party
        for (int i = 0; i < MAX_ASSAULT_PARTY_THIEVES; i++) {
            partyThieves[i] = -1;
            partyThievesPos[i] = -1;
            partyThievesMaxDisp[i] = -1;
            myTurn[i] = false;
            inRoom[i] = false;
        }
    }

    /**
     *
     * Simulates the movement crawlIn of the Assault Thief current thread.
     *
     *
     */
    @Override
    public synchronized void crawlIn() {
        AssaultThief thief = ((AssaultThief) Thread.currentThread());

        while (!myTurn[getIndexParty()]) {
            try {
                wait();
            } catch (InterruptedException ex) {
                System.out.println(ex.getMessage());
            }
        }

        thief.setStatus(CRAWLING_INWARDS);

        int myIndex = getIndexParty();
        int myPos = thief.getPosition();
        int myAgility = thief.getMaxDisp();
        int[] assaultThievesPos = new int[MAX_ASSAULT_PARTY_THIEVES - 1];

        int count = 0;
        int i = 0;
        for (i = 0; i < MAX_ASSAULT_PARTY_THIEVES; i++) {
            if (i != myIndex) {
                assaultThievesPos[count] = partyThievesPos[i];
                count++;
            }
        }

        Arrays.sort(assaultThievesPos);

        // Predict maximum displacement
        for (i = myAgility; i > 0; i--) {
            boolean tooFarOrOcupada = false;
            // Array que vai ter myPos no inicio e assaultThievesPos de seguida
            int[] posAfterMove = new int[assaultThievesPos.length + 1];
            posAfterMove[0] = myPos + i;
            System.arraycopy(assaultThievesPos, 0, posAfterMove, 1, assaultThievesPos.length);
            Arrays.sort(posAfterMove);

            for (int j = 0; j < posAfterMove.length - 1; j++) {
                if ((posAfterMove[j + 1] - posAfterMove[j] > THIEVES_MAX_DISTANCE) || (posAfterMove[j + 1] - posAfterMove[j] == 0 && (posAfterMove[j + 1] != 0 && posAfterMove[j + 1] != getDistOutsideRoom()))) { //ultima condicao deve ser alterada
                    tooFarOrOcupada = true;
                    break;
                }
            }

            // Set new position
            if ((!tooFarOrOcupada)) {
                if (myPos + i >= getDistOutsideRoom()) {
                    partyThievesPos[myIndex] = getDistOutsideRoom();
                    thief.setPosition(getDistOutsideRoom());
                    nThievesRoom++;
                    inRoom[myIndex] = true;

                    log.setAssaultThief();
                    log.setAssaultParty(id, partyThieves, partyThievesPos, roomID);
                    log.reportStatus();

                } else {
                    partyThievesPos[myIndex] = myPos + i;
                    thief.setPosition(myPos + i);

                    log.setAssaultThief();
                    log.setAssaultParty(id, partyThieves, partyThievesPos, roomID);
                    log.reportStatus();
                }

                break;
            }
        }

        // Check if it is possible to move even further
        boolean canMoveAgain = false;
        if (!(myPos == partyThievesPos[myIndex] || inRoom[myIndex])) {
            for (i = partyThievesMaxDisp[myIndex]; i > 0; i--) {
                boolean tooFarOrOcupada = false;
                int[] posAfterMove = new int[assaultThievesPos.length + 1];
                posAfterMove[0] = myPos + i;
                System.arraycopy(assaultThievesPos, 0, posAfterMove, 1, assaultThievesPos.length);
                Arrays.sort(posAfterMove);

                for (int j = 0; j < posAfterMove.length - 1 && posAfterMove[j] != 0; j++) {
                    if ((posAfterMove[j + 1] - posAfterMove[j] > THIEVES_MAX_DISTANCE) || (posAfterMove[j + 1] - posAfterMove[j] == 0 && (posAfterMove[j + 1] != 0 && posAfterMove[j + 1] != getDistOutsideRoom()) && !(nThievesRoom == MAX_ASSAULT_PARTY_THIEVES - 1))) { //ultima condicao deve ser alterada
                        tooFarOrOcupada = true;
                        break;
                    }
                }

                if ((!tooFarOrOcupada)) {
                    canMoveAgain = true;
                    break;
                }
            }
            // Didn't get to room or can't walk further
        } else if (!canMoveAgain) {
            myTurn[myIndex] = false;

            int min = ROOM_MAX_DISTANCE;
            int minIndex = -1;

            for (int x = 0; x < MAX_ASSAULT_PARTY_THIEVES; x++) {
                if (min >= partyThievesPos[x]) {
                    min = partyThievesPos[x];
                    minIndex = x;
                }
            }

            if (minIndex == myIndex) {
                boolean changed = true;
                while (changed) {
                    changed = false;
                    for (int x = 0; x < MAX_ASSAULT_PARTY_THIEVES; x++) {

                        if (partyThievesPos[minIndex] + 1 == partyThievesPos[x] && partyThievesMaxDisp[minIndex] == 2) {
                            if (partyThievesPos[minIndex] + 1 == getDistOutsideRoom()) {
                                continue;
                            }
                            minIndex = x;
                            changed = true;
                        }
                    }
                }

            }
            myTurn[minIndex] = true;

            notifyAll();
        }
    }

    /**
     *
     * Simulates the operation reverseDirection of the Assault Party. The
     * Assault Thief current thread blocks until the last element of the Assault
     * Party executes this action.
     *
     */
    @Override
    public synchronized void reverseDirection() {
        myTurn[getIndexParty()] = false;
        inRoom[getIndexParty()] = false;

        if (nThievesRoom == MAX_ASSAULT_PARTY_THIEVES) {
            nThievesRoom = 0;
        }

        myTurn[0] = true;

        reverse++;
        while (reverse % MAX_ASSAULT_PARTY_THIEVES != 0 && reverse != 0) {
            try {
                wait();
            } catch (InterruptedException ex) {
                System.out.println(ex.getMessage());
            }
        }

        notifyAll();
    }

    /**
     *
     * Simulates the movement crawlOut of the Assault Thief current thread.
     *
     */
    @Override
    public synchronized void crawlOut() {
        AssaultThief thief = ((AssaultThief) Thread.currentThread());
        while (!myTurn[getIndexParty()]) {
            try {
                if (nThievesRoom == MAX_ASSAULT_PARTY_THIEVES) {
                    return;
                }
                wait();
            } catch (InterruptedException ex) {
                System.out.println(ex.getMessage());
            }
        }

        thief.setStatus(CRAWLING_OUTWARDS);

        int myIndex = getIndexParty();
        int myPos = thief.getPosition();
        int myAgility = thief.getMaxDisp();
        int[] assaultThievesPos = new int[MAX_ASSAULT_PARTY_THIEVES - 1];

        int count = 0;
        int i = 0;
        for (i = 0; i < MAX_ASSAULT_PARTY_THIEVES; i++) {
            if (i != myIndex) {
                assaultThievesPos[count] = partyThievesPos[i];
                count++;
            }
        }

        Arrays.sort(assaultThievesPos);

        /// Predict maximum displacement
        for (i = myAgility; i > 0; i--) {
            boolean tooFarOrOcupada = false;
            // Array que vai ter myPos no inicio e assaultThievesPos de seguida
            int[] posAfterMove = new int[assaultThievesPos.length + 1];
            posAfterMove[0] = myPos - i;
            System.arraycopy(assaultThievesPos, 0, posAfterMove, 1, assaultThievesPos.length);
            Arrays.sort(posAfterMove);

            for (int j = 0; j < posAfterMove.length - 1; j++) {
                if ((posAfterMove[j + 1] - posAfterMove[j] > THIEVES_MAX_DISTANCE) || (posAfterMove[j + 1] - posAfterMove[j] == 0 && (posAfterMove[j + 1] != getDistOutsideRoom() && posAfterMove[j + 1] != 0) && !(nThievesRoom == MAX_ASSAULT_PARTY_THIEVES - 1))) {
                    tooFarOrOcupada = true;
                    break;
                }
            }

            // Set new position
            if ((!tooFarOrOcupada)) {
                if (myPos - i <= 0) {
                    partyThievesPos[myIndex] = 0;
                    thief.setPosition(0);
                    nThievesRoom++;
                    inRoom[myIndex] = true;

                    log.setAssaultThief();
                    log.setAssaultParty(id, partyThieves, partyThievesPos, roomID);
                    log.reportStatus();
                } else {
                    partyThievesPos[myIndex] = myPos - i;
                    thief.setPosition(myPos - i);

                    log.setAssaultThief();
                    log.setAssaultParty(id, partyThieves, partyThievesPos, roomID);
                    log.reportStatus();

                }

                break;
            }
        }

        // Check if it is possible to move even further
        boolean canMoveAgain = false;
        if (!(myPos == partyThievesPos[myIndex] || inRoom[myIndex])) {
            for (i = partyThievesMaxDisp[myIndex]; i > 0; i--) {
                boolean tooFarOrOcupada = false;
                int[] posAfterMove = new int[assaultThievesPos.length + 1];
                posAfterMove[0] = myPos - i;
                System.arraycopy(assaultThievesPos, 0, posAfterMove, 1, assaultThievesPos.length);
                Arrays.sort(posAfterMove);

                for (int j = 0; j < posAfterMove.length - 1 && posAfterMove[j] != 0; j++) {
                    if ((posAfterMove[j + 1] - posAfterMove[j] > THIEVES_MAX_DISTANCE) || (posAfterMove[j + 1] - posAfterMove[j] == 0 && (posAfterMove[j + 1] != getDistOutsideRoom() && posAfterMove[j + 1] != 0) && !(nThievesRoom == MAX_ASSAULT_PARTY_THIEVES - 1))) {
                        tooFarOrOcupada = true;
                        break;
                    }
                }

                if ((!tooFarOrOcupada)) {
                    canMoveAgain = true;
                    break;
                }
            }
            // Didn't get to room or can't walk further
        } else if (!canMoveAgain) {
            myTurn[myIndex] = false;

            int max = 0;
            int maxIndex = -1;

            for (int x = 0; x < MAX_ASSAULT_PARTY_THIEVES; x++) {
                if (max <= partyThievesPos[x]) {
                    max = partyThievesPos[x];
                    maxIndex = x;
                }
            }

            if (maxIndex == myIndex) {
                boolean changed = true;
                while (changed) {
                    changed = false;
                    for (int x = 0; x < MAX_ASSAULT_PARTY_THIEVES; x++) {
                        if (partyThievesPos[maxIndex] - 1 == partyThievesPos[x] && partyThievesMaxDisp[maxIndex] == 2) {
                            if (partyThievesPos[maxIndex] - 1 == 0) {
                                continue;
                            }
                            maxIndex = x;
                            changed = true;
                        }
                    }
                }
            }

            myTurn[maxIndex] = true;

            notifyAll();
        }

        thief.setStatus(OUTSIDE);
    }

    /**
     * Add an Assault Thief to the Assault Party.
     *
     * @param thief
     * @return True, if the operation was successful or false if otherwise
     */
    @Override
    public synchronized boolean addThief(AssaultThief thief) {
        for (int i = 0; i < MAX_ASSAULT_PARTY_THIEVES; i++) {
            if (partyThieves[i] == -1) {
                partyThieves[i] = thief.getThiefID();
                partyThievesPos[i] = 0;
                partyThievesMaxDisp[i] = thief.getMaxDisp();

                log.setAssaultParty(id, partyThieves, partyThievesPos, roomID);
                log.reportStatus();

                return true;
            }
        }

        return false;
    }

    /**
     * Get index of current Assault Thief thread in the partyThieves array.
     *
     * @return Returns the index of the current Assault Thief thread in the
     * partyThieves array
     */
    @Override
    public synchronized int getIndexParty() {
        AssaultThief thief = (AssaultThief) Thread.currentThread();

        for (int i = 0; i < MAX_ASSAULT_PARTY_THIEVES; i++) {
            if (partyThieves[i] == thief.getThiefID()) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Set the turn to crawl of the element of index 0 to true.
     *
     */
    @Override
    public void setFirst() {
        myTurn[0] = true;
    }

    /**
     * Set the ID of an Assault Thief in the current Assault Party.
     *
     * @param i
     * @param value
     */
    @Override
    public void setPartyThieves(int i, int value) {
        partyThieves[i] = value;
    }

    /**
     * Get Assault Thieves ID of the current Assault Party.
     *
     * @return Returns array partyThieves ID of the current Assault Party
     */
    @Override
    public int[] getPartyThieves() {
        return partyThieves;
    }

    /**
     * Get Assault Thieves positions of the current Assault Party.
     *
     * @return Returns array partyThieves ID of the current Assault Party
     */
    @Override
    public int[] getPartyThievesPos() {
        return partyThievesPos;
    }

    /**
     * Get Assault Thieves maximum displacement of the current Assault Party.
     *
     * @return Returns array partyThieves ID of the current Assault Party
     */
    @Override
    public int[] getPartyThievesMaxDisp() {
        return partyThievesMaxDisp;
    }

    /**
     *
     * Set a roomID to this Assault Party.
     *
     * @param roomID
     */
    @Override
    public void setRoom(int roomID) {
        this.roomID = roomID;
    }

    /**
     * Get the roomID assigned to the current Assault Party.
     *
     * @return Returns roomIDy
     */
    @Override
    public int getRoomID() {
        return roomID;
    }

    /**
     * +
     * Get distance from the Outside to the Room assigned to this Assault Party
     *
     * @return Returns distance from the Outside to the Room assigned to this
     * Assault Party
     */
    @Override
    public int getDistOutsideRoom() {
        return museum.getRoom(roomID).getDistOutside();
    }
}
