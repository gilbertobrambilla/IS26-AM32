package it.polimi.ingsw.am32.network.server;

/**
 * Network lifecycle management of a client connection is represented.
 */
public interface NetworkNode {

    /**
     * Disconnection of the client and cleanup of network resources handled.
     */
    void handleDisconnect();
}
