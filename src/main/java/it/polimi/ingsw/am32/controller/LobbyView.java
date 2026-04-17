package it.polimi.ingsw.am32.controller;

import it.polimi.ingsw.am32.dto.state.GameInfoDTO;

import java.util.List;

public interface LobbyView {
    void notifyConnectionResult(boolean successful, String message);
    void notifyLobbyState(List<GameInfoDTO> availableGames);
    void notifyGameStarted(int gameId);
}
