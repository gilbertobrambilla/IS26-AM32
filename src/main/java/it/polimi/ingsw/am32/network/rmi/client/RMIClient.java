package it.polimi.ingsw.am32.network.rmi.client;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * A remote interface for the rmi client is represented.
 */
public interface RMIClient extends Remote {

    /**
     * A ping received from the server is processed.
     *
     * @throws RemoteException if a network error occurs
     */
    void onPingReceivedFromServer() throws RemoteException;

    /**
     * A generic message from the server is received.
     *
     * @param message the message received
     * @throws RemoteException if a network error occurs
     */
    void receiveMessage(MessageDTO message) throws RemoteException;
}
