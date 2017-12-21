package serverSide;

import auxiliary.messages.*;
import genclass.GenericIO;

/**
 * This is the generic Client Proxy for the interfaces.
 *
 * @author António Mota
 * @author Marcos Pires
 */
public class ClientProxy extends Thread {

    private static int nProxy;

    private ServerCom sconi;
    private Interface inter;

    /**
     *
     * @param sconi
     * @param inter
     */
    public ClientProxy(ServerCom sconi, Interface inter) {
        super("Proxy_" + getProxyId());

        this.sconi = sconi;
        this.inter = inter;
    }

    @Override
    public void run() {
        Message inMessage = null, 
                outMessage = null;                      

        inMessage = (Message) sconi.readObject();                     
        try {
            outMessage = inter.processAndReply(inMessage);       
        } catch (MessageException e) {

            GenericIO.writelnString("Thread " + getName() + ": " + e.getMessage() + "!");
            GenericIO.writelnString(e.getMessageVal().toString());
            System.exit(1);
        }
        sconi.writeObject(outMessage);           
    /**
     *
     */                    
        sconi.close();                                              
    }

    private static int getProxyId() {
        Class<serverSide.ClientProxy> cl = null;             
        int proxyId;                                        

        try {
            cl = (Class<serverSide.ClientProxy>) Class.forName("serverSide.ClientProxy");
        } catch (ClassNotFoundException e) {
            GenericIO.writelnString("Interface data type was nto found!");
            e.printStackTrace();
            System.exit(1);
        }

        synchronized (cl) {
            proxyId = nProxy;
            nProxy += 1;
        }

        return proxyId;
    }
}