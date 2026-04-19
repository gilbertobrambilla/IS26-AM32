package it.polimi.ingsw.am32.network.rmi;

import it.polimi.ingsw.am32.controller.commands.game.GameCommand;
import it.polimi.ingsw.am32.controller.commands.lobby.LobbyCommand;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIServer extends Remote {
    void ping() throws RemoteException;
    void connect(String nickname, RMIClient clientStub) throws RemoteException;
    void sendLobbyCommand(LobbyCommand command, String nickname) throws RemoteException;
    void sendGameCommand(GameCommand command, String nickname) throws RemoteException;
}