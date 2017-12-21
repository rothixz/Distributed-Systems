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
public interface ControlCollectionSiteInterface extends IMasterThief, Remote {

    public void signalShutdown() throws RemoteException;

    public TimeStamp appraiseSit(int status, int nAssaultThievesCS, TimeStamp ts) throws RemoteException;

    public TimeStamp sendAssaultParty(int status, TimeStamp ts) throws RemoteException;

    public TimeStamp takeARest(int status, TimeStamp ts) throws RemoteException;

    public TimeStamp isReady(TimeStamp ts) throws RemoteException;

    public TimeStamp nextEmptyRoom(TimeStamp ts) throws RemoteException;

    public TimeStamp collectCanvas(int status, TimeStamp ts) throws RemoteException;

    public TimeStamp sumUpResults(int status, TimeStamp ts) throws RemoteException;

    public TimeStamp prepareExcursion(int thiefID, int status, int maxDisp, int partyID, int hasCanvas, TimeStamp ts) throws RemoteException;

    public TimeStamp handCanvas(int thiefID, int status, int maxDisp, int partyID, int hasCanvas, TimeStamp ts) throws RemoteException;

    public TimeStamp gotEndOp(TimeStamp ts) throws RemoteException;

    public TimeStamp getNextParty(TimeStamp ts) throws RemoteException;

    public TimeStamp getNextRoom(TimeStamp ts) throws RemoteException;
}
