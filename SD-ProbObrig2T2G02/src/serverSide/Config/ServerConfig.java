package serverSide.Config;

import genclass.GenericIO;
import java.io.IOException;
import serverSide.ClientProxy;
import serverSide.ServerCom;

/**
 * This data type simulates the Configuration server side solution of the Heist
 * to the Museum Problem that implements a type 2 client-server (server
 * replication) with static launch of the threads Assault Thief and Master
 * Thief. The communication is message based using socked on TCP protocol.
 *
 * @author António Mota
 * @author Marcos Pires
 */
public class ServerConfig {

    /**
     * Listening port number for the provided service (22226 by default)
     *
     * @serialField portNumb
     */
    private static final int portNumb = 22226;

    /**
     *
     * @param args Arguments
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        Config cfg;
        IConfig ICfg;
        ClientProxy cliProxy;
        ServerCom scon, sconi;

        scon = new ServerCom(portNumb);
        scon.start();

        cfg = new Config();
        ICfg = new IConfig(cfg);

        GenericIO.writelnString("The Configuration service was established!");
        GenericIO.writelnString("The server is listening");

        while (true) {
            sconi = scon.accept();
            cliProxy = new ClientProxy(sconi, ICfg);
            cliProxy.start();
        }
    }
}
