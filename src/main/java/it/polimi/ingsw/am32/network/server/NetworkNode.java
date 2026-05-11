package it.polimi.ingsw.am32.network.server;

/**
 * Interface representing the network lifecycle management of a client connection.
 */
public interface NetworkNode {

    /**
     * Handles the disconnection of the client and the cleanup of network resources.
     */
    void handleDisconnect();
}
