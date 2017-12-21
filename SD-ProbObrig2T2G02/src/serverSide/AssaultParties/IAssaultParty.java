package serverSide.AssaultParties;

import auxiliary.messages.*;
import serverSide.Interface;
import static auxiliary.messages.Message.*;

/**
 * This is the interface class for the Control and Collection Site data type. It
 * executes the operations on the Control and Collection Site based on the
 * received messages.
 *
 * @author António Mota
 * @author Marcos Pires
 */
public class IAssaultParty extends Interface {

    /**
     * Assault Party (represents the provided service)
     *
     * @serialField assparty
     */
    private final AssaultParty assparty;

    /**
     * Assault Party interface instantiation
     *
     *
     * @param assparty Represents the Assault Party data type
     */
    public IAssaultParty(AssaultParty assparty) {
        this.assparty = assparty;
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
            case IS_EMPTY_AP:
                boolean emptyAP = assparty.isEmptyAP();
                outMessage = new Message(Message.ACK, emptyAP);
                break;
            case ADDTHIEF:
                assparty.addThief(inMessage.getInteger0(), inMessage.getInteger1());
                outMessage = new Message(Message.ACK);
                break;
            case GET_PTHIEVES:
                int[] pthieves = assparty.getPartyThieves();
                outMessage = new Message(Message.ACK, pthieves);
                break;
            case GET_DIST_OUTSIDE:
                int distOutside = assparty.getDistOutsideRoom();
                outMessage = new Message(Message.ACK, distOutside);
                break;
            case SET_AP_ROOM:
                assparty.setRoom(inMessage.getInteger0());
                outMessage = new Message(Message.ACK);
                break;
            case CRAWL_IN:
                assparty.crawlIn(inMessage.getInteger0(), inMessage.getInteger1(), inMessage.getInteger2(), inMessage.getInteger3(), inMessage.getInteger4());
                outMessage = new Message(Message.ACK);
                break;
            case SETFIRST:
                System.out.println("AP: SETFIRST");
                assparty.setFirst();
                outMessage = new Message(Message.ACK);
                break;
            case GET_ROOM_ID:
                int roomID = assparty.getRoomID();
                outMessage = new Message(Message.ACK, roomID);
                break;
            case REVERSE_DIRECTION:
                assparty.reverseDirection(inMessage.getInteger0(), inMessage.getInteger1(), inMessage.getInteger2(), inMessage.getInteger3(), inMessage.getInteger4());
                outMessage = new Message(Message.ACK);
                break;
            case CRAWL_OUT:
                assparty.crawlOut(inMessage.getInteger0(), inMessage.getInteger1(), inMessage.getInteger2(), inMessage.getInteger3(), inMessage.getInteger4());
                outMessage = new Message(Message.ACK);
                break;
            case SET_PTHIEVES:
                assparty.setPartyThieves(inMessage.getInteger0(), inMessage.getInteger1());
                outMessage = new Message(Message.ACK);
                break;
        }

        return outMessage;
    }
}
