package it.polimi.ingsw.am32.network.client;

import it.polimi.ingsw.am32.network.ServerConnection;

/**
 * A node connected to the server is represented.
 */
public interface ServerNode extends ServerConnection {

    /**
     * A ping is sent to the server to check liveness.
     */
    void sendPingToServer();

    /**
     * The node is disconnected from the server.
     */
    void disconnect();
}
