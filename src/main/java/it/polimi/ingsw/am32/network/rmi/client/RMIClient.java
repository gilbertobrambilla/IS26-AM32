package it.polimi.ingsw.am32.network.rmi.client;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface for the rmi client.
 */
public interface RMIClient extends Remote {

    /**
     * Checks the liveness of the client.
     *
     * @throws RemoteException If a network error occurs.
     */
    void ping() throws RemoteException;

    /**
     * Receives a generic message from the server.
     *
     * @param message The message received.
     * @throws RemoteException If a network error occurs.
     */
    void receiveMessage(MessageDTO message) throws RemoteException;
}
