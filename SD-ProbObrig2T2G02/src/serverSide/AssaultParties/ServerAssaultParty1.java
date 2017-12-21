package serverSide.AssaultParties;

import genclass.*;
import serverSide.ClientProxy;
import serverSide.ServerCom;

/**
 * This data type simulates the Assault Party 1 server side solution of the
 * Heist to the Museum Problem that implements a type 2 client-server (server
 * replication) with static launch of the threads Assault Thief and Master
 * Thief. The communication is message based using socked on TCP protocol.
 *
 * @author António Mota
 * @author Marcos Pires
 */
public class ServerAssaultParty1 {

    /**
     * Listening port number for the provided service (22224 by default)
     *
     * @serialField portNumb
     */
    private static final int portNumb = 22224;

    /**
     * Main program.
     *
     * @param args Arguments
     */
    public static void main(String[] args) {
        AssaultParty assaultParty1;
        IAssaultParty iAssaultParty1;
        ServerCom scon1, sconi1;
        ClientProxy assaultPartyProxy1;

        scon1 = new ServerCom(portNumb);
        scon1.start();

        assaultParty1 = new AssaultParty(1);

        iAssaultParty1 = new IAssaultParty(assaultParty1);

        GenericIO.writelnString("The Assault Party 1 service was established!");
        GenericIO.writelnString("The server is listening");

        while (true) {
            sconi1 = scon1.accept();
            assaultPartyProxy1 = new ClientProxy(sconi1, iAssaultParty1);
            assaultPartyProxy1.start();
        }
    }
}
