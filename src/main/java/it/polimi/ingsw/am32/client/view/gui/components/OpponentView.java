package it.polimi.ingsw.am32.client.view.gui.components;

import it.polimi.ingsw.am32.client.model.LightPlayer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Custom JavaFX Node representing a concise summary of an opponent's state.
 * It displays their nickname, resources (food, prestige), totem color,
 * and the total count of acquired cards.
 */
public class OpponentView extends VBox {

    private final LightPlayer opponentData;

    // UI Elements that require dynamic updates when the game state changes
    private final Label prestigeLabel;
    private final Label foodLabel;
    private final Label totemLabel;
    private final Label cardsSummaryLabel;

    /**
     * Constructs a new OpponentView using the provided data model.
     *
     * @param opponent The LightPlayer representing the opponent's current state.
     */
    public OpponentView(LightPlayer opponent) {
        this.opponentData = opponent;

        this.prestigeLabel = new Label();
        this.foodLabel = new Label();
        this.totemLabel = new Label();
        this.cardsSummaryLabel = new Label();

        setupUI();
        updateValues(opponent);
    }

    /**
     * Configures the internal layout and styles, relying on global.css classes
     * to avoid Magic Numbers for styling and maintaining a clean design.
     */
    private void setupUI() {
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(8.0);
        this.getStyleClass().add("game-panel");

        // Minor inline padding adjustment (can also be completely delegated to CSS)
        this.setPadding(new Insets(10.0, 15.0, 10.0, 15.0));
        this.setPrefWidth(200.0);

        // Player Name
        Label nameLabel = new Label(opponentData.getNickname());
        nameLabel.getStyleClass().add("title-text");

        // Stats rows
        HBox prestigeBox = createStatRow("Prestige:", prestigeLabel, "text-gold");
        HBox foodBox = createStatRow("Food:", foodLabel, "text-dim");
        HBox totemBox = createStatRow("Totem:", totemLabel, "text-main");

        // Label for the number of acquired cards
        cardsSummaryLabel.getStyleClass().add("subtitle-text");

        this.getChildren().addAll(nameLabel, prestigeBox, foodBox, totemBox, cardsSummaryLabel);
    }

    /**
     * Helper method to create consistent horizontal rows for player statistics.
     */
    private HBox createStatRow(String labelText, Label valueLabel, String valueStyleClass) {
        HBox row = new HBox(10.0);
        row.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label(labelText);
        title.getStyleClass().add("subtitle-text");

        valueLabel.getStyleClass().add(valueStyleClass);

        row.getChildren().addAll(title, valueLabel);
        return row;
    }

    /**
     * Updates the text values based on the model's current state.
     * This ensures the UI remains a "dumb" reflection of the LightPlayer data.
     *
     * @param data The updated LightPlayer state.
     */
    public void updateValues(LightPlayer data) {
        prestigeLabel.setText(String.valueOf(data.getPrestige()));
        foodLabel.setText(String.valueOf(data.getFood()));

        // Display the Totem Color assigned to this player
        if (data.getTotemColor() != null) {
            totemLabel.setText(data.getTotemColor().toString());
        } else {
            totemLabel.setText("N/A");
        }

        // Calculate the total number of acquired cards (Characters + Buildings)
        int totalCardsCount = data.getCharacterCards().size() + data.getBuildingCards().size();
        cardsSummaryLabel.setText("Cards acquired: " + totalCardsCount);
    }
}
