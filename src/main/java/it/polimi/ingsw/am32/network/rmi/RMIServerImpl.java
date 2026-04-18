package it.polimi.ingsw.am32.network.rmi;

import it.polimi.ingsw.am32.controller.LobbyController;
import it.polimi.ingsw.am32.controller.VirtualView;
import it.polimi.ingsw.am32.controller.commands.game.GameCommand;
import it.polimi.ingsw.am32.controller.commands.lobby.LobbyCommand;
import it.polimi.ingsw.am32.network.ClientNode;
import it.polimi.ingsw.am32.network.server.VirtualViewImpl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RMIServerImpl extends UnicastRemoteObject implements RMIServer {

    private final transient LobbyController lobbyController;
    private final transient Map<String, VirtualView> activeViews;

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
        System.out.println("[RMI SERVER] connection request received from: " + nickname);

        ClientNode clientNode = new RMIClientAdapter(clientStub);

        VirtualView view = VirtualViewImpl.create(nickname, clientNode, () -> {
            activeViews.remove(nickname);
            lobbyController.handlePlayerNetworkDrop(nickname);
        });

        if (activeViews.putIfAbsent(nickname, view) != null) {
            view.handleDisconnect();
            throw new RemoteException("Nickname already in use: " + nickname);
        }

        lobbyController.connectPlayer(nickname, view);
    }

    @Override
    public void sendLobbyCommand(LobbyCommand command, String nickname) throws RemoteException {
        System.out.println("[RMI SERVER] LobbyCommand received from: " + nickname);
        VirtualView view = activeViews.get(nickname);

        if (view != null) {
            command.execute(lobbyController, view, nickname);
        }
    }

    @Override
    public void sendGameCommand(GameCommand command, String nickname) throws RemoteException {
        System.out.println("[RMI SERVER] game command received from: " + nickname);
        lobbyController.handleGameCommand(command, nickname);
    }
}