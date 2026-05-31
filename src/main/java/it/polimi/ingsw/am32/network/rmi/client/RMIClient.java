package it.polimi.ingsw.am32.network.rmi.client;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface for the rmi client.
 */
public interface RMIClient extends Remote {

    /**
     * Processes a ping received from the server.
     *
     * @throws RemoteException if a network error occurs
     */
    void onPingReceivedFromServer() throws RemoteException;

    /**
     * Receives a generic message from the server.
     *
     * @param message the message received
     * @throws RemoteException if a network error occurs
     */
    void receiveMessage(MessageDTO message) throws RemoteException;
}
