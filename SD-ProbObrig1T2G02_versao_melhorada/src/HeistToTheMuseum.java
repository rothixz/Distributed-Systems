import threads.*;
import monitors.*;
import java.rmi.RemoteException;
import genclass.*;

/**
 *
 * @author António Mota
 * @author Marcos Pires
 */
public class HeistToTheMuseum {

    /**
     * @param args the command line arguments
     * @throws java.rmi.RemoteException
     */
    public static void main(String[] args) throws RemoteException {
        int x = 0;

        //while (x < 1000) {
            x++;
            System.out.println("##############################" + x + "#############################");
            // Inicialização
            Logger log = new Logger();
            Museum museum = new Museum(log);
            AssaultParty[] assparties = new AssaultParty[2];

            for (int i = 0; i < 2; i++) {
                assparties[i] = new AssaultParty(i, museum, log);
            }

            ControlCollectionSite ccs = new ControlCollectionSite(assparties, log);
            ConcentrationSite cs = new ConcentrationSite(ccs, assparties, log);

            MasterThief masterThief = new MasterThief(ccs, cs, museum);
            AssaultThief thiefs[] = new AssaultThief[6];

            log.startLog();
            log.reportInitialStatus();

            masterThief.start();

            for (int i = 0; i < 6; i++) {
                thiefs[i] = new AssaultThief(i, 0, cs, ccs, assparties, museum);
                thiefs[i].start();
            }

            /* aguardar o fim da simulação */
            GenericIO.writelnString();
            try {
                masterThief.join();
            } catch (InterruptedException ex) {
                System.out.println("MasterThief has finished!");
            }

            GenericIO.writelnString();
            for (int i = 0; i < 6; i++) {
                try {
                    thiefs[i].join();
                } catch (InterruptedException e) {
                }
                GenericIO.writelnString("Thief " + i + " has finished.");
            }
            
            log.reportFinalStatus();

            GenericIO.writelnString();
        //}
    }
}
