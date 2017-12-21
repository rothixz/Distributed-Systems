package serverSide.ConcentrationSite;

import genclass.*;
import serverSide.ClientProxy;
import serverSide.ServerCom;

/**
 * This data type simulates the Concentration Site server side solution of the
 * Heist to the Museum Problem that implements a type 2 client-server (server
 * replication) with static launch of the threads Assault Thief and Master
 * Thief. The communication is message based using socked on TCP protocol.
 *
 * @author António Mota
 * @author Marcos Pires
 */
public class ServerConcentrationSite {

    /**
     * Listening port number for the provided service (22221 by default)
     *
     * @serialField portNumb
     */
    private static final int portNumb = 22221;

    /**
     * Main program.
     *
     * @param args Arguments
     */
    public static void main(String[] args) {
        ConcentrationSite CS;
        IConcentrationSite iCS;
        ServerCom scon, sconi;
        ClientProxy cliProxy;

        scon = new ServerCom(portNumb);
        scon.start();
        CS = new ConcentrationSite();
        iCS = new IConcentrationSite(CS);
        GenericIO.writelnString("The Concentration Site service was established!");
        GenericIO.writelnString("The server is listening");
        /* processamento de pedidos */
        while (true) {
            sconi = scon.accept();
            cliProxy = new ClientProxy(sconi, iCS);
            cliProxy.start();
        }
    }
}
