package it.polimi.ingsw.am32.network.rmi.client;

import it.polimi.ingsw.am32.controller.commands.game.GameCommand;
import it.polimi.ingsw.am32.controller.commands.lobby.LobbyCommand;
import it.polimi.ingsw.am32.log.LogLevel;
import it.polimi.ingsw.am32.log.LogManager;
import it.polimi.ingsw.am32.network.client.ServerNode;
import it.polimi.ingsw.am32.network.exceptions.NetworkException;
import it.polimi.ingsw.am32.network.rmi.server.RMIServer;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Proxy representing the remote server connected via rmi.
 */
public class RMIServerProxy implements ServerNode {

    private final RMIServer serverStub;
    private final RMIClient clientStub;
    private String connectionID;

    /**
     * Constructs a new rmi server proxy.
     *
     * @param serverStub the rmi server stub
     * @param clientStub the rmi client stub
     */
    public RMIServerProxy(RMIServer serverStub, RMIClient clientStub) {
        this.serverStub = serverStub;
        this.clientStub = clientStub;
    }

    @Override
    public void connect() {
        try {
            // connection requested asynchronously
            serverStub.connect(clientStub);
        } catch (RemoteException e) {
            // exception thrown
            throw new NetworkException("rmi error while connecting", e);
        }
    }

    @Override
    public void login(String nickname) {
        try {
            // login requested
            serverStub.login(nickname, connectionID);
        } catch (RemoteException e) {
            // exception thrown
            throw new NetworkException("rmi error while login", e);
        }
    }

    @Override
    public void sendLobbyCommand(LobbyCommand command) {
        if (this.connectionID == null) {
            // exception thrown
            throw new NetworkException("Cannot send commands before connection is established and acknowledged.");
        }

        try {
            // lobby command sent through the stub
            serverStub.sendLobbyCommand(command, connectionID);
        } catch (RemoteException e) {
            // exception thrown
            throw new NetworkException("RMI error sending LobbyCommand", e);
        }
    }

    @Override
    public void sendGameCommand(GameCommand command) {
        try {
            // game command sent through the stub
            serverStub.sendGameCommand(command, connectionID);
        } catch (RemoteException e) {
            // exception thrown
            throw new NetworkException("RMI error sending GameCommand", e);
        }
    }

    @Override
    public void sendPingToServer() {
        try {
            // ping reception triggered through the stub
            serverStub.onPingReceivedFromClient(connectionID);
        } catch (RemoteException e) {
            // exception thrown
            throw new NetworkException("rmi server unreachable during ping", e);
        }
    }

    @Override
    public void disconnect() {
        try {
            // client object unexported
            UnicastRemoteObject.unexportObject(clientStub, true);
        } catch (NoSuchObjectException e) {
            // warning logged
            LogManager.getInstance().log(LogLevel.WARNING, "[NETWORK] RMI unexport failed: the client stub was already unexported or not found.");
        }
    }

    /**
     * Sets the connection identifier.
     *
     * @param connectionID the unique connection identifier
     */
    public void setConnectionID(String connectionID) {
        // connection id stored
        this.connectionID = connectionID;
    }
}
