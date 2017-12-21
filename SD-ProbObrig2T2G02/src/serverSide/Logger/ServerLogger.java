package serverSide.Logger;

import genclass.*;
import serverSide.ClientProxy;
import serverSide.ServerCom;

/**
 * This data type simulates the Logger server side solution of the Heist to the
 * Museum Problem that implements a type 2 client-server (server replication)
 * with static launch of the threads Assault Thief and Master Thief. The
 * communication is message based using socked on TCP protocol.
 *
 * @author António Mota
 * @author Marcos Pires
 */
public class ServerLogger {

    /**
     * Listening port number for the provided service (22220 by default)
     *
     * @serialField portNumb
     */
    private static final int portNumb = 22220;

    /**
     * Main program.
     *
     * @param args Arguments
     */
    public static void main(String[] args) {
        Logger logger;
        ILogger iLogger;
        ServerCom scon, sconi;
        ClientProxy cliProxy;

        scon = new ServerCom(portNumb);
        scon.start();
        logger = new Logger();
        iLogger = new ILogger(logger);
        GenericIO.writelnString("The Logger service was established!");
        GenericIO.writelnString("The server is listening");

        while (true) {
            sconi = scon.accept();
            cliProxy = new ClientProxy(sconi, iLogger);
            cliProxy.start();
        }
    }
}
