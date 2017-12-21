package serverSide.ConcentrationSite;

import auxiliary.messages.*;
import serverSide.Interface;
import static auxiliary.messages.Message.*;

/**
 * This is the interface class for the Concentration Site data type. It executes
 * the operations on the Concentration Site based on the received messages.
 *
 * @author António Mota
 * @author Marcos Pires
 */
public class IConcentrationSite extends Interface {

    /**
     * Concentration Site (represents the provided service)
     *
     * @serialField cs
     */
    private final ConcentrationSite cs;

    /**
     * Control and Collection Site interface instantiation
     *
     *
     * @param cs Represents the Concentration Site data type
     */
    public IConcentrationSite(ConcentrationSite cs) {
        this.cs = cs;
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

        /* seu processamento */
        switch (inMessage.getType()) {
            case AMINEEDED:
                int partyID = cs.amINeeded(inMessage.getInteger0(), inMessage.getInteger1(), inMessage.getInteger2(), inMessage.getInteger3(), inMessage.getInteger4());
                outMessage = new Message(Message.ACK, partyID);
                break;
            case STARTOP:
                cs.startOfOperations(inMessage.getInteger0());
                outMessage = new Message(Message.ACK, 2);
                break;
            case PREPARE_AP:
                cs.prepareAssaultParty(inMessage.getInteger0());
                outMessage = new Message(Message.ACK);
                break;
            case GET_ASSAULT_THIEVES_CS:
                int thievesCS = cs.getnAssaultThievesCS();
                outMessage = new Message(Message.ACK, thievesCS);
                break;
        }
        return outMessage;
    }
}
