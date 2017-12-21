package auxiliary.messages;

import java.io.*;
import java.util.HashMap;
import serverSide.Museum.Room;

/**
 *
 * This data type defines the exchanged messages between server and clients in a
 * solution of the Heist to the Museum that implements a type 2 client-server
 * model (with server replication) with static "lancamento" of thieves threads
 * The communication is based on the exchange of type Message objectes in a TCP
 * channel.
 */
public class Message implements Serializable {

    private static final long serialVersionUID = 1001L;

    /**
     * Message type for establishment of initial parameters of Logger
     */
    public static final int SETNFIC = 1;

    /**
     * Message type for updating GRI and report in logging file
     */
    public static final int REP_STATUS_AT = 3;

    /**
     * Message type for CS to execute the startOfOperations method
     */
    public static final int STARTOP = 5;

    /**
     * Message type for CS to execute the startOfOperations method
     */
    public static final int AMINEEDED = 7;

    /**
     *
     */
    public static final int GET_PTHIEVES = 13;

    /**
     *
     */
    public static final int ENDOP = 15;

    /**
     *
     */
    public static final int ACK = 16;

    /**
     *
     */
    public static final int GET_NEXT_ROOM = 17;

    /**
     *
     */
    public static final int PREPARE_AP = 18;

    /**
     *
     */
    public static final int SET_AP_ROOM = 19;

    /**
     *
     */
    public static final int ISREADY = 20;

    /**
     *
     */
    public static final int GET_ASSAULT_THIEVES_CS = 21;

    /**
     *
     */
    public static final int APPRAISE_SIT = 22;

    /**
     *
     */
    public static final int ADDTHIEF = 23;

    /**
     *
     */
    public static final int GET_NEXT_AP = 24;

    /**
     *
     */
    public static final int IS_EMPTY_AP = 25;

    /**
     *
     */
    public static final int SENDAP = 26;

    /**
     *
     */
    public static final int PREPAREEXCURSION = 27;

    /**
     *
     */
    public static final int GET_DIST_OUTSIDE = 28;

    /**
     *
     */
    public static final int CRAWL_IN = 29;

    /**
     *
     */
    public static final int SETFIRST = 30;

    /**
     *
     */
    public static final int GET_ROOM_ID = 31;

    /**
     *
     */
    public static final int REVERSE_DIRECTION = 32;

    /**
     *
     */
    public static final int CRAWL_OUT = 33;

    /**
     *
     */
    public static final int ROLL_A_CANVAS = 34;

    /**
     *
     */
    public static final int HAND_CANVAS = 35;

    /**
     *
     */
    public static final int SET_PTHIEVES = 36;

    /**
     *
     */
    public static final int TAKE_A_REST = 37;

    /**
     *
     */
    public static final int COLLECT_CANVAS = 38;

    /**
     *
     */
    public static final int SUM_UP_RESULTS = 39;

    /**
     *
     */
    public static final int NEXT_EMPTY_ROOM = 40;

    /**
     *
     */
    public static final int GET_CONFIGS = 41;

    /**
     *
     */
    public static final int REP_STATUS_MT = 42;

    /**
     *
     */
    public static final int REP_STATUS_AP = 43;

    /**
     *
     */
    public static final int REP_MUSEUM = 44;

    /* Message fields */
    /**
     * Message type
     *
     * @serialField msgType
     */
    private int msgType = -1;

    private int integer0;
    private int integer1;
    private int integer2;
    private int integer3;
    private int integer4;
    private int integer5;
    private boolean b;
    private int partyThieves[];
    private int partyThievesPos[];
    private Room rooms[];

    /**
     * Logging file name
     *
     * @serialField fName
     */
    private String fName = null;

    /**
     * Thieves lifecyle iterations number
     *
     * @serialField nIter
     */
    private int nIter = -1;
    private HashMap<String, String>[] map;

    /**
     *
     * @param type
     */
    public Message(int type) {
        msgType = type;
    }

    /**
     *
     * @param type
     * @param integer0
     */
    public Message(int type, int integer0) {
        msgType = type;
        this.integer0 = integer0;
    }

    /**
     *
     * @param type
     * @param integer0
     * @param integer1
     */
    public Message(int type, int integer0, int integer1) {
        msgType = type;
        this.integer0 = integer0;
        this.integer1 = integer1;
    }

    /**
     *
     * @param type
     * @param integer0
     * @param integer1
     * @param integer2
     */
    public Message(int type, int integer0, int integer1, int integer2) {
        msgType = type;
        this.integer0 = integer0;
        this.integer1 = integer1;
        this.integer2 = integer2;
    }

    /**
     *
     * @param type
     * @param integer0
     * @param integer1
     * @param integer2
     * @param integer3
     * @param integer4
     */
    public Message(int type, int integer0, int integer1, int integer2, int integer3, int integer4) {
        msgType = type;
        this.integer0 = integer0;
        this.integer1 = integer1;
        this.integer2 = integer2;
        this.integer3 = integer3;
        this.integer4 = integer4;
    }
    
    /**
     *
     * @param type
     * @param integer0
     * @param integer1
     * @param integer2
     * @param integer3
     * @param integer4
     * @param integer5
     */
    public Message(int type, int integer0, int integer1, int integer2, int integer3, int integer4, int integer5) {
        msgType = type;
        this.integer0 = integer0;
        this.integer1 = integer1;
        this.integer2 = integer2;
        this.integer3 = integer3;
        this.integer4 = integer4;
        this.integer5 = integer5;
    }

    /**
     *
     * @param type
     * @param partyThieves
     */
    public Message(int type, int[] partyThieves) {
        msgType = type;
        this.partyThieves = partyThieves;
    }

    /**
     *
     * @param type
     * @param integer0
     * @param partyThieves
     * @param partyThievesPos
     * @param integer1
     */
    public Message(int type, int integer0, int[] partyThieves, int[] partyThievesPos, int integer1) {
        msgType = type;
        this.integer0 = integer0;
        this.partyThieves = partyThieves;
        this.partyThievesPos = partyThievesPos;
        this.integer1 = integer1;
    }
    
    /**
     *
     * @param type
     * @param rooms
     */
    public Message(int type, Room[] rooms) {
        msgType = type;
        this.rooms = rooms;
    }

    /**
     *
     * @param type
     * @param name
     * @param nIter
     */
    public Message(int type, String name, int nIter) {
        msgType = type;
        fName = name;
        this.nIter = nIter;
    }

    /**
     *
     * @param type
     * @param map
     */
    public Message(int type, HashMap<String, String>[] map) {
        msgType = type;
        this.map = map;
        System.out.println("Config Message");
    }

    /**
     *
     * @param type
     * @param b
     */
    public Message(int type, boolean b) {
        msgType = type;
        this.b = b;
    }

    /**
     *
     * @return
     */
    public int getInteger0() {
        return integer0;
    }

    /**
     *
     * @return
     */
    public int getInteger1() {
        return integer1;
    }

    /**
     *
     * @return
     */
    public int getInteger2() {
        return integer2;
    }

    /**
     *
     * @return
     */
    public int getInteger3() {
        return integer3;
    }

    /**
     *
     * @return
     */
    public int getInteger4() {
        return integer4;
    }

    /**
     *
     * @return
     */
    public int getInteger5() {
        return integer5;
    }
    
    /**
     *
     * @return
     */
    public int[] getPartyThieves() {
        return partyThieves;
    }

    /**
     *
     * @return
     */
    public int[] getPartyThievesPos() {
        return partyThievesPos;
    }

    /**
     *
     * @return
     */
    public Room[] getRooms() {
        return rooms;
    }
    
    

    /**
     * Get value of message type field
     *
     * @return message type
     */
    public int getType() {
        return (msgType);
    }

    /**
     * Get value of logging file name field
     *
     * @return file name
     */
    public String getFName() {
        return (fName);
    }

    /**
     * Get value of thieves lifecycle iterations number field
     *
     * @return thieves lifecycle iterations number
     */
    public int getNIter() {
        return (nIter);
    }

    /**
     * Internal fields print. Used for debugging.
     *
     * @return string containing, in separate lines, the concatenatino of the
     * field identification and its value
     */
    @Override
    public String toString() {
        return ("Type = " + msgType
                + "\nThief Id = ");
    }

    /**
     *
     * @return
     */
    public boolean isB() {
        return b;
    }

    /**
     *
     * @return
     */
    public HashMap<String, String>[] getMap() {
        return map;
    }

}
