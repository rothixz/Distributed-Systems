package serverSide;

import auxiliary.messages.*;


/**
 *
 * @author António Mota
 * @author Marcos Pires
 */
public abstract class Interface {
    abstract public Message processAndReply(Message inMessage) throws MessageException;
}