package it.polimi.ingsw.am32.client.view;

import it.polimi.ingsw.am32.dto.messages.game.events.*;
import it.polimi.ingsw.am32.dto.messages.lobby.events.*;

/**
 * Visual interface contract for the client.
 */
public interface UI {
    // --- connection phase ---
    /** Connection result is shown. */
    void showConnectionResult(ConnectionResultEvent event);

    // --- lobby phase ---
    /** Available games list is updated. */
    void updateAvailableGames(AvailableLobbiesEvent event);
    /** Lobby room state is updated. */
    void updateLobbyRoom(LobbyStateUpdatedEvent event);
    /** Join game error is shown. */
    void showLobbyError(LobbyErrorEvent event);

    // --- game lifecycle ---
    /** Game start is shown. */
    void showGameStarted(GameStartedEvent event);
    /** Game state is restored. */
    void restoreGameState(GameStateEvent event);
    /** Game phase is updated. */
    void updateGamePhase(GamePhaseChangedEvent event);
    /** Round summary is shown. */
    void showRoundSummary(RoundEndedEvent event);
    /** Game results are shown. */
    void showGameResults(GameEndedEvent event);

    // --- gameplay actions ---
    /** Totem placement is shown. */
    void showTotemPlaced(TotemPlacedEvent event);
    /** Action resolution is shown. */
    void showActionResolved(ActionResolvedEvent event);
    /** Event resolution is shown. */
    void showEventResolved(EventResolvedEvent event);
    /** Extra acquisition is shown. */
    void showExtraAcquisition(ExtraAcquisitionResolvedEvent event);
    /**
     * Focus is set on a specific player in the user interface.
     * * @param nickname the nickname of the player to focus on, or null to clear focus.
     */
    void showPlayerFocus(String nickname);

    // --- resilience and errors ---
    /** Error message is shown. */
    void showError(GameErrorEvent event);
    /** Player disconnection is shown. */
    void showPlayerDisconnected(PlayerDisconnectedEvent event);
    /** Player reconnection is shown. */
    void showPlayerReconnected(PlayerReconnectedEvent event);
    /** Game suspension is shown. */
    void showGameSuspended(GameSuspendedEvent event);
    /** Game resumption is shown. */
    void showGameResumed(GameResumedEvent event);
    /** Fatal connection loss is shown. */
    void notifyDisconnection(String message);
}
