package monitors;

import interfaces.ILogger;
import threads.*;
import genclass.*;
import static auxiliary.constants.Constants.*;
import interfaces.*;

/**
 *
 * @author António Mota
 * @author Marcos Pires
 */
public class Logger implements ILogger {

    private String fileName;
    private int masterThiefStatus;
    private int[] assaultThiefStatus;
    private int[] assaultThiefMaxDisp;
    private String[] assaultThiefSituation;
    private int[] hasCanvas;
    private int nPaintings;
    private Room[] rooms;
    private TextFile log;
    private int[][] asspartiesPos;
    private int[][] assparties;
    private int[] asspartiesRoomID;

    /**
     *
     */
    public Logger() {
        log = new TextFile();
        assaultThiefStatus = new int[THIEVES_NUMBER];
        assaultThiefMaxDisp = new int[THIEVES_NUMBER];
        assaultThiefSituation = new String[THIEVES_NUMBER];
        hasCanvas = new int[THIEVES_NUMBER];
        nPaintings = 0;
        rooms = new Room[ROOMS_NUMBER];

        assparties = new int[2][MAX_ASSAULT_PARTY_THIEVES];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < MAX_ASSAULT_PARTY_THIEVES; j++) {
                assparties[i][j] = -1;
            }
        }

        asspartiesPos = new int[2][MAX_ASSAULT_PARTY_THIEVES];

        asspartiesRoomID = new int[2];
        for (int i = 0; i < 2; i++) {
            asspartiesRoomID[i] = -1;
        }
    }

    /**
     * Set the number of Paintings in the General Repository of Information.
     *
     * @param nPaintings
     */
    @Override
    public void setnPaintings(int nPaintings) {
        this.nPaintings = nPaintings;
    }

    /**
     * Set an Assault Party in the General Repository of Information.
     *
     * @param id
     * @param elements
     * @param positions
     * @param roomID
     */
    @Override
    public void setAssaultParty(int id, int[] elements, int[] positions, int roomID) {
        assparties[id] = elements;
        asspartiesPos[id] = positions;
        asspartiesRoomID[id] = roomID;
    }

    /**
     * Set the Master Thief in the General Repository of Information.
     *
     */
    @Override
    public void setMasterThief() {
        MasterThief mthief = (MasterThief) Thread.currentThread();

        masterThiefStatus = mthief.getStatus();
    }

    /**
     * Set an Assault Thief in the General Repository of Information.
     *
     */
    @Override
    public void setAssaultThief() {
        AssaultThief thief = (AssaultThief) Thread.currentThread();

        assaultThiefStatus[thief.getThiefID()] = thief.getStatus();
        assaultThiefMaxDisp[thief.getThiefID()] = thief.getMaxDisp();
        if (thief.getPartyID() == -1) {
            assaultThiefSituation[thief.getThiefID()] = "W";
        } else {
            assaultThiefSituation[thief.getThiefID()] = "" + thief.getPartyID();
        }
        hasCanvas[thief.getThiefID()] = thief.getHasCanvas();
    }

    /**
     * Set the Museum in the General Repository of Information.
     *
     * @param rooms
     */
    @Override
    public void setMuseum(Room[] rooms) {
        this.rooms = rooms;
    }

    /**
     * Start a file where all the operations will be written along with the
     * changes to the main attributes of the Heist.
     *
     */
    @Override
    public synchronized void startLog() {
        char opt;                                            // opção
        boolean success;                                     // validação de dados de entrada

        GenericIO.writelnString("\n" + "      Heist to the Museum\n");

        do {
            GenericIO.writeString("Nome do ficheiro de armazenamento da simulação? ");
            fileName = GenericIO.readlnString();
            //fileName = "teste";
            if (FileOp.exists(".", fileName)) {
                do {
                    GenericIO.writeString("Já existe um directório/ficheiro com esse nome. Quer apagá-lo (s - sim; n - não)? ");
                    opt = GenericIO.readlnChar();
                    //opt = 's';
                } while ((opt != 's') && (opt != 'n'));
                if (opt == 's') {
                    success = true;
                } else {
                    success = false;
                }
            } else {
                success = true;
            }
        } while (!success);

    }

    /**
     * Log the initial status of the Heist.
     *
     */
    @Override
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
    @Override
    public synchronized void reportStatus() {
        if (!log.openForAppending(".", fileName)) {
            GenericIO.writelnString("3 A operação de criação do ficheiro " + fileName + " falhou!");
            System.exit(1);
        }

        String line = "";
        String line2 = "";

        line += masterThiefStatus + "  ";
        for (int i = 0; i < THIEVES_NUMBER; i++) {
            if (assaultThiefStatus[i] != 0) {
                line += assaultThiefStatus[i] + " " + assaultThiefSituation[i] + "  " + assaultThiefMaxDisp[i] + "    ";
            } else {
                line += "----" + " " + "-" + "  " + "-" + "    ";
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
                        line2 += assparties[i][j] + "   " + asspartiesPos[i][j] + "  " + hasCanvas[assparties[i][j]] + "   ";
                    } else {
                        line2 += assparties[i][j] + "  " + asspartiesPos[i][j] + "  " + hasCanvas[assparties[i][j]] + "   ";
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

        log.writelnString(line);
        log.writelnString(line2);

        if (!log.close()) {
            GenericIO.writelnString("A operação de fecho do ficheiro " + fileName + " falhou!");
            System.exit(1);
        }
    }

    /**
     * Log the final status of the Heist.
     *
     */
    @Override
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
}
