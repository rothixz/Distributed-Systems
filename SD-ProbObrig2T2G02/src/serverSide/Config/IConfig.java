package serverSide.Config;

import auxiliary.messages.*;
import static auxiliary.messages.Message.*;
import java.util.HashMap;
import serverSide.Interface;

/**
 * This is the interface class for the Config data type. It executes the
 * operations on the config based on the received messages.
 *
 * @author António Mota
 * @author Marcos Pires
 */
public class IConfig extends Interface {

    /**
     * Config (represents the provided service)
     *
     * @serialField cfg
     */
    private final Config cfg;

    /**
     * Config interface instantiation
     *
     *
     * @param cfg Represents the Config data type
     */
    public IConfig(Config cfg) {
        this.cfg = cfg;
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
            case GET_CONFIGS:
                HashMap<String, String>[] map = cfg.getMap();
                outMessage = new Message(Message.ACK, map);
                break;
        }
        return outMessage;
    }
}

