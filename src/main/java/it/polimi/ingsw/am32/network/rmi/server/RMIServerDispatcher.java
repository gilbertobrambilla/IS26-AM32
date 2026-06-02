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
 * The rmi server interface is implemented.
 */
public class RMIServerDispatcher extends UnicastRemoteObject implements RMIServer {

    private final LobbyController lobbyController;
    private final ConcurrentHashMap<String, RemoteClientView> activeNodes;

    /**
     * A new rmi server implementation is constructed.
     *
     * @param lobbyController the controller handling the lobby logic
     * @throws RemoteException if the remote object cannot be exported
     */
    public RMIServerDispatcher(LobbyController lobbyController) throws RemoteException {
        super();
        this.lobbyController = lobbyController;
        // active nodes map instantiated
        this.activeNodes = new ConcurrentHashMap<>();
    }

    @Override
    public void onPingReceivedFromClient(String connectionID) throws RemoteException {
        if (connectionID == null) {
            // anonymous ping ignored
            return;
        }

        // node retrieved from active nodes
        RemoteClientView node = activeNodes.get(connectionID);
        if (node != null) {
            // ping received processed
            node.onPingReceivedFromClient();
        }
    }

    @Override
    public void connect(RMIClient clientStub) throws RemoteException {
        // client node instantiated
        ClientNode clientNode = new RMIClientProxy(clientStub);

        // connection id generated
        String connectionID = UUID.randomUUID().toString();

        try {
            // view implementation instantiated
            RemoteClientView view = RemoteClientView.create(clientNode, connectionID, (isNetworkDrop) -> {
                // node removed from active nodes
                this.activeNodes.remove(connectionID);
                if (isNetworkDrop) {
                    // network drop handled
                    this.lobbyController.processPlayerNetworkDrop(connectionID);
                }
            });

            // node stored immediately
            this.activeNodes.put(connectionID, view);

            // connection result notified to the client
            view.notifyConnectionResult(new ConnectionResultEvent(true, "connection successful", connectionID));

        } catch (Exception e) {
            // created node retrieved
            RemoteClientView createdNode = this.activeNodes.remove(connectionID);
            if (createdNode != null) {
                // disconnection handled
                createdNode.handleDisconnect();
            }
            // exception thrown
            throw new RemoteException("internal server error during connection setup", e);
        }
    }

    @Override
    public void login(String nickname, String connectionID) throws RemoteException {
        // node retrieved from active nodes
        RemoteClientView node = this.activeNodes.get(connectionID);
        if (node != null) {
            this.lobbyController.processLogin(nickname, node);
        }
    }

    @Override
    public void sendLobbyCommand(LobbyCommand command, String connectionID) throws RemoteException {
        // lobby command dispatched
        this.lobbyController.processLobbyCommand(command, connectionID);
    }

    @Override
    public void sendGameCommand(GameCommand command, String connectionID) throws RemoteException {
        // game command dispatched
        this.lobbyController.processGameCommand(command, connectionID);
    }
}
