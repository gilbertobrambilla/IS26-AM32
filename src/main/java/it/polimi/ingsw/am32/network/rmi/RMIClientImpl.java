package it.polimi.ingsw.am32.network.rmi;

import it.polimi.ingsw.am32.dto.messages.game.models.GameResultDTO;
import it.polimi.ingsw.am32.dto.messages.lobby.models.GameInfoDTO;
import it.polimi.ingsw.am32.model.enums.InvalidActionReason;
import it.polimi.ingsw.am32.model.observers.GameEvent;
import it.polimi.ingsw.am32.network.client.VirtualServer;

import java.rmi.RemoteException;
import java.util.List;

public class RMIClientImpl implements RMIClient {

    private VirtualServer virtualServer;

    public RMIClientImpl() {
    }

    public void setVirtualServer(VirtualServer virtualServer) {
        this.virtualServer = virtualServer;
    }

    @Override
    public void ping() throws RemoteException {
    }

    @Override
    public void notifyConnectionResult(boolean successful, String message) throws RemoteException {
        if (virtualServer != null) {
            virtualServer.receiveConnectionResult(successful, message);
        }
    }

    @Override
    public void notifyLobbyState(List<GameInfoDTO> availableGames) throws RemoteException {
        if (virtualServer != null) {
            virtualServer.receiveLobbyState(availableGames);
        }
    }

    @Override
    public void notifyGameStarted(int gameId) throws RemoteException {
        if (virtualServer != null) {
            virtualServer.receiveGameStarted(gameId);
        }
    }

    @Override
    public void notifyError(InvalidActionReason reason, String message) throws RemoteException {
        if (virtualServer != null) {
            virtualServer.receiveError(reason, message);
        }
    }

    @Override
    public void receiveEvent(GameEvent event) throws RemoteException {
        if (virtualServer != null) {
            virtualServer.receiveEvent(event);
        }
    }

    @Override
    public void notifyGameSuspended() throws RemoteException {
        if (virtualServer != null) {
            virtualServer.receiveGameSuspended();
        }
    }

    @Override
    public void notifyGameResumed() throws RemoteException {
        if (virtualServer != null) {
            virtualServer.receiveGameResumed();
        }
    }

    @Override
    public void notifyGameEnded(GameResultDTO resultDTO) throws RemoteException {
        if (virtualServer != null) {
            virtualServer.receiveGameEnded(resultDTO);
        }
    }
}
