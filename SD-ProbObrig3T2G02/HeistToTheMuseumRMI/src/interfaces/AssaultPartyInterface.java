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
public interface AssaultPartyInterface extends IMasterThief, Remote {

    public void signalShutdown() throws RemoteException;

    public TimeStamp crawlIn(TimeStamp ts, int thiefID, int status, int maxDisp, int partyID, int hasCanvas) throws RemoteException;

    public TimeStamp reverseDirection(TimeStamp ts, int thiefID, int status, int maxDisp, int partyID, int hasCanvas) throws RemoteException;

    public TimeStamp crawlOut(TimeStamp ts, int thiefID, int status, int maxDisp, int partyID, int hasCanvas) throws RemoteException;

    public TimeStamp getRoomID(TimeStamp ts) throws RemoteException;

    public TimeStamp getPartyThieves(TimeStamp ts) throws RemoteException;

    public TimeStamp addThief(TimeStamp ts, int thiefID, int maxDisp) throws RemoteException;

    public TimeStamp setFirst(TimeStamp ts) throws RemoteException;

    public TimeStamp setRoom(TimeStamp ts, int roomID) throws RemoteException;

    public TimeStamp isEmptyAP(TimeStamp ts) throws RemoteException;

    public TimeStamp setPartyThieves(TimeStamp ts, int i, int value) throws RemoteException;

}
