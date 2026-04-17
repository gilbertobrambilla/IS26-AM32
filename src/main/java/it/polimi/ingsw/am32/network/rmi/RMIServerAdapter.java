package it.polimi.ingsw.am32.network.rmi;

import it.polimi.ingsw.am32.controller.commands.game.GameCommand;
import it.polimi.ingsw.am32.controller.commands.lobby.LobbyCommand;
import it.polimi.ingsw.am32.network.client.ServerNode;
import it.polimi.ingsw.am32.network.exceptions.NetworkException;

import java.rmi.RemoteException;

public class RMIServerAdapter implements ServerNode {

    private final RMIServer serverStub;
    private final RMIClient clientStub;

    public RMIServerAdapter(RMIServer serverStub, RMIClient clientStub) {
        this.serverStub = serverStub;
        this.clientStub = clientStub;
    }

    @Override
    public void connect(String nickname) {
        try {
            serverStub.connect(nickname, clientStub);
        } catch (RemoteException e) {
            throw new NetworkException("RMI error while connecting", e);
        }
    }

    @Override
    public void sendLobbyCommand(LobbyCommand command, String nickname) {
        try {
            serverStub.sendLobbyCommand(command, nickname);
        } catch (RemoteException e) {
            throw new NetworkException("RMI error sending LobbyCommand", e);
        }
    }

    @Override
    public void sendGameCommand(GameCommand command, String nickname) {
        try {
            serverStub.sendGameCommand(command, nickname);
        } catch (RemoteException e) {
            throw new NetworkException("RMI error sending GameCommand", e);
        }
    }

    @Override
    public void disconnect() {
        System.out.println("[RMI] disconnecting from server and cleaning up resources...");

        try {
            java.rmi.server.UnicastRemoteObject.unexportObject(clientStub, true);
        } catch (java.rmi.NoSuchObjectException e) {
            System.err.println("[RMI] client object was already unexported.");
        }
    }
}