package it.polimi.ingsw.am32.network.rmi.server;

import it.polimi.ingsw.am32.controller.commands.game.GameCommand;
import it.polimi.ingsw.am32.controller.commands.lobby.LobbyCommand;
import it.polimi.ingsw.am32.dto.messages.lobby.events.ConnectionResultEvent;
import it.polimi.ingsw.am32.network.rmi.client.RMIClient;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface for the rmi server.
 */
public interface RMIServer extends Remote {
    /**
     * Checks the liveness of a client connection.
     *
     * @param connectionID The unique identifier of the connection.
     * @throws RemoteException If a network error occurs.
     */
    void ping(String connectionID) throws RemoteException;

    /**
     * Connects a new player to the server and returns a unique connection identifier.
     *
     * @param nickname The nickname of the connecting player.
     * @param clientStub The remote reference to the client.
     * @return The unique connection identifier.
     * @throws RemoteException If a network error occurs.
     */
    ConnectionResultEvent connect(String nickname, RMIClient clientStub) throws RemoteException;

    /**
     * Sends a lobby command to the server.
     *
     * @param command The lobby command to be sent.
     * @param connectionID The unique identifier of the connection.
     * @throws RemoteException If a network error occurs.
     */
    void sendLobbyCommand(LobbyCommand command, String connectionID) throws RemoteException;

    /**
     * Sends a game command to the server.
     *
     * @param command The game command to be sent.
     * @param connectionID The unique identifier of the connection.
     * @throws RemoteException If a network error occurs.
     */
    void sendGameCommand(GameCommand command, String connectionID) throws RemoteException;
}
