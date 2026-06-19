package it.polimi.ingsw.am32.client.view.gui.components;

import it.polimi.ingsw.am32.client.assets.AssetManager;
import it.polimi.ingsw.am32.client.model.LightOfferTile;
import it.polimi.ingsw.am32.client.view.gui.utils.ImageCache;
import it.polimi.ingsw.am32.model.enums.TotemColor;
import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * A JavaFX offer tile component with optional occupying totem is represented.
 */
public class OfferView extends StackPane {

    private LightOfferTile tileData;
    private final ImageView backgroundImageView;
    private final StackPane totemOverlay;

    private static final double HOVER_SCALE = 1.08;
    private static final int ANIMATION_DURATION_MS = 150;

    /**
     * An offer tile view is created from the light tile model.
     *
     * @param tileData the light offer tile model
     * @param totemColor the occupying player's totem color, or null if free
     */
    public OfferView(LightOfferTile tileData, TotemColor totemColor) {
        this.tileData = tileData;
        this.backgroundImageView = new ImageView();
        this.totemOverlay = new StackPane();

        initializeComponent();
        loadTileImage();
        renderTotemState(totemColor);
        setupInteractionEffects();
    }

    /**
     * Base layout bindings and child nodes are initialized.
     */
    private void initializeComponent() {
        setMinSize(0, 0);
        setMaxSize(Double.MAX_VALUE, 120.0);
        HBox.setHgrow(this, Priority.ALWAYS);
        VBox.setVgrow(this, Priority.ALWAYS);

        backgroundImageView.setPreserveRatio(true);
        backgroundImageView.setSmooth(true);
        backgroundImageView.fitWidthProperty().bind(widthProperty());
        backgroundImageView.fitHeightProperty().bind(heightProperty());

        totemOverlay.setAlignment(Pos.CENTER);
        this.getChildren().addAll(backgroundImageView, totemOverlay);
    }

    /**
     * The tile background image is loaded from the asset cache.
     */
    private void loadTileImage() {
        if (tileData == null) return;
        var dto = AssetManager.getInstance().getOfferTile(tileData.getId());
        if (dto != null && dto.getAssetPath() != null) {
            Image img = ImageCache.getInstance().getImage(dto.getAssetPath());
            if (img != null) backgroundImageView.setImage(img);
        }
    }

    /**
     * The visible totem state of the offer tile is refreshed.
     *
     * @param totemColor the occupying player's totem color, or null if free
     */
    public void renderTotemState(TotemColor totemColor) {
        totemOverlay.getChildren().clear();
        String occupyingPlayer = tileData.getOccupyingPlayer();

        if (occupyingPlayer != null && !occupyingPlayer.isEmpty() && totemColor != null) {
            TotemView totem = new TotemView(totemColor);
            totem.setMaxSize(40.0, 40.0);
            StackPane.setAlignment(totem, javafx.geometry.Pos.TOP_CENTER);
            StackPane.setMargin(totem, new javafx.geometry.Insets(5.0, 0, 0, 0));
            Tooltip tooltip = new Tooltip(occupyingPlayer);
            tooltip.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            Tooltip.install(totem, tooltip);
            totemOverlay.getChildren().add(totem);
            this.setDisable(true);
            this.setOpacity(0.8);
        } else {
            this.setDisable(false);
            this.setOpacity(1.0);
        }
    }

    /**
     * Hover interaction effects are configured.
     */
    private void setupInteractionEffects() {
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(ANIMATION_DURATION_MS), this);
        scaleIn.setToX(HOVER_SCALE); scaleIn.setToY(HOVER_SCALE);
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(ANIMATION_DURATION_MS), this);
        scaleOut.setToX(1.0); scaleOut.setToY(1.0);

        this.setOnMouseEntered(e -> {
            if (!this.isDisable()) { this.setCursor(Cursor.HAND); scaleOut.stop(); scaleIn.playFromStart(); }
        });
        this.setOnMouseExited(e -> {
            this.setCursor(Cursor.DEFAULT); scaleIn.stop(); scaleOut.playFromStart();
        });
    }

    /**
     * The represented tile model and totem state are updated.
     *
     * @param newTile the updated light offer tile model
     * @param totemColor the occupying player's totem color, or null if free
     */
    public void updateTile(LightOfferTile newTile, TotemColor totemColor) {
        this.tileData = newTile;
        renderTotemState(totemColor);
    }

    /**
     * The light tile model represented by this view is returned.
     *
     * @return the light offer tile model
     */
    public LightOfferTile getTileData() { return tileData; }
}
