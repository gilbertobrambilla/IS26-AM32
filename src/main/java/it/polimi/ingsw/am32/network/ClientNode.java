package it.polimi.ingsw.am32.network;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;

/**
 * Interface representing a generic client node in the network.
 */
public interface ClientNode {

    /**
     * Checks the liveness of the client.
     */
    void ping();

    /**
     * Sends a generic message to the client.
     *
     * @param message The message to be sent.
     */
    void sendMessage(MessageDTO message);
}
