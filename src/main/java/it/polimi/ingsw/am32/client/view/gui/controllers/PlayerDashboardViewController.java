package it.polimi.ingsw.am32.client.view.gui.controllers;

import it.polimi.ingsw.am32.client.assets.AssetManager;
import it.polimi.ingsw.am32.client.model.LightPlayer;
import it.polimi.ingsw.am32.client.view.gui.components.CardView;
import it.polimi.ingsw.am32.client.view.gui.utils.GuiLogger;
import it.polimi.ingsw.am32.dto.setup.CharacterCardDTO;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Controller for the player dashboard strip shown at the bottom of the in-game view.
 *
 * <p>Displays the local player's food and prestige counters, and their hand of cards
 * grouped by category (character type or "Building") in stable left-to-right order.</p>
 *
 * <p>This controller carries no client-side state and has no external dependencies;
 * all data is pushed to it by its parent {@link InGameViewController}.</p>
 */
public class PlayerDashboardViewController {

    // --- Card layout constants ---
    private static final double CASCADE_SPACING = -85.0;
    private static final double COLUMN_WIDTH    = 90.0;
    private static final double CARD_WIDTH      = 72.0;
    private static final double CARD_HEIGHT     = 108.0;

    @FXML private Label myFoodLabel;
    @FXML private Label myPrestigeLabel;
    @FXML private HBox  myCardsContainer;

    /** Cached CardView instances keyed by card id to avoid redundant asset loads. */
    private final Map<String, CardView> myCardsCache = new HashMap<>();

    @FXML
    public void initialize() {
        clearDashboard();
    }

    /** Resets all counters to zero and empties the card hand. */
    public void clearDashboard() {
        myFoodLabel.setText("0");
        myPrestigeLabel.setText("0");
        myCardsContainer.getChildren().clear();
        myCardsCache.clear();
    }

    /**
     * Refreshes all dashboard values to reflect the local player's current state.
     *
     * @param localPlayer the local player model; no-op if {@code null}
     */
    public void updateDashboard(LightPlayer localPlayer) {
        if (localPlayer == null) return;

        myFoodLabel.setText(String.valueOf(localPlayer.getFood()));
        myPrestigeLabel.setText(String.valueOf(localPlayer.getPrestige()));

        // Character cards are listed before building cards — preserve this order.
        List<String> allMyCards = new ArrayList<>(localPlayer.getCharacterCards());
        allMyCards.addAll(localPlayer.getBuildingCards());

        Set<String> currentIds = new HashSet<>(allMyCards);
        myCardsCache.keySet().removeIf(id -> !currentIds.contains(id));

        for (String cardId : allMyCards) {
            if (!myCardsCache.containsKey(cardId)) {
                CardView cardView = new CardView(cardId, CARD_WIDTH, CARD_HEIGHT);
                cardView.setSlideUpEnabled(true);
                myCardsCache.put(cardId, cardView);
            }
        }

        // LinkedHashMap preserves insertion order so columns appear in the same
        // left-to-right sequence on every render, rather than varying by HashMap bucket.
        Map<String, List<CardView>> groupedCards = new LinkedHashMap<>();
        for (String cardId : allMyCards) {
            String category = resolveCardCategory(cardId);
            groupedCards.computeIfAbsent(category, k -> new ArrayList<>()).add(myCardsCache.get(cardId));
        }

        myCardsContainer.getChildren().clear();
        myCardsContainer.setSpacing(16.0);

        for (List<CardView> columnCards : groupedCards.values()) {
            myCardsContainer.getChildren().add(buildCardColumn(columnCards));
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Builds a cascading column of cards for a single category.
     *
     * @param cards the card views to stack; must not be empty
     * @return a fixed-width VBox with negative spacing to produce the cascade effect
     */
    private static VBox buildCardColumn(List<CardView> cards) {
        VBox column = new VBox(CASCADE_SPACING);
        column.setAlignment(Pos.TOP_CENTER);
        column.setFillWidth(false);
        column.setMinWidth(COLUMN_WIDTH);
        column.setPrefWidth(COLUMN_WIDTH);
        column.setMaxWidth(COLUMN_WIDTH);
        column.getChildren().addAll(cards);
        return column;
    }

    /**
     * Resolves the display category for a card id.
     * Building cards are all grouped under {@code "Building"}.
     * Character cards use the {@code characterType} field from the asset registry.
     *
     * @param cardId the card identifier
     * @return the category string used as a grouping key
     */
    private static String resolveCardCategory(String cardId) {
        if (cardId.startsWith("building")) return "Building";

        CharacterCardDTO dto = AssetManager.getInstance().getCharacterCard(cardId);
        if (dto == null) {
            GuiLogger.warning("[PlayerDashboardViewController] unknown card id: " + cardId);
            return "Generic";
        }
        return dto.getCharacterType();
    }
}
