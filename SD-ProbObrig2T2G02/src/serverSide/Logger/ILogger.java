package serverSide.Logger;

import auxiliary.messages.*;
import serverSide.Interface;
import static auxiliary.messages.Message.*;

/**
 * This is the interface class for the Logger data type. It executes the
 * operations on the logger based on the received messages.
 *
 * @author António Mota
 * @author Marcos Pires
 */
public class ILogger extends Interface {

    /**
     * Logger (represents the provided service)
     *
     * @serialField log
     */
    private final Logger log;

    /**
     * Logger interface instantiation
     *
     *
     * @param log Represents the Logger data type
     */
    public ILogger(Logger log) {
        this.log = log;
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
            case SETNFIC:                           
                log.setFileName(inMessage.getFName(), inMessage.getNIter());
                log.reportInitialStatus();
                outMessage = new Message(Message.ACK);
                break;
            case REP_MUSEUM:                             
                log.setMuseum(inMessage.getRooms());
                outMessage = new Message(Message.ACK);
                break;
            case REP_STATUS_AT:                              
                log.setAssaultThief(inMessage.getInteger0(), inMessage.getInteger1(), inMessage.getInteger2(), inMessage.getInteger3(), inMessage.getInteger4());
                log.reportStatus();
                outMessage = new Message(Message.ACK);
                break;
            case REP_STATUS_MT:                                  
                log.setMasterThief(inMessage.getInteger0());
                log.reportStatus();
                outMessage = new Message(Message.ACK);
                break;
            case REP_STATUS_AP:                      
                log.setAssaultParty(inMessage.getInteger0(), inMessage.getPartyThieves(), inMessage.getPartyThievesPos(), inMessage.getInteger1());
                log.reportStatus();
                outMessage = new Message(Message.ACK);
                break;
            case ENDOP:                                   
                log.reportFinalStatus();
                outMessage = new Message(Message.ACK);
                break;
        }                System.out.println("LOGGER - REP_STATUS_MT");


        return (outMessage);
    }
}
