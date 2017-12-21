package auxiliary.messages;

/**
 *   
 *   This type of data defines a thrown exception if a message is invalid
 */

public class MessageException extends Exception
{
  /**
   *    Message that originated the exception
   *     
   */

   private Message msg;

  /**
   *  Message instantiation 
   *
   *    @param errorMessage error description message (text)
   *    @param msg Message that originated the exception
   */

   public MessageException (String errorMessage, Message msg)
   {
     super (errorMessage);
     this.msg = msg;
   }

  /**
   *  
   *
   *    @return Returns message that originated exception
   */

   public Message getMessageVal ()
   {
     return (msg);
   }
}
