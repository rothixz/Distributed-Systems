package serverSide.Museum;

import auxiliary.messages.*;
import serverSide.Interface;
import static auxiliary.messages.Message.*;

/**
 * This is the interface class for the Museum data type. It executes the
 * operations on the museum based on the received messages.
 *
 * @author António Mota
 * @author Marcos Pires
 */
public class IMuseum extends Interface {

    /**
     * Museum (represents the provided service)
     *
     * @serialField museum
     */
    private final Museum museum;

    /**
     * Museum interface instantiation
     *
     *
     * @param museum Represents the Museum data type
     */
    public IMuseum(Museum museum) {
        this.museum = museum;
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
            case GET_DIST_OUTSIDE:
                int distOutside = museum.getRoom(inMessage.getInteger0()).getDistOutside();
                outMessage = new Message(Message.ACK, distOutside);
                break;
            case ROLL_A_CANVAS:
                int hasCanvas = museum.rollACanvas(inMessage.getInteger0(), inMessage.getInteger1(), inMessage.getInteger2(), inMessage.getInteger3(), inMessage.getInteger4(), inMessage.getInteger5());
                outMessage = new Message(Message.ACK, hasCanvas);
                break;
        }

        return outMessage;
    }
}
