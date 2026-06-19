package it.polimi.ingsw.am32.client.view.gui.controllers;

import it.polimi.ingsw.am32.client.assets.AssetManager;
import it.polimi.ingsw.am32.client.model.LightPlayer;
import it.polimi.ingsw.am32.client.view.gui.components.CardView;
import it.polimi.ingsw.am32.client.view.gui.components.TotemView;
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
 * The player dashboard strip controller is represented.
 *
 * <p>The local player's food, prestige, and cards are displayed. Cards are grouped
 * by category in stable left-to-right order.</p>
 *
 * <p>No client-side state is carried and no external dependencies are required.
 * All data is pushed by the parent {@link InGameViewController}.</p>
 */
public class PlayerDashboardViewController {

    // card layout constants
    private static final double CASCADE_SPACING = -85.0;
    private static final double COLUMN_WIDTH    = 90.0;
    private static final double CARD_WIDTH      = 72.0;
    private static final double CARD_HEIGHT     = 108.0;

    @FXML private Label myFoodLabel;
    @FXML private Label myPrestigeLabel;
    @FXML private HBox  myCardsContainer;

    @FXML private HBox myTotemContainer;

    /** Card id keys cached card view instances to avoid redundant asset loads. */
    private final Map<String, CardView> myCardsCache = new HashMap<>();

    /**
     * Dashboard contents are initialized.
     */
    @FXML
    public void initialize() {
        clearDashboard();
    }

    /**
     * All counters are reset to zero and the card hand is emptied.
     */
    public void clearDashboard() {
        myFoodLabel.setText("0");
        myPrestigeLabel.setText("0");
        myCardsContainer.getChildren().clear();
        myCardsCache.clear();

        if (myTotemContainer != null) {
            myTotemContainer.getChildren().clear();
        }
    }

    /**
     * All dashboard values are refreshed from the local player's current state.
     *
     * @param localPlayer the local player model; no-op if {@code null}
     */
    public void updateDashboard(LightPlayer localPlayer) {
        if (localPlayer == null) return;

        myFoodLabel.setText(String.valueOf(localPlayer.getFood()));
        myPrestigeLabel.setText(String.valueOf(localPlayer.getPrestige()));

        // character cards are listed before building cards to preserve order
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

        // linked hash map preserves stable left-to-right column order
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

        if (myTotemContainer != null) {
            myTotemContainer.getChildren().clear();

            TotemView myTotem = new TotemView(localPlayer.getTotemColor(), null, false);
            myTotem.setMaxSize(30.0, 30.0);
            myTotem.setPrefSize(30.0, 30.0);

            Label nameLabel = new Label(localPlayer.getNickname());
            nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

            myTotemContainer.getChildren().addAll(myTotem, nameLabel);
        }
    }

    // private helpers

    /**
     * A cascading column of cards is built for a single category.
     *
     * @param cards the card views to stack; must not be empty
     * @return a fixed-width VBox with negative spacing to produce the cascade effect
     */
    private static VBox buildCardColumn(List<CardView> cards) {
        VBox column = new VBox(CASCADE_SPACING);
        column.setAlignment(Pos.TOP_CENTER);
        column.setFillWidth(false);
        column.setMinWidth(CARD_WIDTH);
        column.setPrefWidth(CARD_WIDTH);
        column.setMaxWidth(CARD_WIDTH);
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
