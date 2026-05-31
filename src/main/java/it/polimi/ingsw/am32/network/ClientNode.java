package it.polimi.ingsw.am32.network;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;

/**
 * Interface representing a generic client node in the network.
 */
public interface ClientNode {

    /**
     * Ping is sent to the client to check liveness.
     */
    void sendPingToClient();

    /**
     * Sends a generic message to the client.
     *
     * @param message The message to be sent.
     */
    void sendMessage(MessageDTO message);
}
