package serverSide.ControlCollectionSite;

import auxiliary.messages.*;
import serverSide.Interface;
import static auxiliary.messages.Message.*;

/**
 * This is the interface class for the Control and Collection Site data type. It executes the
 * operations on the Control and Collection Site based on the received messages.
 *
 * @author António Mota
 * @author Marcos Pires
 */
public class IControlCollectionSite extends Interface {

    /**
     * Control and Collection Site (represents the provided service)
     *
     * @serialField ccs
     */
    private final ControlCollectionSite ccs;

    /**
     * Control and Collection Site interface instantiation
     *
     *
     * @param ccs Represents the Control and Collection Site data type
     */
    public IControlCollectionSite(ControlCollectionSite ccs) {
        this.ccs = ccs;
    }

    /**
     * Message processment by executing the correspondent operation. Reply
     * message generation
     *
     * @param inMessage Message with the request
     *
     * @return outMessage - Message with reply
     *
     * @throws MessageException if the message with request is invalid
     */
    @Override
    public Message processAndReply(Message inMessage) throws MessageException {
        Message outMessage = null;                     

        switch (inMessage.getType()) {
            case GET_NEXT_ROOM:
                int nextRoom = ccs.getNextRoom();
                outMessage = new Message(Message.ACK, nextRoom);
                break;
            case ISREADY:
                ccs.isReady();
                outMessage = new Message(Message.ACK);
                break;
            case APPRAISE_SIT:
                int situation = ccs.appraiseSit(inMessage.getInteger0(), inMessage.getInteger1());
                outMessage = new Message(Message.ACK, situation);
                break;
            case GET_NEXT_AP:
                int partyID = ccs.getNextParty();
                outMessage = new Message(Message.ACK, partyID);
                break;
            case SENDAP:
                ccs.sendAssaultParty(inMessage.getInteger0());
                outMessage = new Message(Message.ACK);
                break;
            case PREPAREEXCURSION:
                ccs.prepareExcursion(inMessage.getInteger0(), inMessage.getInteger1(), inMessage.getInteger2(), inMessage.getInteger3(), inMessage.getInteger4());
                outMessage = new Message(Message.ACK);
                break;
            case HAND_CANVAS:
                ccs.handCanvas(inMessage.getInteger0(), inMessage.getInteger1(), inMessage.getInteger2(), inMessage.getInteger3(), inMessage.getInteger4());
                outMessage = new Message(Message.ACK);
                break;
            case TAKE_A_REST:
                ccs.takeARest(inMessage.getInteger0());
                outMessage = new Message(Message.ACK);
                break;
            case COLLECT_CANVAS:
                ccs.collectCanvas(inMessage.getInteger0());
                outMessage = new Message(Message.ACK);
                break;
            case SUM_UP_RESULTS:
                ccs.sumUpResults(inMessage.getInteger0());
                outMessage = new Message(Message.ACK);
                break;
            case NEXT_EMPTY_ROOM:
                int nextEmptyRoom = ccs.nextEmptyRoom();
                outMessage = new Message(Message.ACK, nextEmptyRoom);
                break;
        }

        return outMessage;
    }
}
