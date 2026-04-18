package it.polimi.ingsw.am32.client;

import it.polimi.ingsw.am32.dto.results.GameResultDTO;
import it.polimi.ingsw.am32.dto.state.GameInfoDTO;
import it.polimi.ingsw.am32.model.enums.InvalidActionReason;
import it.polimi.ingsw.am32.model.observers.GameEvent;
import it.polimi.ingsw.am32.network.client.VirtualServer;

import java.util.List;

public class ClientController {

    private final VirtualServer virtualServer;
    // TODO UI: private UI ui; (This will be added when you create the TUI/GUI interfaces)

    public ClientController(VirtualServer virtualServer) {
        this.virtualServer = virtualServer;
    }

    public void onConnectionResult(boolean successful, String message) {
        // ui.showConnectionResult(successful, message);
        System.out.println("[CONTROLLER] Connection result processed.");
    }

    public void onLobbyStateUpdate(List<GameInfoDTO> availableGames) {
        // ui.updateLobbyGames(availableGames);
        System.out.println("[CONTROLLER] Lobby state passed to UI.");
    }

    public void onGameStarted(int gameId) {
        // ui.showGameScreen();
        System.out.println("[CONTROLLER] Notifying UI that game " + gameId + " started!");
    }

    public void onError(InvalidActionReason reason, String message) {
        // ui.showErrorMessage(message);
        System.err.println("[CONTROLLER] Error received: " + message);
    }

    public void onGameEvent(GameEvent event) {
        // ui.applyGameEvent(event);
        System.out.println("[CONTROLLER] Passing game event to UI.");
    }

    public void onGameSuspended() {
        // ui.showSuspendedScreen();
        System.out.println("[CONTROLLER] Notifying UI of game suspension.");
    }

    public void onGameResumed() {
        // ui.removeSuspendedScreen();
        System.out.println("[CONTROLLER] Notifying UI that game is back active.");
    }

    public void onGameEnded(GameResultDTO resultDTO) {
        // ui.showEndGameScreen(resultDTO);
        System.out.println("[CONTROLLER] Game over. Showing results.");
    }

}