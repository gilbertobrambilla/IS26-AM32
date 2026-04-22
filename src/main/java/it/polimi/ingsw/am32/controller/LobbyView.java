package it.polimi.ingsw.am32.controller;

import it.polimi.ingsw.am32.dto.messages.lobby.events.AvailableGamesListEvent;
import it.polimi.ingsw.am32.dto.messages.lobby.events.ConnectionResultEvent;
import it.polimi.ingsw.am32.dto.messages.lobby.events.LobbyStateUpdatedEvent;

public interface LobbyView {
    void notifyConnectionResult(ConnectionResultEvent dto);
    void notifyLobbyState(AvailableGamesListEvent dto);
    void notifyLobbyRoomUpdated(LobbyStateUpdatedEvent dto);
}
