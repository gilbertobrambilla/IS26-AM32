package it.polimi.ingsw.am32.network.rmi;

import it.polimi.ingsw.am32.controller.LobbyController;
import it.polimi.ingsw.am32.controller.VirtualView;
import it.polimi.ingsw.am32.network.server.VirtualViewImpl;
import it.polimi.ingsw.am32.network.ClientNode;
import it.polimi.ingsw.am32.controller.commands.game.GameCommand;
import it.polimi.ingsw.am32.controller.commands.lobby.LobbyCommand;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;

public class RMIServerImpl extends UnicastRemoteObject implements RMIServer {

    private final LobbyController lobbyController;
    private final ConcurrentHashMap<String, VirtualView> activeViews;

    public RMIServerImpl(LobbyController lobbyController) throws RemoteException {
        super();
        this.lobbyController = lobbyController;
        this.activeViews = new ConcurrentHashMap<>();
    }

    @Override
    public void ping() throws RemoteException {
    }

    @Override
    public void connect(String nickname, RMIClient clientStub) throws RemoteException {
        ClientNode clientNode = new RMIClientAdapter(clientStub);

        if (activeViews.putIfAbsent(nickname, null) != null || activeViews.get(nickname) != null) {
            throw new RemoteException("Nickname already in use: " + nickname);
        }

        try {
            VirtualView view = VirtualViewImpl.create(nickname, clientNode, (isNetworkDrop) -> {
                activeViews.remove(nickname);
                if (isNetworkDrop) {
                    lobbyController.handlePlayerNetworkDrop(nickname);
                }
            });

            activeViews.put(nickname, view);
            lobbyController.connectPlayer(nickname, view);

        } catch (Exception e) {
            VirtualView createdView = activeViews.remove(nickname);
            if (createdView != null) {
                createdView.handleDisconnect();
            }
            throw new RemoteException("Internal server error during connection setup.", e);
        }
    }

    @Override
    public void sendLobbyCommand(LobbyCommand command, String nickname) throws RemoteException {
        lobbyController.handleLobbyCommand(command, nickname);
    }

    @Override
    public void sendGameCommand(GameCommand command, String nickname) throws RemoteException {
        lobbyController.handleGameCommand(command, nickname);
    }
}