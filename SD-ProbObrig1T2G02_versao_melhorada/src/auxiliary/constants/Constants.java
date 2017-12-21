package auxiliary.constants;

/**
 *
 * @author António Mota
 * @author Marcos Pires
 */
public class Constants {

    // Status of AssaultThief
    public final static int 
            /**
             * Status OUTSIDE.
             */
            OUTSIDE = 1000,
            /**
             * Status WAITING_SEND_ASSAULT_PARTY.
             */
            WAITING_SEND_ASSAULT_PARTY = 1001,
            /**
             * Status CRAWLING_INWARDS.
             */
            CRAWLING_INWARDS = 2000,
            /**
             * Status AT_A_ROOM.
             */
            AT_A_ROOM = 3000,
            /**
             * Status CRAWLING_OUTWARDS.
             */
            CRAWLING_OUTWARDS = 4000,
            /**
             * Status AT_COLLECTION_SITE.
             */
            AT_COLLECTION_SITE = 5000,
            /**
             * Status HEIST_END.
             */
            HEIST_END = 6000;

    // Status of MasterThief
    public final static int 
            /**
             * Status PLANNING_THE_HEIST.
             */
            PLANNING_THE_HEIST = 1000,
            /**
             * Status DECIDING_WHAT_TO_DO.
             */
            DECIDING_WHAT_TO_DO = 2000,
            /**
             * Status ASSEMBLING_A_GROUP.
             */
            ASSEMBLING_A_GROUP = 3000,
            /**
             * Status WAITING_FOR_GROUP_ARRIVAL.
             */
            WAITING_FOR_GROUP_ARRIVAL = 4000,
            /**
             * Status PRESENTING_THE_REPORT.
             */
            PRESENTING_THE_REPORT = 5000;

    /**
     * Total number of AssaultParties in the Heist.
     */
    public static final int MAX_ASSAULT_PARTIES = 2;

    /**
     * Total number of AssaultThieves in the Heist.
     */
    public static final int THIEVES_NUMBER = 6;

    /**
     * AssaultThieves maximum displacement.
     */
    public static final int THIEVES_MAX_DISPLACEMENT = 6;

    /**
     * AssaultThieves minimum displacement.
     */
    public static final int THIEVES_MIN_DISPLACEMENT = 2;

    /**
     * AssaultThieves maximum distance between them while crawling in.
     */
    public static final int THIEVES_MAX_DISTANCE = 3;

    /**
     * Room maximum distance to Outside.
     */
    public static final int ROOM_MAX_DISTANCE = 30;

    /**
     * Maximum number of AssaultThieves in an AssaultParty.
     */
    public static final int MAX_ASSAULT_PARTY_THIEVES = 3;

    /**
     * Total number of Rooms in the Museum.
     */
    public static final int ROOMS_NUMBER = 5;

    /**
     * Paintings minimum number in a Room.
     */
    public static final int MIN_PAINTINGS = 8;

    /**
     * Paintings maximum number in a Room.
     */
    public static final int MAX_PAINTINGS = 16;

    /**
     * Maximum distance from the Room in the Museum to the Outside.
     */
    public static final int MAX_DIST_OUTSIDE = 30;

    /**
     * Minimum distance from the Room in the Museum to the Outside.
     */
    public static final int MIN_DIST_OUTSIDE = 15;
}
