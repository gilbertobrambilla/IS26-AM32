package it.polimi.ingsw.am32.network.rmi;

import it.polimi.ingsw.am32.controller.LobbyController;
import it.polimi.ingsw.am32.network.server.VirtualViewImpl;
import it.polimi.ingsw.am32.network.ClientNode;
import it.polimi.ingsw.am32.controller.commands.game.GameCommand;
import it.polimi.ingsw.am32.controller.commands.lobby.LobbyCommand;
import it.polimi.ingsw.am32.network.server.NetworkNode;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the RMI server interface.
 * Handles player connections and dispatches commands to the lobby controller.
 */
public class RMIServerImpl extends UnicastRemoteObject implements RMIServer {

    private final LobbyController lobbyController;
    private final ConcurrentHashMap<String, NetworkNode> activeNodes;

    /**
     * Constructs a new RMI server implementation.
     *
     * @param lobbyController the controller handling the lobby logic.
     * @throws RemoteException if the remote object cannot be exported.
     */
    public RMIServerImpl(LobbyController lobbyController) throws RemoteException {
        super();
        this.lobbyController = lobbyController;
        this.activeNodes = new ConcurrentHashMap<>();
    }

    /**
     * Responds to a ping from the client to check connection liveness.
     *
     * @throws RemoteException if a network error occurs.
     */
    @Override
    public void ping() throws RemoteException {
        // empty implementation for liveness check
    }

    /**
     * Connects a new player to the server via RMI.
     *
     * @param nickname   the nickname of the connecting player.
     * @param clientStub the remote reference to the client.
     * @throws RemoteException if the connection fails or the nickname is already taken.
     */
    @Override
    public void connect(String nickname, RMIClient clientStub) throws RemoteException {
        ClientNode clientNode = new RMIClientAdapter(clientStub);

        // nickname uniqueness is verified using collection checks
        if (this.activeNodes.containsKey(nickname)) {
            throw new RemoteException("Nickname already in use: " + nickname);
        }

        try {
            // view implementation is instantiated
            VirtualViewImpl view = VirtualViewImpl.create(nickname, clientNode, (isNetworkDrop) -> {
                this.activeNodes.remove(nickname);
                if (isNetworkDrop) {
                    this.lobbyController.handlePlayerNetworkDrop(nickname);
                }
            });

            // node is stored for lifecycle management
            this.activeNodes.put(nickname, view);

            // player connection is triggered on the controller
            this.lobbyController.connectPlayer(nickname, view);

        } catch (Exception e) {
            // failed connection cleanup is performed via the network node interface
            NetworkNode createdNode = this.activeNodes.remove(nickname);
            if (createdNode != null) {
                createdNode.handleDisconnect();
            }
            throw new RemoteException("Internal server error during connection setup.", e);
        }
    }

    /**
     * Forwards a lobby command to the lobby controller.
     *
     * @param command  the command to be executed.
     * @param nickname the nickname of the player issuing the command.
     * @throws RemoteException if a network error occurs.
     */
    @Override
    public void sendLobbyCommand(LobbyCommand command, String nickname) throws RemoteException {
        // lobby command is dispatched to the controller
        this.lobbyController.handleLobbyCommand(command, nickname);
    }

    /**
     * Forwards a game command to the lobby controller for routing.
     *
     * @param command  the command to be executed.
     * @param nickname the nickname of the player issuing the command.
     * @throws RemoteException if a network error occurs.
     */
    @Override
    public void sendGameCommand(GameCommand command, String nickname) throws RemoteException {
        // game command is dispatched to the controller for routing
        this.lobbyController.handleGameCommand(command, nickname);
    }
}
