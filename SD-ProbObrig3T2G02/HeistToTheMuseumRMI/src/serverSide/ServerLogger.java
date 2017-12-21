package serverSide;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import genclass.GenericIO;
import interfaces.LoggerInterface;
import interfaces.Register;

/**
 *  This data type instantiates and registers a remote object that will run mobile code.
 *  Communication is based in Java RMI.
 */

public class ServerLogger
{
  /**
   *  Main task.
   */

   public static void main(String[] args)
   {
    /* get location of the registry service */

     String rmiRegHostName;
     int rmiRegPortNumb;

     GenericIO.writeString ("Nome do nó de processamento onde está localizado o serviço de registo? ");
     rmiRegHostName = "localhost";
     GenericIO.writeString ("Número do port de escuta do serviço de registo? ");
     rmiRegPortNumb = 22350;

    /* create and install the security manager */

     if (System.getSecurityManager () == null)
        System.setSecurityManager (new SecurityManager ());
     GenericIO.writelnString ("Security manager was installed!");

    /* instantiate a remote object that runs mobile code and generate a stub for it */

     Logger log = new Logger ();
     LoggerInterface logstub = null;
     int listeningPort = 22002;                            /* it should be set accordingly in each case */

     try
     { logstub = (LoggerInterface) UnicastRemoteObject.exportObject (log, listeningPort);
     }
     catch (RemoteException e)
     { GenericIO.writelnString ("Logger stub generation exception: " + e.getMessage ());
       e.printStackTrace ();
       System.exit (1);
     }
     GenericIO.writelnString ("Logger was generated!");

    /* register it with the general registry service */

     String nameEntryBase = "RegisterHandler";
     String nameEntryObject = "LoggerInt";
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
     { reg.bind (nameEntryObject, logstub);
     }
     catch (RemoteException e)
     { GenericIO.writelnString ("Logger registration exception: " + e.getMessage ());
       e.printStackTrace ();
       System.exit (1);
     }
     catch (AlreadyBoundException e)
     { GenericIO.writelnString ("Logger already bound exception: " + e.getMessage ());
       e.printStackTrace ();
       System.exit (1);
     }
     GenericIO.writelnString ("Logger object was registered!");
 }
}