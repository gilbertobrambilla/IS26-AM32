package it.polimi.ingsw.am32.network.client;

import it.polimi.ingsw.am32.client.ClientController;
import it.polimi.ingsw.am32.dto.messages.game.events.*;
import it.polimi.ingsw.am32.dto.messages.lobby.events.AvailableGamesListEvent;
import it.polimi.ingsw.am32.dto.messages.lobby.events.ConnectionResultEvent;
import it.polimi.ingsw.am32.dto.messages.lobby.events.LobbyStateUpdatedEvent;

public class MessageDispatcher implements ClientMessageVisitor {

    private final ClientController controller;

    public MessageDispatcher(ClientController controller) {
        this.controller = controller;
    }

    @Override
    public void visit(AvailableGamesListEvent message) {
        controller.onAvailableGamesListUpdate(message);
    }

    @Override
    public void visit(LobbyStateUpdatedEvent message) {
        controller.onLobbyRoomUpdated(message);
    }

    @Override
    public void visit(GameStartedEvent message) {
        controller.onGameStarted(message);
    }

    @Override
    public void visit(ActionResolvedEvent message) {
        controller.onActionResolved(message);
    }

    @Override
    public void visit(EventResolvedEvent message) {
        controller.onEventResolved(message);
    }

    @Override
    public void visit(ExtraAcquisitionResolvedEvent message) {
        controller.onExtraAcquisitionResolved(message);
    }

    @Override
    public void visit(GameEndedEvent message) {
        controller.onGameEnded(message);
    }

    @Override
    public void visit(GamePhaseChangedEvent message) {
        controller.onGamePhaseChanged(message);
    }

    @Override
    public void visit(InvalidActionEvent message) {
        controller.onInvalidAction(message);
    }

    @Override
    public void visit(PlayerDisconnectedEvent message) {
        controller.onPlayerDisconnected(message);
    }

    @Override
    public void visit(PlayerReconnectedEvent message) {
        controller.onPlayerReconnected(message);
    }

    @Override
    public void visit(RoundEndedEvent message) {
        controller.onRoundEnded(message);
    }

    @Override
    public void visit(TotemPlacedEvent message) {
        controller.onTotemPlaced(message);
    }

    @Override
    public void visit(ConnectionResultEvent message) {
        controller.onConnectionResult(message);
    }

    @Override
    public void visit(GameSuspendedEvent message) {
        controller.onGameSuspended(message);
    }

    @Override
    public void visit(GameResumedEvent message) {
        controller.onGameResumed(message);
    }

    @Override
    public void visit(GameStateEvent message) {
        controller.onGameStateUpdate(message);
    }
}
