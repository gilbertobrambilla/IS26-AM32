package it.polimi.ingsw.am32.network.server;

/**
 * Interface used to represent the network lifecycle management of a client connection.
 */
public interface NetworkNode {

    /**
     * Disconnection of the client and cleanup of network resources handled.
     */
    void handleDisconnect();
}
