package serverSide;

import static auxiliary.constants.Constants.*;
import genclass.*;
import interfaces.LoggerInterface;
import java.rmi.RemoteException;
import auxiliary.time.Room;

/**
 * This data type represents the Logger of the Heist to the Museum Problem.
 *
 * @author António Mota
 * @author Marcos Pires
 */
public class Logger implements LoggerInterface {

    /**
     * Name of the file that will contain the log of the whole heist
     */
    private String fileName;
    /**
     * Status of the Master thief
     */
    private String masterThiefStatus;
    /**
     * List of all the status of all the assault thieves
     */
    private String[] assaultThiefStatus;
    /**
     * List of the maximum displacements of all the assault thieves
     */
    private int[] assaultThiefMaxDisp;
    /**
     * List of the current situations of the assault thieves, for 
     * printing purposes
     */
    private String[] assaultThiefSituation;
    /**
     * List which values mean whether an assault thief has 
     * a canvas or not, the thief id corresponds to it's placement
     * in this list
     */
    private int[] hasCanvas;
    /**
     * Total number of paintings stollen at the moment
     */
    private int nPaintings;
    /**
     * List of Rooms of the museum
     */
    private Room[] rooms;
    /**
     * TextFile where the log will be written
     */
    private TextFile log;
    /**
     * Bi Dimensional list with the assault thieves' positions 
     */
    private int[][] asspartiesPos;
    /**
     * Bi Dimensional list with the assault thieves' ids and to which assault party they are
     * assigned to
     */
    private int[][] assparties;
    /**
     * Rooms ids to which the assault parties were assigned to
     */
    private int[] asspartiesRoomID;
    /**
     * Next lines of text to be written in the logging file
     */
    private String lastLine, lastLine2;
    /**
     * number of Heists to be made
     */
    private int nIter;

    public Logger() {
        log = new TextFile();
        masterThiefStatus = "---";

        assaultThiefStatus = new String[THIEVES_NUMBER];
        for (int i = 0; i < THIEVES_NUMBER; i++) {
            assaultThiefStatus[i] = "-";
        }
        assaultThiefMaxDisp = new int[THIEVES_NUMBER];
        for (int i = 0; i < THIEVES_NUMBER; i++) {
            assaultThiefMaxDisp[i] = -1;
        }
        assaultThiefSituation = new String[THIEVES_NUMBER];
        for (int i = 0; i < THIEVES_NUMBER; i++) {
            assaultThiefSituation[i] = "-";
        }
        hasCanvas = new int[THIEVES_NUMBER];
        for (int i = 0; i < THIEVES_NUMBER; i++) {
            hasCanvas[i] = 0;
        }
        assparties = new int[2][MAX_ASSAULT_PARTY_THIEVES];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < MAX_ASSAULT_PARTY_THIEVES; j++) {
                assparties[i][j] = -1;
            }
        }
        asspartiesPos = new int[2][MAX_ASSAULT_PARTY_THIEVES];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < MAX_ASSAULT_PARTY_THIEVES; j++) {
                asspartiesPos[i][j] = -1;
            }
        }
        asspartiesRoomID = new int[2];
        for (int i = 0; i < 2; i++) {
            asspartiesRoomID[i] = -1;
        }
        rooms = new Room[ROOMS_NUMBER];
        for (int i = 0; i < ROOMS_NUMBER; i++) {
            rooms[i] = new Room(-1);
        }

        nPaintings = 0;
        lastLine = "";
        lastLine2 = "";

    }

    /**
     * Set the number of Paintings in the General Repository of Information.
     *
     * @param nPaintings Number of paintings
     */
    public void setnPaintings(int nPaintings) {
        this.nPaintings = nPaintings;
    }

    /**
     * Set an Assault Party in the General Repository of Information.
     *
     * @param id ID of the party
     * @param elements Array of thieves ID's of the party
     * @param positions Array of thieves positions of the party
     * @param roomID ID of the room assigned to the party
     */
    public void setAssaultParty(int id, int[] elements, int[] positions, int roomID) {
        assparties[id] = elements;
        asspartiesPos[id] = positions;
        asspartiesRoomID[id] = roomID;
    }

    /**
     * Set the Master Thief status in the General Repository of Information.
     *
     * @param status Status of the master thief
     */
    public void setMasterThief(int status) {
        switch (status) {
            case 1000:
                masterThiefStatus = "PLAN";
                break;
            case 2000:
                masterThiefStatus = "DCID";
                break;
            case 3000:
                masterThiefStatus = "ASSG";
                break;
            case 4000:
                masterThiefStatus = "WAIT";
                break;
            case 5000:
                masterThiefStatus = "REPO";
                break;

        }
    }

    /**
     * Set an Assault Thief in the General Repository of Information.
     *
     * @param thiefID ID of the assault thief
     * @param status Status of the thief
     * @param maxDisp Maximum Displacement of the thief
     * @param partyID ID of the party assigned to the thief
     * @param hasCanvas Represents what the thief is carrying, 0 - empty handed,
     * 1 - got 1 canvas or -1 default value
     */
    public void setAssaultThief(int thiefID, int status, int maxDisp, int partyID, int hasCanvas) {
        switch (status) {
            case 1000:
                assaultThiefStatus[thiefID] = "OUTS";
                break;
            case 1001:
                assaultThiefStatus[thiefID] = "WAIT";
                break;
            case 2000:
                assaultThiefStatus[thiefID] = "CR_I";
                break;
            case 3000:
                assaultThiefStatus[thiefID] = "ROOM";
                break;
            case 4000:
                assaultThiefStatus[thiefID] = "CR_O";
                break;
            case 5000:
                assaultThiefStatus[thiefID] = "COLL";
                break;
            case 6000:
                assaultThiefStatus[thiefID] = "HEND";
                break;

        }
        assaultThiefMaxDisp[thiefID] = maxDisp;
        if (partyID == -1) {
            assaultThiefSituation[thiefID] = "W";
        } else {
            assaultThiefSituation[thiefID] = "" + partyID;
        }
        this.hasCanvas[thiefID] = hasCanvas;
    }

    /**
     * Set the Museum in the General Repository of Information.
     *
     * @param rooms Rooms of the museum
     */
    public void setMuseum(Room[] rooms) {
        this.rooms = rooms;
    }

    /**
     * Start a file where all the operations will be written along with the
     * changes to the main attributes of the Heist.
     *
     * @param fileName File name for the logging of the problem
     * @param nIter Number of iterations of the problem
     */
    public synchronized void setFileName(String fileName, int nIter) {
        if ((fileName != null) && !("".equals(fileName))) {
            this.fileName = fileName;
        }
        if (nIter > 0) {
            this.nIter = nIter;
        }
    }

    /**
     * Log the initial status of the Heist.
     *
     */
    public void reportInitialStatus() {
        if (!log.openForWriting(".", fileName)) {
            GenericIO.writelnString("2 A operação de criação do ficheiro " + fileName + " falhou!");
            System.exit(1);
        }
        log.writelnString("                             Heist to the Museum - Description of the internal state\n\n");
        log.writelnString("MstT   Thief 1      Thief 2      Thief 3      Thief 4      Thief 5      Thief 6");
        log.writelnString("Stat  Stat S MD    Stat S MD    Stat S MD    Stat S MD    Stat S MD    Stat S MD");
        log.writelnString("                   Assault party 1                       Assault party 2                       Museum");
        log.writelnString("           Elem 1     Elem 2     Elem 3          Elem 1     Elem 2     Elem 3   Room 1  Room 2  Room 3  Room 4  Room 5");
        log.writelnString("    RId  Id Pos Cv  Id Pos Cv  Id Pos Cv  RId  Id Pos Cv  Id Pos Cv  Id Pos Cv   NP DT   NP DT   NP DT   NP DT   NP DT");
        if (!log.close()) {
            GenericIO.writelnString("A operação de fecho do ficheiro " + fileName + " falhou!");
            System.exit(1);
        }
    }

    /**
     * Log the status of everything in the General Repository of Information.
     *
     */
    public synchronized void reportStatus() {
        boolean dontPrint = false;
        if (!log.openForAppending(".", fileName)) {
            GenericIO.writelnString("3 A operação de criação do ficheiro " + fileName + " falhou!");
            System.exit(1);
        }

        String line = "";
        String line2 = "";

        line += masterThiefStatus + "  ";
        for (int i = 0; i < THIEVES_NUMBER; i++) {
            if (!assaultThiefStatus[i].equals("-")) {
                line += assaultThiefStatus[i] + " " + assaultThiefSituation[i] + "  " + assaultThiefMaxDisp[i] + "    ";
            } else {
                //line += "----" + " " + "-" + "  " + "-" + "    ";
                dontPrint = true;
            }
        }

        line2 += "     ";

        for (int i = 0; i < 2; i++) {
            if (asspartiesRoomID[i] == -1) {
                line2 += "-" + "    ";
            } else {
                line2 += asspartiesRoomID[i] + "    ";
            }

            for (int j = 0; j < MAX_ASSAULT_PARTY_THIEVES; j++) {
                if (assparties[i][j] != -1) {
                    if (asspartiesPos[i][j] < 10) {
                        line2 += (assparties[i][j] + 1) + "   " + asspartiesPos[i][j] + "  " + hasCanvas[assparties[i][j]] + "   ";
                    } else {
                        line2 += (assparties[i][j] + 1) + "  " + asspartiesPos[i][j] + "  " + hasCanvas[assparties[i][j]] + "   ";
                    }
                } else {
                    line2 += "-" + "  " + "--" + "  " + "-" + "   ";
                }
            }
            line2 += "";
        }

        for (int i = 0; i < ROOMS_NUMBER; i++) {
            if (rooms[i].getNPaintings() < 10) {
                line2 += " " + rooms[i].getNPaintings() + " " + rooms[i].getDistOutside() + "   ";
            } else {
                line2 += rooms[i].getNPaintings() + " " + rooms[i].getDistOutside() + "   ";
            }
        }

        if (!dontPrint) {
            dontPrint = true;
            if (!(lastLine.equals(line) && lastLine2.equals(line2))) {
                log.writelnString(line);
                log.writelnString(line2);
                lastLine = line;
                lastLine2 = line2;
            }
        }

        if (!log.close()) {
            GenericIO.writelnString("A operação de fecho do ficheiro " + fileName + " falhou!");
            System.exit(1);
        }
    }

    /**
     * Log the final status of the Heist.
     *
     */
    public synchronized void reportFinalStatus() {
        if (!log.openForAppending(".", fileName)) {
            GenericIO.writelnString("A operação de criação do ficheiro " + fileName + " falhou!");
            System.exit(1);
        }

        log.writelnString("My friends, tonight's effor produced " + nPaintings + " priceless paintings!");

        if (!log.close()) {
            GenericIO.writelnString("A operação de fecho do ficheiro " + fileName + " falhou!");
            System.exit(1);
        }
    }

    @Override
    public void signalShutdown() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
