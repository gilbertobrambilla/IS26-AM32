package it.polimi.ingsw.am32.network.client;

import it.polimi.ingsw.am32.network.ServerConnection;

public interface ServerNode extends ServerConnection {
    void ping();
    void disconnect();
}