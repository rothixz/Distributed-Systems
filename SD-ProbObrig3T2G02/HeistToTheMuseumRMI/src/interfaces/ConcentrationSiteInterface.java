/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfaces;

import auxiliary.time.TimeStamp;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author mota
 */
public interface ConcentrationSiteInterface extends IMasterThief, Remote {

    public void signalShutdown() throws RemoteException;

    public TimeStamp prepareAssaultParty(int status, TimeStamp ts) throws RemoteException;

    public TimeStamp getnAssaultThievesCS(TimeStamp ts) throws RemoteException;

    public TimeStamp amINeeded(int thiefID, int status, int maxDisp, int partyID, int hasCanvas, TimeStamp ts) throws RemoteException;

    public TimeStamp startOfOperations(TimeStamp ts, int status) throws RemoteException;
}
