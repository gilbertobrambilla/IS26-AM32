package it.polimi.ingsw.am32.client.view.gui.components;

import it.polimi.ingsw.am32.client.model.LightPlayer;
import it.polimi.ingsw.am32.model.enums.TotemColor;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * A compact JavaFX summary of an opponent's state is represented.
 *
 * <p>The player's nickname, totem icon, food counter, and prestige counter are
 * shown. The component fills the full width of its parent container.</p>
 *
 * <p>The {@link TotemView} is rebuilt only when the player's {@link TotemColor}
 * changes. Repeated calls to {@link #updateValues(LightPlayer)} therefore
 * allocate no new nodes.</p>
 *
 * <p>All visual styling is driven by CSS classes defined in {@code global.css}.
 * No inline {@code setStyle()} calls are used.</p>
 */
public class OpponentView extends HBox {

    private static final double ROW_HEIGHT = 70.0;

    private final Label nameLabel     = new Label();
    private final Label foodLabel     = new Label();
    private final Label prestigeLabel = new Label();
    private final HBox  totemSlot     = new HBox();

    /** The last rendered color is tracked to avoid rebuilding the totem view unnecessarily. */
    private TotemColor lastTotemColor = null;

    /**
     * An opponent view is constructed and populated with the player's initial state.
     *
     * @param player the light model containing the opponent's data
     */
    public OpponentView(LightPlayer player) {
        initLayout();
        updateValues(player);
    }

    /**
     * All displayed values are refreshed from the latest player state.
     * The totem view is rebuilt only when the player's color has changed.
     *
     * @param player the updated player model; no-op if {@code null}
     */
    public void updateValues(LightPlayer player) {
        if (player == null) return;

        nameLabel.setText(player.getNickname());
        foodLabel.setText(String.valueOf(player.getFood()));
        prestigeLabel.setText(String.valueOf(player.getPrestige()));

        TotemColor color = player.getTotemColor();
        if (color != lastTotemColor) {
            lastTotemColor = color;
            totemSlot.getChildren().clear();
            if (color != null) {
                totemSlot.getChildren().add(new TotemView(color, player.getNickname()));
            }
        }
    }

    // -------------------------------------------------------------------------
    // layout
    // -------------------------------------------------------------------------

    private void initLayout() {
        setMinHeight(ROW_HEIGHT);
        setPrefHeight(ROW_HEIGHT);
        setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(this, Priority.ALWAYS);

        getStyleClass().add("opponent-card");
        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(10.0));

        getChildren().addAll(buildIdentityBox(), buildSpacer(), buildStatsBox());
    }

    private VBox buildIdentityBox() {
        // opponent-name CSS class sets font weight, size, and fill
        nameLabel.getStyleClass().addAll("text-main", "opponent-name");

        totemSlot.setAlignment(Pos.CENTER_LEFT);

        VBox box = new VBox(4.0);
        box.setAlignment(Pos.CENTER_LEFT);
        box.getChildren().addAll(nameLabel, totemSlot);
        return box;
    }

    private static Region buildSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    private VBox buildStatsBox() {
        HBox foodRow     = buildStatRow("Food:",     foodLabel,     "text-main");
        HBox prestigeRow = buildStatRow("Prestige:", prestigeLabel, "text-gold");

        VBox box = new VBox(4.0);
        box.setAlignment(Pos.CENTER_RIGHT);
        box.getChildren().addAll(foodRow, prestigeRow);
        return box;
    }

    /**
     * A {@code caption : value} row is built for the stats panel.
     *
     * @param caption static text (e.g. {@code "Food:"})
     * @param valueLabel the mutable label whose text is updated by {@link #updateValues}
     * @param colorClass CSS utility class applied to the value label (e.g. {@code "text-gold"})
     */
    private static HBox buildStatRow(String caption, Label valueLabel, String colorClass) {
        Label captionLabel = new Label(caption);
        captionLabel.getStyleClass().add("text-dim");

        // stat-value CSS class sets bold font weight
        // color class is applied separately
        valueLabel.getStyleClass().addAll("stat-value", colorClass);

        HBox row = new HBox(6.0);
        row.setAlignment(Pos.CENTER_RIGHT);
        row.getChildren().addAll(captionLabel, valueLabel);
        return row;
    }
}
