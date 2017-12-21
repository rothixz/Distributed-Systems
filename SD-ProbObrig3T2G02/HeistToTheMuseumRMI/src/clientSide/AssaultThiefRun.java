package clientSide;

import static auxiliary.constants.Constants.*;
import genclass.GenericIO;
import interfaces.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class AssaultThiefRun {

    /**
     * Main Program
     *
     * @param args No arguments needed
     */
    public static void main(String[] args) {
        /* get location of the generic registry service */
        String rmiRegHostName;
        int rmiRegPortNumb;

        GenericIO.writeString("Nome do nó de processamento onde está localizado o serviço de registo? ");
        rmiRegHostName = "localhost";
        GenericIO.writeString("Número do port de escuta do serviço de registo? ");
        rmiRegPortNumb = 22350;

        LoggerInterface li = null;
        MuseumInterface mi = null;
        ConcentrationSiteInterface csi = null;
        ControlCollectionSiteInterface ccsi = null;
        AssaultPartyInterface[] api = new AssaultPartyInterface[MAX_ASSAULT_PARTIES];

        String loggerEntryBase = "LoggerInt";
        String museumEntryBase = "MuseumInt";
        String csEntryBase = "ConcentrationSiteInt";
        String ccsEntryBase = "ControlCollectionSiteInt";
        String ass0EntryBase = "AssaultParty0Int";
        String ass1EntryBase = "AssaultParty1Int";

        try {
            Registry registry = LocateRegistry.getRegistry(rmiRegHostName, rmiRegPortNumb);
            li = (LoggerInterface) registry.lookup(loggerEntryBase);
        } catch (RemoteException e) {
            System.out.println("Exception thrown while locating logger: " + e.getMessage() + "!");
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("Logger is not registered: " + e.getMessage() + "!");
            System.exit(1);
        }

        try {
            Registry registry = LocateRegistry.getRegistry(rmiRegHostName, rmiRegPortNumb);
            mi = (MuseumInterface) registry.lookup(museumEntryBase);
        } catch (RemoteException e) {
            System.out.println("Exception thrown while locating museum: " + e.getMessage() + "!");
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("Museum is not registered: " + e.getMessage() + "!");
            System.exit(1);
        }

        try {
            Registry registry = LocateRegistry.getRegistry(rmiRegHostName, rmiRegPortNumb);
            csi = (ConcentrationSiteInterface) registry.lookup(csEntryBase);
        } catch (RemoteException e) {
            System.out.println("Exception thrown while locating concentration site: " + e.getMessage() + "!");
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("ConcentratioSite is not registered: " + e.getMessage() + "!");
            System.exit(1);
        }

        try {
            Registry registry = LocateRegistry.getRegistry(rmiRegHostName, rmiRegPortNumb);
            ccsi = (ControlCollectionSiteInterface) registry.lookup(ccsEntryBase);
        } catch (RemoteException e) {
            System.out.println("Exception thrown while locating concentration site: " + e.getMessage() + "!");
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("ControlCollectionSite is not registered: " + e.getMessage() + "!");
            System.exit(1);
        }

        try {
            Registry registry = LocateRegistry.getRegistry(rmiRegHostName, rmiRegPortNumb);
            api[0] = (AssaultPartyInterface) registry.lookup(ass0EntryBase);
        } catch (RemoteException e) {
            System.out.println("Exception thrown while locating assault party 0: " + e.getMessage() + "!");
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("AssaultParty0 is not registered: " + e.getMessage() + "!");
            System.exit(1);
        }

        try {
            Registry registry = LocateRegistry.getRegistry(rmiRegHostName, rmiRegPortNumb);
            api[1] = (AssaultPartyInterface) registry.lookup(ass1EntryBase);
        } catch (RemoteException e) {
            System.out.println("Exception thrown while locating assault party 1: " + e.getMessage() + "!");
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("AssaultParty1 is not registered: " + e.getMessage() + "!");
            System.exit(1);
        }

        AssaultThief thiefs[] = new AssaultThief[THIEVES_NUMBER];
        for (int i = 0; i < THIEVES_NUMBER; i++) {
            thiefs[i] = new AssaultThief(i, li, mi, csi, ccsi, api);
        }

        for (int i = 0; i < THIEVES_NUMBER; i++) {
            thiefs[i].start();
        }

        GenericIO.writelnString();
        for (int i = 0; i < THIEVES_NUMBER; i++) {
            while (thiefs[i].isAlive()) {
                Thread.yield();
            }
            try {
                thiefs[i].join();
            } catch (InterruptedException e) {
            }
        }
        
        System.out.println("Done Thief!");
    }

}
