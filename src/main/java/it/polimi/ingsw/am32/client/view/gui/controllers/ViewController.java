package it.polimi.ingsw.am32.client.view.gui.controllers;

import it.polimi.ingsw.am32.dto.messages.game.events.*;
import it.polimi.ingsw.am32.dto.messages.lobby.events.*;

public interface ViewController {

    default void onConnectionResult(ConnectionResultEvent event) {}
    default void onDisconnection(String message) {}
    default void onLoginResult(LoginResultEvent event) {}

    default void onLobbyError(LobbyErrorEvent event) {}
    default void onAvailableLobbies(AvailableLobbiesEvent event) {}
    default void onLobbyRoomUpdate(LobbyRoomUpdateEvent event) {}
    default void onGameStarted(GameStartedEvent event) {}
    default void onGameStateRestored(GameStateEvent event) {}
    default void onGamePhaseUpdated(GamePhaseChangedEvent event) {}
    default void onRoundSummary(RoundEndedEvent event) {}
    default void onGameResults(GameEndedEvent event) {}

    default void onLeaderboard(LeaderboardEvent event) {}


    default void onTotemPlaced(TotemPlacedEvent event) {}
    default void onActionResolved(ActionResolvedEvent event) {}
    default void onEventResolved(EventResolvedEvent event) {}
    default void onExtraAcquisition(ExtraAcquisitionResolvedEvent event) {}
    default void onPlayerFocus(String nickname) {}

    default void onError(GameErrorEvent event) {}
    default void onPlayerDisconnected(PlayerDisconnectedEvent event) {}
    default void onPlayerReconnected(PlayerReconnectedEvent event) {}
    default void onGameSuspended(GameSuspendedEvent event) {}
    default void onGameResumed(GameResumedEvent event) {}
}
