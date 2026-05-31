package it.polimi.ingsw.am32.network.rmi.server;

import it.polimi.ingsw.am32.controller.commands.game.GameCommand;
import it.polimi.ingsw.am32.controller.commands.lobby.LobbyCommand;
import it.polimi.ingsw.am32.network.rmi.client.RMIClient;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface provided for the rmi server.
 */
public interface RMIServer extends Remote {

    /**
     * Ping received from a client processed.
     *
     * @param connectionID the unique identifier of the connection
     * @throws RemoteException if a network error occurs
     */
    void onPingReceivedFromClient(String connectionID) throws RemoteException;

    /**
     * New anonymous player connected to the server.
     *
     * @param clientStub the remote reference to the client
     * @throws RemoteException if a network error occurs
     */
    void connect(RMIClient clientStub) throws RemoteException;

    /**
     * Connected player login processed.
     *
     * @param nickname the nickname of the player
     * @param connectionID the unique identifier of the connection
     * @throws RemoteException if a network error occurs
     */
    void login(String nickname, String connectionID) throws RemoteException;

    /**
     * Lobby command sent to the server.
     *
     * @param command the lobby command to be sent
     * @param connectionID the unique identifier of the connection
     * @throws RemoteException if a network error occurs
     */
    void sendLobbyCommand(LobbyCommand command, String connectionID) throws RemoteException;

    /**
     * Game command sent to the server.
     *
     * @param command the game command to be sent
     * @param connectionID the unique identifier of the connection
     * @throws RemoteException if a network error occurs
     */
    void sendGameCommand(GameCommand command, String connectionID) throws RemoteException;
}
