package it.polimi.ingsw.am32.network;

import it.polimi.ingsw.am32.dto.messages.lobby.models.GameInfoDTO;

import java.util.List;

public interface LobbyClientConnection extends ClientConnection {
    void notifyConnectionResult(boolean successful, String message);
    void notifyLobbyState(List<GameInfoDTO> availableGames);
    void notifyGameStarted(int gameId);
}
