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
public interface MuseumInterface extends IMasterThief, Remote {

    public void signalShutdown() throws RemoteException;

    public TimeStamp rollACanvas(TimeStamp ts, int thiefID, int status, int maxDisp, int partyID, int hasCanvas, int nRoom) throws RemoteException;

    public TimeStamp getRoom(TimeStamp ts, int roomID) throws RemoteException;
}
