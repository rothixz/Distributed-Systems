package serverSide;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import genclass.GenericIO;
import interfaces.ConcentrationSiteInterface;
import interfaces.Register;

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
   *  Main task.
   */

   public static void main(String[] args)
   {
    /* get location of the registry service */

     String rmiRegHostName;
     int rmiRegPortNumb;

     GenericIO.writeString ("Nome do nó de FODASASDASD ASD ASD ASD ASD ASviço de registo? ");
     rmiRegHostName = "localhost";
     GenericIO.writeString ("Número do port de escuta do serviço de registo? ");
     rmiRegPortNumb = 22350;

    /* create and install the security manager */

     if (System.getSecurityManager () == null)
        System.setSecurityManager (new SecurityManager ());
     GenericIO.writelnString ("Security manager was installed!");

    /* instantiate a remote object that runs mobile code and generate a stub for it */
     System.out.println("antes");
     ConcentrationSite cs = new ConcentrationSite ();
     System.out.println("depois");
     ConcentrationSiteInterface csstub = null;
     int listeningPort = 22003;                            /* it should be set accordingly in each case */

     try
     { csstub = (ConcentrationSiteInterface) UnicastRemoteObject.exportObject (cs, listeningPort);
     }
     catch (RemoteException e)
     { GenericIO.writelnString ("ConcentrationSite stub generation exception: " + e.getMessage ());
       e.printStackTrace ();
       System.exit (1);
     }
     GenericIO.writelnString ("ConcentrationSite was generated!");

    /* register it with the general registry service */

     String nameEntryBase = "RegisterHandler";
     String nameEntryObject = "ConcentrationSiteInt";
     Registry registry = null;
     Register reg = null;

     try
     { registry = LocateRegistry.getRegistry (rmiRegHostName, rmiRegPortNumb);
     }
     catch (RemoteException e)
     { GenericIO.writelnString ("RMI registry creation exception: " + e.getMessage ());
       e.printStackTrace ();
       System.exit (1);
     }
     GenericIO.writelnString ("RMI registry was created!");

     try
     { reg = (Register) registry.lookup (nameEntryBase);
     }
     catch (RemoteException e)
     { GenericIO.writelnString ("RegisterRemoteObject lookup exception: " + e.getMessage ());
       e.printStackTrace ();
       System.exit (1);
     }
     catch (NotBoundException e)
     { GenericIO.writelnString ("RegisterRemoteObject not bound exception: " + e.getMessage ());
       e.printStackTrace ();
       System.exit (1);
     }

     try
     { reg.bind (nameEntryObject, csstub);
     }
     catch (RemoteException e)
     { GenericIO.writelnString ("ConcentrationSite registration exception: " + e.getMessage ());
       e.printStackTrace ();
       System.exit (1);
     }
     catch (AlreadyBoundException e)
     { GenericIO.writelnString ("ConcentrationSite already bound exception: " + e.getMessage ());
       e.printStackTrace ();
       System.exit (1);
     }
     GenericIO.writelnString ("ConcentrationSite object was registered!");
 }
}