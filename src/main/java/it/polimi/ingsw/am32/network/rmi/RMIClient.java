package it.polimi.ingsw.am32.network.rmi;

import it.polimi.ingsw.am32.dto.results.GameResultDTO;
import it.polimi.ingsw.am32.dto.results.LeaderboardDTO;
import it.polimi.ingsw.am32.dto.state.GameInfoDTO;
import it.polimi.ingsw.am32.model.enums.InvalidActionReason;
import it.polimi.ingsw.am32.model.observers.GameEvent;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RMIClient extends Remote {
    void ping() throws RemoteException;
    void notifyConnectionResult(boolean successful, String message) throws RemoteException;
    void notifyLobbyState(List<GameInfoDTO> availableGames) throws RemoteException;
    void notifyGameStarted(int gameId) throws RemoteException;
    void notifyLeaderboard(LeaderboardDTO leaderboard) throws RemoteException;
    void notifyError(InvalidActionReason reason, String message) throws RemoteException;
    void receiveEvent(GameEvent event) throws RemoteException;
    void notifyGameSuspended() throws RemoteException;
    void notifyGameResumed() throws RemoteException;
    void notifyGameEnded(GameResultDTO resultDTO) throws RemoteException;
}