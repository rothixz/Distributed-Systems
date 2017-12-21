package serverSide.ControlCollectionSite;

import genclass.*;
import serverSide.ClientProxy;
import serverSide.ServerCom;

/**
 * This data type simulates the Control and Collection Site server side solution
 * of the Heist to the Museum Problem that implements a type 2 client-server
 * (server replication) with static launch of the threads Assault Thief and
 * Master Thief. The communication is message based using socked on TCP
 * protocol.
 *
 * @author António Mota
 * @author Marcos Pires
 */
public class ServerControlCollection {

    /**
     * Listening port number for the provided service (22222 by default)
     *
     * @serialField portNumb
     */
    private static final int portNumb = 22222;

    /**
     * Main program.
     *
     * @param args Arguments
     */
    public static void main(String[] args) {
        ControlCollectionSite CCS;                                   
        IControlCollectionSite iCCS;                  
        ServerCom scon, sconi;                              
        ClientProxy cliProxy;                              

      
        scon = new ServerCom(portNumb);                 
        scon.start();                                      
        CCS = new ControlCollectionSite();                 
        iCCS = new IControlCollectionSite(CCS); 
        GenericIO.writelnString("The Control and Collection Site service was established!");
        GenericIO.writelnString("The server is listening");


        while (true) {
            sconi = scon.accept();                          
            cliProxy = new ClientProxy(sconi, iCCS);
            cliProxy.start();
        }
    }
}
