package it.polimi.ingsw.am32.client.view.gui.components;

import it.polimi.ingsw.am32.client.assets.AssetManager;
import it.polimi.ingsw.am32.client.model.LightOfferTile;
import it.polimi.ingsw.am32.client.view.gui.utils.ImageCache;
import it.polimi.ingsw.am32.model.enums.TotemColor;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * Custom JavaFX component representing an offer tile on the game board.
 * Manages the display of the tile asset and the placement of the occupying player's totem.
 */
public class OfferView extends StackPane {

    private LightOfferTile tileData;
    private final ImageView backgroundImageView;
    private final StackPane totemOverlay;

    private static final double TILE_WIDTH = 88.0;
    private static final double TILE_HEIGHT = 88.0;
    private static final double HOVER_SCALE = 1.08;
    private static final int ANIMATION_DURATION_MS = 150;

    /**
     * Constructs an OfferView with the given tile data and the current occupying player's totem details.
     *
     * @param tileData    The light model representation of the offer tile.
     * @param totemColor  The color of the totem occupying this tile, or null if empty.
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
     * Initializes layout parameters, constraints, and hierarchy.
     */
    private void initializeComponent() {
        this.setPrefSize(TILE_WIDTH, TILE_HEIGHT);
        this.setMinSize(TILE_WIDTH, TILE_HEIGHT);
        this.setMaxSize(TILE_WIDTH, TILE_HEIGHT);

        backgroundImageView.setFitWidth(TILE_WIDTH);
        backgroundImageView.setFitHeight(TILE_HEIGHT);
        backgroundImageView.setPreserveRatio(true);
        backgroundImageView.setSmooth(true);

        totemOverlay.setAlignment(Pos.CENTER);

        this.getChildren().addAll(backgroundImageView, totemOverlay);
    }

    /**
     * Loads the background tile image from the cache based on the asset manager registry.
     */
    private void loadTileImage() {
        if (tileData == null) return;

        var dto = AssetManager.getInstance().getOfferTile(tileData.getId());
        if (dto != null && dto.getAssetPath() != null) {
            Image img = ImageCache.getInstance().getImage(dto.getAssetPath());
            if (img != null) {
                backgroundImageView.setImage(img);
            }
        }
    }

    /**
     * Renders the totem graphic over the tile if it is currently occupied.
     * Instantiates a square TotemView displaying the player's initials and binds a full-name tooltip.
     *
     * @param totemColor The color of the occupying totem, or null if the tile is free.
     */
    public void renderTotemState(TotemColor totemColor) {
        totemOverlay.getChildren().clear();

        String occupyingPlayer = tileData.getOccupyingPlayer();

        if (occupyingPlayer != null && !occupyingPlayer.isEmpty() && totemColor != null) {
            TotemView totem = new TotemView(totemColor, occupyingPlayer);

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
     * Sets up hover animations and cursor tracking for interactive tiles.
     */
    private void setupInteractionEffects() {
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(ANIMATION_DURATION_MS), this);
        scaleIn.setToX(HOVER_SCALE);
        scaleIn.setToY(HOVER_SCALE);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(ANIMATION_DURATION_MS), this);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);

        this.setOnMouseEntered(e -> {
            if (!this.isDisable()) {
                this.setCursor(Cursor.HAND);
                scaleOut.stop();
                scaleIn.playFromStart();
            }
        });

        this.setOnMouseExited(e -> {
            this.setCursor(Cursor.DEFAULT);
            scaleIn.stop();
            scaleOut.playFromStart();
        });
    }

    /**
     * Updates the data model backing this view and completely refreshes the totem rendering state.
     *
     * @param newTile    The updated data model for the tile.
     * @param totemColor The current color of the occupying totem, or null if empty.
     */
    public void updateTile(LightOfferTile newTile, TotemColor totemColor) {
        this.tileData = newTile;
        renderTotemState(totemColor);
    }

    /**
     * Gets the current data backing this view component.
     *
     * @return The LightOfferTile object.
     */
    public LightOfferTile getTileData() {
        return tileData;
    }
}
