package it.polimi.ingsw.am32.network.client;

import it.polimi.ingsw.am32.network.ServerConnection;

/**
 * Interface representing a node connected to the server.
 */
public interface ServerNode extends ServerConnection {

    /**
     * Sends a ping to the server to check liveness.
     */
    void sendPingToServer();

    /**
     * Disconnects the node from the server.
     */
    void disconnect();
}
