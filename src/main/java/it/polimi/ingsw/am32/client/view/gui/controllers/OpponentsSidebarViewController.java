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
 * The opponent sidebar controller is represented during a game session.
 *
 * <p>A per-nickname cache of {@link OpponentView} instances is maintained so
 * each view is created only once. Interaction handlers are registered in the
 * same {@code computeIfAbsent} call and bound exactly once per view.</p>
 */
public class OpponentsSidebarViewController {

    private final ClientController clientController;

    @FXML private VBox opponentsContainer;

    private final Map<String, OpponentView> opponentViewsCache = new HashMap<>();

    /**
     * Dependency injection is handled through the constructor.
     *
     * @param clientController the main client controller
     */
    public OpponentsSidebarViewController(ClientController clientController) {
        this.clientController = clientController;
    }

    /**
     * Sidebar contents are initialized.
     */
    @FXML
    public void initialize() {
        clearSidebar();
    }

    /**
     * All opponent views are removed and the internal cache is reset.
     */
    public void clearSidebar() {
        opponentsContainer.getChildren().clear();
        opponentViewsCache.clear();
    }

    /**
     * The sidebar is synchronized with the current player map.
     *
     * <p>Players no longer present in {@code players} are evicted from the cache.
     * New players are assigned a fresh {@link OpponentView} with interaction
     * handlers bound once. Existing views are updated in-place. The local player
     * is excluded from the list.</p>
     *
     * @param players the full player map from the current game state
     * @param localPlayerName the nickname of the local player to exclude
     */
    public void updateOpponents(Map<String, LightPlayer> players, String localPlayerName) {
        if (players == null) return;

        // stale entries are evicted first so cache stays lean
        opponentViewsCache.keySet().removeIf(nick -> !players.containsKey(nick));

        List<OpponentView> orderedViews = new ArrayList<>();

        players.forEach((nickname, player) -> {
            if (nickname.equals(localPlayerName)) return;

            OpponentView view = opponentViewsCache.computeIfAbsent(nickname, k -> {
                OpponentView newView = new OpponentView(player);

                // interaction handlers are set once and never re-registered
                newView.setCursor(Cursor.HAND);
                newView.setOnMouseClicked(e -> clientController.requestPlayerFocus(k));

                return newView;
            });

            // data is updated on every refresh while handler setup is not repeated
            view.updateValues(player);
            view.setOpacity(player.isConnected() ? 1.0 : 0.4);

            orderedViews.add(view);
        });

        opponentsContainer.getChildren().setAll(orderedViews);
    }
}
