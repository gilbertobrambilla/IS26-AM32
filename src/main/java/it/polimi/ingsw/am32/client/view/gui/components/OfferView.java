package it.polimi.ingsw.am32.client.view.gui.components;

import it.polimi.ingsw.am32.client.model.LightOfferTile;
import it.polimi.ingsw.am32.client.view.gui.utils.ImageCache;
import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * Custom JavaFX component representing an Offer Tile on the board.
 * It manages the background image rendering and the dynamic overlay
 * showing if a player's totem is currently placed on it.
 */
public class OfferView extends StackPane {

    private final LightOfferTile tileData;
    private final ImageView backgroundImageView;
    private final StackPane totemOverlay;

    // Standard dimensions for tiles
    private static final double TILE_WIDTH = 100.0;
    private static final double TILE_HEIGHT = 100.0;
    private static final double HOVER_SCALE = 1.08;
    private static final int ANIMATION_DURATION_MS = 150;
    private static final Color TOTEM_COLOR = Color.web("#4a5d23");
    /**
     * Constructs a new OfferView based on the provided data model.
     *
     * @param tileData The state of the offer tile.
     */
    public OfferView(LightOfferTile tileData) {
        this.tileData = tileData;
        this.backgroundImageView = new ImageView();
        this.totemOverlay = new StackPane();

        initializeComponent();
        loadTileImage();
        renderTotemState();
        setupInteractionEffects();
    }

    /**
     * Sets up the geometry and layers of the StackPane.
     */
    private void initializeComponent() {
        this.setPrefSize(TILE_WIDTH, TILE_HEIGHT);
        this.setMinSize(TILE_WIDTH, TILE_HEIGHT);
        this.setMaxSize(TILE_WIDTH, TILE_HEIGHT);

        backgroundImageView.setFitWidth(TILE_WIDTH);
        backgroundImageView.setFitHeight(TILE_HEIGHT);
        backgroundImageView.setPreserveRatio(true);
        backgroundImageView.setSmooth(true);

        // Ensure the totem indicator is centered over the tile
        totemOverlay.setAlignment(Pos.CENTER);

        // Z-Index: Background first, Overlay on top
        this.getChildren().addAll(backgroundImageView, totemOverlay);
    }

    /**
     * Loads the specific tile PNG using the Flyweight pattern.
     */
    private void loadTileImage() {
        if (tileData != null && tileData.getId() != null) {
            Image image = ImageCache.getInstance().getImage(tileData.getId());
            if (image != null) {
                backgroundImageView.setImage(image);
            } else {
                System.err.println("[GUI] Missing asset for offer tile ID: " + tileData.getId());
            }
        }
    }

    /**
     * Updates the overlay to display a player's totem if one is placed here.
     */
    public void renderTotemState() {
        totemOverlay.getChildren().clear();

        String occupyingPlayer = tileData.getOccupyingPlayer();

        if (occupyingPlayer != null && !occupyingPlayer.isEmpty()) {
            Circle totemShape = new Circle(20, TOTEM_COLOR);
            totemShape.setStroke(Color.WHITE);
            totemShape.setStrokeWidth(2);

            Label nameLabel = new Label(occupyingPlayer.substring(0, Math.min(occupyingPlayer.length(), 3)).toUpperCase());
            nameLabel.setTextFill(Color.WHITE);
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 10px;");

            totemOverlay.getChildren().addAll(totemShape, nameLabel);

            this.setDisable(true);
            this.setOpacity(0.8);
        } else {
            this.setDisable(false);
            this.setOpacity(1.0);
        }
    }

    /**
     * Sets up hover animations, active only if the tile is unoccupied.
     */
    private void setupInteractionEffects() {
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(ANIMATION_DURATION_MS), this);
        scaleIn.setToX(HOVER_SCALE);
        scaleIn.setToY(HOVER_SCALE);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(ANIMATION_DURATION_MS), this);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);

        DropShadow shadow = new DropShadow(10, Color.BLACK);

        this.setOnMouseEntered(e -> {
            if (!this.isDisabled()) {
                this.setCursor(Cursor.HAND);
                this.setEffect(shadow);
                scaleOut.stop();
                scaleIn.playFromStart();
            }
        });

        this.setOnMouseExited(e -> {
            if (!this.isDisabled()) {
                this.setCursor(Cursor.DEFAULT);
                this.setEffect(null);
                scaleIn.stop();
                scaleOut.playFromStart();
            }
        });
    }

    /**
     * @return The underlying tile data.
     */
    public LightOfferTile getTileData() {
        return tileData;
    }
}
