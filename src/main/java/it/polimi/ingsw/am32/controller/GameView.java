package it.polimi.ingsw.am32.controller;

import it.polimi.ingsw.am32.dto.messages.game.events.*;
import it.polimi.ingsw.am32.dto.messages.lobby.events.LobbyStateUpdatedEvent;

public interface GameView {

    // state and lifecycle events
    void notifyLobbyRoomUpdated(LobbyStateUpdatedEvent dto);
    void notifyGameStarted(GameStartedEvent dto);
    void notifyGameState(GameStateEvent dto);
    void notifyGameEnded(GameEndedEvent resultDTO);
    void notifyError(GameErrorEvent dto);

    // resilience events
    void notifyGameSuspended();
    void notifyGameResumed();
    void notifyPlayerDisconnected(PlayerDisconnectedEvent dto);
    void notifyPlayerReconnected(PlayerReconnectedEvent dto);

    // gameplay events
    void notifyTotemPlaced(TotemPlacedEvent dto);
    void notifyActionResolved(ActionResolvedEvent dto);
    void notifyExtraAcquisitionResolved(ExtraAcquisitionResolvedEvent dto);
    void notifyEventResolved(EventResolvedEvent dto);
    void notifyRoundEnded(RoundEndedEvent dto);
    void notifyGamePhaseChanged(GamePhaseChangedEvent dto);

    /**
     * Retrieves the unique identifier associated with this connection.
     * @return the unique connection id.
     */
    String getConnectionID();
}
