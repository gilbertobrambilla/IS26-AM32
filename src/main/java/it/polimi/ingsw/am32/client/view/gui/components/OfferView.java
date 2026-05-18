package it.polimi.ingsw.am32.client.view.gui.components;

import it.polimi.ingsw.am32.client.assets.AssetManager;
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
 * custom javafx component is implemented to represent an offer tile on the board.
 * background image rendering and dynamic overlay are managed.
 */
public class OfferView extends StackPane {

    private LightOfferTile tileData;
    private final ImageView backgroundImageView;
    private final StackPane totemOverlay;

    // standard dimensions for tiles are defined
    private static final double TILE_WIDTH = 100.0;
    private static final double TILE_HEIGHT = 100.0;
    private static final double HOVER_SCALE = 1.08;
    private static final int ANIMATION_DURATION_MS = 150;
    private static final Color TOTEM_COLOR = Color.web("#4a5d23");

    public OfferView(LightOfferTile tileData) {
        this.tileData = tileData;
        this.backgroundImageView = new ImageView();
        this.totemOverlay = new StackPane();

        // component is initialized
        initializeComponent();

        // tile image is loaded
        loadTileImage();

        // totem state is rendered
        renderTotemState();

        // interaction effects are configured
        setupInteractionEffects();
    }

    private void initializeComponent() {
        // layout constraints are set
        this.setPrefSize(TILE_WIDTH, TILE_HEIGHT);
        this.setMinSize(TILE_WIDTH, TILE_HEIGHT);
        this.setMaxSize(TILE_WIDTH, TILE_HEIGHT);

        // background image properties are configured
        backgroundImageView.setFitWidth(TILE_WIDTH);
        backgroundImageView.setFitHeight(TILE_HEIGHT);
        backgroundImageView.setPreserveRatio(true);
        backgroundImageView.setSmooth(true);

        // totem indicator is centered over the tile
        totemOverlay.setAlignment(Pos.CENTER);

        // layers are added to the stackpane (background first, overlay on top)
        this.getChildren().addAll(backgroundImageView, totemOverlay);
    }

    private void loadTileImage() {
        if (tileData != null && tileData.getId() != null) {
            String validId = normalizeId(tileData.getId());

            // correct dto is retrieved from asset manager
            var dto = AssetManager.getInstance().getOfferTile(validId);

            if (dto != null && dto.getAssetPath() != null) {
                String assetPath = dto.getAssetPath();
                // image is fetched from cache
                Image image = ImageCache.getInstance().getImage(assetPath);

                if (image != null) {
                    backgroundImageView.setImage(image);
                } else {
                    // error is logged if cache fails
                    System.err.println("[OfferView] imagecache failed to load asset for offer tile id: " + validId);;
                }
            } else {
                // error is logged if dto or path is missing
                System.err.println("[OfferView] missing asset path in assetmanager for offer tile id: " + validId);
            }
        }
    }

    public void renderTotemState() {
        // overlay children are cleared
        totemOverlay.getChildren().clear();

        String occupyingPlayer = tileData.getOccupyingPlayer();

        if (occupyingPlayer != null && !occupyingPlayer.isEmpty()) {
            // totem graphic is instantiated
            Circle totemShape = new Circle(20, TOTEM_COLOR);
            totemShape.setStroke(Color.WHITE);
            totemShape.setStrokeWidth(2);

            // player label is formatted and instantiated
            Label nameLabel = new Label(occupyingPlayer.substring(0, Math.min(occupyingPlayer.length(), 3)).toUpperCase());
            nameLabel.setTextFill(Color.WHITE);
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 10px;");

            // elements are added to the overlay
            totemOverlay.getChildren().addAll(totemShape, nameLabel);

            // tile interaction is disabled
            this.setDisable(true);
            this.setOpacity(0.8);
        } else {
            // tile interaction is enabled
            this.setDisable(false);
            this.setOpacity(1.0);
        }
    }

    private void setupInteractionEffects() {
        // animations are initialized
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(ANIMATION_DURATION_MS), this);
        scaleIn.setToX(HOVER_SCALE);
        scaleIn.setToY(HOVER_SCALE);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(ANIMATION_DURATION_MS), this);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);

        DropShadow shadow = new DropShadow(10, Color.BLACK);

        // hover enter event is assigned
        this.setOnMouseEntered(e -> {
            if (!this.isDisabled()) {
                this.setCursor(Cursor.HAND);
                this.setEffect(shadow);
                scaleOut.stop();
                scaleIn.playFromStart();
            }
        });

        // hover exit event is assigned
        this.setOnMouseExited(e -> {
            if (!this.isDisabled()) {
                this.setCursor(Cursor.DEFAULT);
                this.setEffect(null);
                scaleIn.stop();
                scaleOut.playFromStart();
            }
        });
    }

    public void updateTile(LightOfferTile newTile) {
        // tile data is updated
        this.tileData = newTile;
        // visual state is refreshed
        renderTotemState();
    }

    public LightOfferTile getTileData() {
        // tile data is returned
        return tileData;
    }

    private String normalizeId(String rawId) {
        if (rawId == null) return null;
        // id format is normalized
        return rawId.replace("offertile_", "offer_tile_");
    }
}
