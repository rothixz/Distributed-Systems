package serverSide.AssaultParties;

import genclass.*;
import serverSide.ClientProxy;
import serverSide.ServerCom;

/**
 * This data type simulates the Assault Party 0 server side solution
 * of the Heist to the Museum Problem that implements a type 2 client-server
 * (server replication) with static launch of the threads Assault Thief and
 * Master Thief. The communication is message based using socked on TCP
 * protocol.
 *
 * @author António Mota
 * @author Marcos Pires
 */
public class ServerAssaultParty0 {

    /**
     * Listening port number for the provided service (22223 by default)
     *
     * @serialField portNumb
     */
    private static final int portNumb = 22223;

    /**
     * Main program.
     *
     * @param args Arguments
     */
    public static void main(String[] args) {
        AssaultParty assaultParty0;
        IAssaultParty iAssaultParty0;
        ServerCom scon0, sconi0;
        ClientProxy assaultPartyProxy0;

        scon0 = new ServerCom(portNumb);
        scon0.start();

        assaultParty0 = new AssaultParty(0);

        iAssaultParty0 = new IAssaultParty(assaultParty0);

        GenericIO.writelnString("The Assault Party 0 service was established!");
        GenericIO.writelnString("The server is listening");

        while (true) {
            sconi0 = scon0.accept();
            assaultPartyProxy0 = new ClientProxy(sconi0, iAssaultParty0);
            assaultPartyProxy0.start();
        }
    }
}
