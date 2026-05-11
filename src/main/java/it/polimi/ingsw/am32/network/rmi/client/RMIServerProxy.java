package it.polimi.ingsw.am32.network.rmi.client;

import it.polimi.ingsw.am32.controller.commands.game.GameCommand;
import it.polimi.ingsw.am32.controller.commands.lobby.LobbyCommand;
import it.polimi.ingsw.am32.dto.messages.lobby.events.ConnectionResultEvent;
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
     * @param serverStub The rmi server stub.
     * @param clientStub The rmi client stub.
     */
    public RMIServerProxy(RMIServer serverStub, RMIClient clientStub) {
        this.serverStub = serverStub;
        this.clientStub = clientStub;
    }

    @Override
    public void ping() {
        try {
            // ping is sent through the stub
            serverStub.ping(connectionID);
        } catch (RemoteException e) {
            // exception is thrown
            throw new NetworkException("RMI server unreachable during ping", e);
        }
    }

    @Override
    public void connect(String nickname) {
        try {
            // connection is requested
            ConnectionResultEvent result = serverStub.connect(nickname, clientStub);

            if (result.isSuccess()) {
                // connection id is stored
                this.connectionID = result.getConnectionID();
            }

            // result is forwarded to virtual server
            clientStub.receiveMessage(result);

        } catch (RemoteException e) {
            // exception is thrown
            throw new NetworkException("RMI error while connecting", e);
        }
    }

    @Override
    public void sendLobbyCommand(LobbyCommand command) {
        try {
            // lobby command is sent through the stub
            serverStub.sendLobbyCommand(command, connectionID);
        } catch (RemoteException e) {
            // exception is thrown
            throw new NetworkException("RMI error sending LobbyCommand", e);
        }
    }

    @Override
    public void sendGameCommand(GameCommand command) {
        try {
            // game command is sent through the stub
            serverStub.sendGameCommand(command, connectionID);
        } catch (RemoteException e) {
            // exception is thrown
            throw new NetworkException("RMI error sending GameCommand", e);
        }
    }

    @Override
    public void disconnect() {
        try {
            // client object is unexported
            UnicastRemoteObject.unexportObject(clientStub, true);
        } catch (NoSuchObjectException e) {
            LogManager.getInstance().log(LogLevel.WARNING, "[NETWORK] RMI unexport failed: the client stub was already unexported or not found.");
        }
    }
}
