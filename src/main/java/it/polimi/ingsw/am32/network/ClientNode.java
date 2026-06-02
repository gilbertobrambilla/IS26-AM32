package it.polimi.ingsw.am32.network;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;

/**
 * A generic client node in the network is represented.
 */
public interface ClientNode {

    /**
     * Ping is sent to the client to check liveness.
     */
    void sendPingToClient();

    /**
     * A generic message is sent to the client.
     *
     * @param message the message to be sent
     */
    void sendMessage(MessageDTO message);
}
