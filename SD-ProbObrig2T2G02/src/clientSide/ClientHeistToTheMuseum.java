package clientSide;

import static auxiliary.constants.Constants.*;
import auxiliary.messages.Message;
import genclass.*;
import java.util.HashMap;

public class ClientHeistToTheMuseum {

    /**
     * Programa principal.
     *
     * @param args
     */
    public static void main(String[] args) {
        int nIter;                                           
        String fName;                                    
        String serverHostName = null, userMachineName = null;                        
        HashMap<String, String>[] map;                    

        GenericIO.writelnString("\n" + "      Heist to the Museum\n");
        GenericIO.writeString("Number of iterations?");
        nIter = GenericIO.readlnInt();
        GenericIO.writeString("Name of the logging file?");
        fName = GenericIO.readlnString();
        serverHostName = CONFIG_PC_NAME;
        userMachineName = USER_PC_NAME;

        ClientCom con = new ClientCom(serverHostName, 22226);    
        Message inMessage, outMessage;                       

        while (!con.open()) {
            try {
                Thread.sleep((long) (1000));
            } catch (InterruptedException e) {
            }
        }
        outMessage = new Message(Message.GET_CONFIGS);
        con.writeObject(outMessage);
        inMessage = (Message) con.readObject();
        con.close();
        map = inMessage.getMap();

        MasterThief mthief = new MasterThief(userMachineName, map);
        AssaultThief thiefs[] = new AssaultThief[THIEVES_NUMBER];
        for (int i = 0; i < THIEVES_NUMBER; i++) {
            thiefs[i] = new AssaultThief(i, nIter, userMachineName, 4001 + i, map);
        }

        con = new ClientCom(map[0].get("Logger"), Integer.parseInt(map[1].get("Logger")));
        while (!con.open()) {
            try {
                Thread.sleep((long) (1000));
            } catch (InterruptedException e) {
            }
        }
        outMessage = new Message(Message.SETNFIC, fName, nIter);
        con.writeObject(outMessage);
        inMessage = (Message) con.readObject();
        con.close();
        
        mthief.start();

        for (int i = 0; i < THIEVES_NUMBER; i++) {
            thiefs[i].start();
        }

        GenericIO.writelnString();
        for (int i = 0; i < THIEVES_NUMBER; i++) {
            try {
                thiefs[i].join();
            } catch (InterruptedException e) {
            }
            GenericIO.writelnString("The client " + i + " has finished.");
        }
        GenericIO.writelnString();
        while (mthief.isAlive()) {
            //mthief.sendInterrupt();
            Thread.yield();
        }
        try {
            mthief.join();
        } catch (InterruptedException e) {
        }

        GenericIO.writelnString();
    }
}
