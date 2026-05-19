package it.polimi.ingsw.am32.client.view.gui.controllers;

import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.client.model.LightPlayer;
import it.polimi.ingsw.am32.client.view.gui.components.OpponentView;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpponentsSidebarViewController {

    private final ClientController clientController;

    @FXML private VBox opponentsContainer;

    private final Map<String, OpponentView> opponentViewsCache = new HashMap<>();

    public OpponentsSidebarViewController(ClientController clientController) {
        this.clientController = clientController;
    }

    @FXML
    public void initialize() {
        // ui and internal caches are cleared on initialization
        clearSidebar();
    }

    public void clearSidebar() {
        opponentsContainer.getChildren().clear();
        opponentViewsCache.clear();
    }

    public void updateOpponents(Map<String, LightPlayer> players, String localPlayerName) {
        if (players == null) return;

        // stale opponents are removed from cache
        opponentViewsCache.keySet().removeIf(nick -> !players.containsKey(nick));

        List<OpponentView> updatedViews = new ArrayList<>();

        players.forEach((nickname, player) -> {
            if (!nickname.equals(localPlayerName)) {
                OpponentView view = opponentViewsCache.computeIfAbsent(nickname, k -> new OpponentView(player));

                // view data is refreshed
                view.updateValues(player);

                // visual feedback for disconnected players is applied
                view.setOpacity(player.isConnected() ? 1.0 : 0.4);

                updatedViews.add(view);
            }
        });

        // container is populated with updated views
        opponentsContainer.getChildren().setAll(updatedViews);
    }
}
