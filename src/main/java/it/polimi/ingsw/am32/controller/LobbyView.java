package it.polimi.ingsw.am32.controller;

import it.polimi.ingsw.am32.dto.messages.lobby.events.*;

public interface LobbyView {
    void notifyConnectionResult(ConnectionResultEvent dto);
    void notifyLobbyState(AvailableLobbiesEvent dto);
    void notifyLobbyRoomUpdated(LobbyStateUpdatedEvent dto);
    void notifyLobbyError(LobbyErrorEvent dto);
}
