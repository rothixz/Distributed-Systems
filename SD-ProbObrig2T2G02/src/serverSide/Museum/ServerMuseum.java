package serverSide.Museum;

import genclass.*;
import serverSide.ClientProxy;
import serverSide.ServerCom;

/**
 * This data type simulates the Museum server side solution of the Heist to the
 * Museum Problem that implements a type 2 client-server (server replication)
 * with static launch of the threads Assault Thief and Master Thief. The
 * communication is message based using socked on TCP protocol.
 *
 * @author António Mota
 * @author Marcos Pires
 */

public class ServerMuseum {

    /**
     * Listening port number for the provided service (22225 by default)
     *
     * @serialField portNumb
     */
    private static final int portNumb = 22225;

    /**
     * Main program.
     *
     * @param args Arguments
     */ 
    public static void main(String[] args) {
        Museum museum;
        IMuseum iMuseum;
        ServerCom scon, sconi;
        ClientProxy cliProxy;

        scon = new ServerCom(portNumb);
        scon.start();
        museum = new Museum();
        iMuseum = new IMuseum(museum);
        GenericIO.writelnString("The Museum service was established!");
        GenericIO.writelnString("The server is listening");

        while (true) {
            sconi = scon.accept();
            cliProxy = new ClientProxy(sconi, iMuseum);
            cliProxy.start();
        }
    }
}
