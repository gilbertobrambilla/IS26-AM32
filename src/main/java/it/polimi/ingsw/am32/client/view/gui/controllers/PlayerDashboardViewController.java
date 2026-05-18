package it.polimi.ingsw.am32.client.view.gui.controllers;

import it.polimi.ingsw.am32.client.assets.AssetManager;
import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.client.model.LightPlayer;
import it.polimi.ingsw.am32.client.view.gui.components.CardView;
import it.polimi.ingsw.am32.dto.setup.CharacterCardDTO;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.*;

public class PlayerDashboardViewController {

    private final ClientController clientController;

    @FXML private Label myFoodLabel;
    @FXML private Label myPrestigeLabel;
    @FXML private HBox myCardsContainer;

    private final Map<String, CardView> myCardsCache = new HashMap<>();
    private static final double CASCADE_SPACING = -150.0;

    public PlayerDashboardViewController(ClientController clientController) {
        this.clientController = clientController;
    }

    @FXML
    public void initialize() {
        // internal caches and layouts are reset
        clearDashboard();
    }

    public void clearDashboard() {
        myFoodLabel.setText("0");
        myPrestigeLabel.setText("0");
        myCardsContainer.getChildren().clear();
        myCardsCache.clear();
    }

    public void updateDashboard(LightPlayer localPlayer) {
        if (localPlayer == null) return;

        // local vitals are updated
        myFoodLabel.setText(String.valueOf(localPlayer.getFood()));
        myPrestigeLabel.setText(String.valueOf(localPlayer.getPrestige()));

        List<String> allMyCards = new ArrayList<>();
        allMyCards.addAll(localPlayer.getCharacterCards());
        allMyCards.addAll(localPlayer.getBuildingCards());
        Set<String> currentIds = new HashSet<>(allMyCards);

        // stale cards are removed from cache
        myCardsCache.keySet().removeIf(id -> !currentIds.contains(id));

        for (String cardId : allMyCards) {
            if (!myCardsCache.containsKey(cardId)) {
                CardView cardView = new CardView(cardId);
                myCardsCache.put(cardId, cardView);
            }
        }

        // cards are grouped by category for layout
        Map<String, List<CardView>> groupedCards = new HashMap<>();
        for (String cardId : allMyCards) {
            String category = getCardCategory(cardId);
            groupedCards.computeIfAbsent(category, k -> new ArrayList<>()).add(myCardsCache.get(cardId));
        }

        myCardsContainer.getChildren().clear();

        // cascaded columns are constructed
        for (Map.Entry<String, List<CardView>> entry : groupedCards.entrySet()) {
            VBox column = new VBox();
            column.setAlignment(Pos.TOP_CENTER);
            column.setSpacing(CASCADE_SPACING);
            column.getChildren().addAll(entry.getValue());
            myCardsContainer.getChildren().add(column);
        }
    }

    private String getCardCategory(String cardId) {
        if (cardId.startsWith("building")) {
            return "Building";
        }
        CharacterCardDTO dto = AssetManager.getInstance().getCharacterCard(cardId);
        if (dto == null) {
            // fallback category is assigned for unknown identifiers
            System.err.println("[PlayerDashboardViewController] unknown card id: " + cardId);
            return "Generic";
        }
        return dto.getCharacterType();
    }
}
