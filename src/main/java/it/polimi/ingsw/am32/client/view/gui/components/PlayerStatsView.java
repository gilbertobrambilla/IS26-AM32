package it.polimi.ingsw.am32.client.view.gui.components;

import it.polimi.ingsw.am32.client.model.LightPlayer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * Custom JavaFX Node representing the local player's detailed statistics.
 * It is displayed in the bottom-left corner of the In-Game view.
 */
public class PlayerStatsView extends VBox {

    private final LightPlayer playerData;

    private final Label prestigeLabel;
    private final Label foodLabel;
    private final Label totemColorLabel;

    /**
     * Constructs a new PlayerStatsView.
     * @param player The LightPlayer representing the local user.
     */
    public PlayerStatsView(LightPlayer player) {
        this.playerData = player;

        this.prestigeLabel = new Label();
        this.foodLabel = new Label();
        this.totemColorLabel = new Label();

        setupUI();
        updateStats(player);
    }

    /**
     * Sets up the UI layout using a GridPane for neat alignment.
     */
    private void setupUI() {
        this.setSpacing(15.0);
        this.setAlignment(Pos.TOP_LEFT);
        this.setPadding(new Insets(10.0));

        // Using a GridPane for perfect column alignment of labels and values
        GridPane grid = new GridPane();
        grid.setHgap(20.0);
        grid.setVgap(10.0);
        grid.setAlignment(Pos.CENTER_LEFT);

        // Titles
        Label prestigeTitle = new Label("Prestige Points:");
        prestigeTitle.getStyleClass().add("subtitle-text");

        Label foodTitle = new Label("Food:");
        foodTitle.getStyleClass().add("subtitle-text");

        Label totemTitle = new Label("Totem Color:");
        totemTitle.getStyleClass().add("subtitle-text");

        // Values Styling
        prestigeLabel.getStyleClass().addAll("title-text", "text-gold");
        foodLabel.getStyleClass().addAll("title-text", "text-dim");
        totemColorLabel.getStyleClass().addAll("title-text", "text-main");

        // Add to grid (column, row)
        grid.add(prestigeTitle, 0, 0);
        grid.add(prestigeLabel, 1, 0);

        grid.add(foodTitle, 0, 1);
        grid.add(foodLabel, 1, 1);

        grid.add(totemTitle, 0, 2);
        grid.add(totemColorLabel, 1, 2);

        this.getChildren().add(grid);
    }

    /**
     * Updates the displayed values based on the player's current state.
     * @param data The updated LightPlayer.
     */
    public void updateStats(LightPlayer data) {
        prestigeLabel.setText(String.valueOf(data.getPrestige()));
        foodLabel.setText(String.valueOf(data.getFood()));

        if (data.getTotemColor() != null) {
            totemColorLabel.setText(data.getTotemColor().toString());
        } else {
            totemColorLabel.setText("NONE");
        }
    }
}
