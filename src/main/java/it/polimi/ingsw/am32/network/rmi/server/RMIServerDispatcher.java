package it.polimi.ingsw.am32.network.rmi.server;

import it.polimi.ingsw.am32.controller.LobbyController;
import it.polimi.ingsw.am32.dto.messages.lobby.events.ConnectionResultEvent;
import it.polimi.ingsw.am32.network.rmi.client.RMIClient;
import it.polimi.ingsw.am32.network.server.RemoteClientView;
import it.polimi.ingsw.am32.network.ClientNode;
import it.polimi.ingsw.am32.controller.commands.game.GameCommand;
import it.polimi.ingsw.am32.controller.commands.lobby.LobbyCommand;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the rmi server interface.
 */
public class RMIServerDispatcher extends UnicastRemoteObject implements RMIServer {
    private final LobbyController lobbyController;
    private final ConcurrentHashMap<String, RemoteClientView> activeNodes;

    /**
     * Constructs a new rmi server implementation.
     *
     * @param lobbyController The controller handling the lobby logic.
     * @throws RemoteException If the remote object cannot be exported.
     */
    public RMIServerDispatcher(LobbyController lobbyController) throws RemoteException {
        super();
        this.lobbyController = lobbyController;
        // active nodes map is instantiated
        this.activeNodes = new ConcurrentHashMap<>();
    }

    @Override
    public void ping(String connectionID) throws RemoteException {
        if (connectionID == null) {
            // anonymous ping is ignored
            return;
        }

        // node is retrieved from active nodes
        RemoteClientView node = activeNodes.get(connectionID);
        if (node != null) {
            // ping received is notified
            node.notifyPingReceived();
        }
    }

    @Override
    public ConnectionResultEvent connect(String nickname, RMIClient clientStub) throws RemoteException {
        // client node is instantiated
        ClientNode clientNode = new RMIClientProxy(clientStub);

        // connection id is generated
        String connectionID = UUID.randomUUID().toString();

        try {
            // view implementation is instantiated
            RemoteClientView view = RemoteClientView.create(nickname, clientNode, connectionID, (isNetworkDrop) -> {
                // node is removed from active nodes
                this.activeNodes.remove(connectionID);
                if (isNetworkDrop) {
                    // network drop is handled
                    this.lobbyController.handlePlayerNetworkDrop(nickname, connectionID);
                }
            });

            // player connection is delegated to controller
            ConnectionResultEvent result = this.lobbyController.connectPlayer(nickname, view);

            if (result.isSuccess()) {
                // node is stored
                this.activeNodes.put(connectionID, view);
            } else {
                // heartbeat is stopped for rejected client
                view.stopHeartbeat();
            }

            // connection result is returned
            return result;

        } catch (Exception e) {
            // created node is retrieved
            RemoteClientView createdNode = this.activeNodes.remove(connectionID);
            if (createdNode != null) {
                // disconnection is handled
                createdNode.handleDisconnect();
            }
            // exception is thrown
            throw new RemoteException("Internal server error during connection setup.", e);
        }
    }

    @Override
    public void sendLobbyCommand(LobbyCommand command, String connectionID) throws RemoteException {
        // node is retrieved from active nodes
        RemoteClientView node = this.activeNodes.get(connectionID);
        if (node != null) {
            // nickname is extracted
            String nickname = node.getNickname();
            // lobby command is dispatched
            this.lobbyController.handleLobbyCommand(command, nickname);
        }
    }

    @Override
    public void sendGameCommand(GameCommand command, String connectionID) throws RemoteException {
        // node is retrieved from active nodes
        RemoteClientView node = this.activeNodes.get(connectionID);
        if (node != null) {
            // nickname is extracted
            String nickname = node.getNickname();
            // game command is dispatched
            this.lobbyController.handleGameCommand(command, nickname);
        }
    }
}
