package it.polimi.ingsw.am32.client.view.gui.controllers;

import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.client.model.LightPlayer;
import it.polimi.ingsw.am32.client.view.gui.components.OpponentView;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for the opponents sidebar shown during a game session.
 *
 * <p>Maintains a per-nickname cache of {@link OpponentView} instances so that
 * each view is created only once. Interaction handlers (cursor, click) are
 * registered in the same {@code computeIfAbsent} call, ensuring they are bound
 * exactly once per view rather than re-registered on every state update.</p>
 */
public class OpponentsSidebarViewController {

    private final ClientController clientController;

    @FXML private VBox opponentsContainer;

    private final Map<String, OpponentView> opponentViewsCache = new HashMap<>();

    public OpponentsSidebarViewController(ClientController clientController) {
        this.clientController = clientController;
    }

    @FXML
    public void initialize() {
        clearSidebar();
    }

    /** Removes all opponent views and resets the internal cache. */
    public void clearSidebar() {
        opponentsContainer.getChildren().clear();
        opponentViewsCache.clear();
    }

    /**
     * Synchronises the sidebar with the current player map.
     *
     * <p>Players no longer present in {@code players} are evicted from the cache.
     * New players get a fresh {@link OpponentView} with interaction handlers bound
     * once. Existing views are updated in-place (data only; handlers are untouched).
     * The local player is excluded from the list.</p>
     *
     * @param players         the full player map from the current game state
     * @param localPlayerName the nickname of the local player to exclude
     */
    public void updateOpponents(Map<String, LightPlayer> players, String localPlayerName) {
        if (players == null) return;

        // Evict stale entries first so cache stays lean.
        opponentViewsCache.keySet().removeIf(nick -> !players.containsKey(nick));

        List<OpponentView> orderedViews = new ArrayList<>();

        players.forEach((nickname, player) -> {
            if (nickname.equals(localPlayerName)) return;

            OpponentView view = opponentViewsCache.computeIfAbsent(nickname, k -> {
                OpponentView newView = new OpponentView(player);

                // Interaction handlers are set once here and never re-registered.
                newView.setCursor(Cursor.HAND);
                newView.setOnMouseClicked(e -> clientController.requestPlayerFocus(k));

                return newView;
            });

            // Data update runs on every refresh; handler setup does not.
            view.updateValues(player);
            view.setOpacity(player.isConnected() ? 1.0 : 0.4);

            orderedViews.add(view);
        });

        opponentsContainer.getChildren().setAll(orderedViews);
    }
}
