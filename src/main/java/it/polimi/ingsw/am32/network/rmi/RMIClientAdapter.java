package it.polimi.ingsw.am32.network.rmi;

import it.polimi.ingsw.am32.dto.results.GameResultDTO;
import it.polimi.ingsw.am32.dto.state.GameInfoDTO;
import it.polimi.ingsw.am32.model.enums.InvalidActionReason;
import it.polimi.ingsw.am32.model.observers.GameEvent;
import it.polimi.ingsw.am32.network.ClientNode;
import it.polimi.ingsw.am32.network.exceptions.NetworkException;

import java.rmi.RemoteException;
import java.util.List;

public class RMIClientAdapter implements ClientNode {

    private final RMIClient clientStub;

    public RMIClientAdapter(RMIClient clientStub) {
        this.clientStub = clientStub;
    }

    @Override
    public void ping() {
        try {
            clientStub.ping();
        } catch (RemoteException e) {
            throw new NetworkException("Errore di rete RMI durante il ping", e);
        }
    }

    @Override
    public void notifyConnectionResult(boolean successful, String message) {
        try {
            clientStub.notifyConnectionResult(successful, message);
        } catch (RemoteException e) {
            throw new NetworkException("Errore di rete RMI", e);
        }
    }

    @Override
    public void notifyLobbyState(List<GameInfoDTO> availableGames) {
        try {
            clientStub.notifyLobbyState(availableGames);
        } catch (RemoteException e) {
            throw new NetworkException("Errore di rete RMI", e);
        }
    }

    @Override
    public void notifyGameStarted(int gameId) {
        try {
            clientStub.notifyGameStarted(gameId);
        } catch (RemoteException e) {
            throw new NetworkException("Errore di rete RMI", e);
        }
    }

    @Override
    public void notifyError(InvalidActionReason reason, String message) {
        try {
            clientStub.notifyError(reason, message);
        } catch (RemoteException e) {
            throw new NetworkException("Errore di rete RMI", e);
        }
    }

    @Override
    public void receiveEvent(GameEvent event) {
        try {
            clientStub.receiveEvent(event);
        } catch (RemoteException e) {
            throw new NetworkException("Errore di rete RMI", e);
        }
    }

    @Override
    public void notifyGameSuspended() {
        try {
            clientStub.notifyGameSuspended();
        } catch (RemoteException e) {
            throw new NetworkException("Errore di rete RMI", e);
        }
    }

    @Override
    public void notifyGameResumed() {
        try {
            clientStub.notifyGameResumed();
        } catch (RemoteException e) {
            throw new NetworkException("Errore di rete RMI", e);
        }
    }

    @Override
    public void notifyGameEnded(GameResultDTO resultDTO) {
        try {
            clientStub.notifyGameEnded(resultDTO);
        } catch (RemoteException e) {
            throw new NetworkException("Errore di rete RMI", e);
        }
    }

    @Override
    public void notifyLeaderboard(LeaderboardDTO leaderboard) {
        try {
            clientStub.notifyLeaderboard(leaderboard);
        } catch (RemoteException e) {
            throw new NetworkException("Errore di rete RMI durante notifyLeaderboard", e);
        }
    }
}