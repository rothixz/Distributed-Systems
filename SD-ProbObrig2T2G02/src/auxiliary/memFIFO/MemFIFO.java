package auxiliary.memFIFO;

/**
 *
 * @author 
 */

public class MemFIFO extends MemObject
{
  /**
   *  Insertion Point
   *
   *    @serialField inPnt
   */

   private int inPnt = 0;

  /**
   *  Retrieving Point
   *
   *    @serialField outPnt
   */

   private int outPnt = 0;

  /**
   *  Signals FIFO is empty
   *
   *    @serialField empty
   */

   private boolean empty = true;

  /**
   *  insertion of FIFO.
   *
   *    @param nElem size of FIFO (n. of elements in array)
   */

   public MemFIFO (int nElem)
   {
     super (nElem);
   }

  /**
   *  Store value
   *
   *    @param val value to store
   */

    @Override
   public void write (Object val)
   {
     if ((inPnt != outPnt) || empty)
        { mem[inPnt] = val;
          inPnt += 1;
          inPnt %= nMax;
          empty = false;
        }
   }

  /**
   *  Read Value
   *
   *    @return Read value
   */

    @Override
   public Object read ()
   {
     Object val = null;

     if ((outPnt != inPnt) || !empty)
        { val = mem[outPnt];
          outPnt += 1;
          outPnt %= nMax;
          empty = (inPnt == outPnt);
        }
     return (val);
   }

  /**
   *  Empty FIFO detection
   *
   *    @return <li> true, if FIFO is empty</li>
   *            <li> false, if otherwise</li>
   */

   public boolean empty ()
   {
     return (this.empty);
   }

  /**
   *  Detect if FIFO is full
   *
   *    @return <li> true, if is full
   *            <li> false, if otherwise
   */

   public boolean full ()
   {
     return (!this.empty && (outPnt == inPnt));
   }
}
