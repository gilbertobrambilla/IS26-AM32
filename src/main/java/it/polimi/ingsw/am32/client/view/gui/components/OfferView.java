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
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class OfferView extends StackPane {

    private LightOfferTile tileData;
    private final ImageView backgroundImageView;
    private final StackPane totemOverlay;

    private static final double HOVER_SCALE = 1.08;
    private static final int ANIMATION_DURATION_MS = 150;

    public OfferView(LightOfferTile tileData, TotemColor totemColor) {
        this.tileData = tileData;
        this.backgroundImageView = new ImageView();
        this.totemOverlay = new StackPane();

        initializeComponent();
        loadTileImage();
        renderTotemState(totemColor);
        setupInteractionEffects();
    }

    private void initializeComponent() {
        // Sblocco dimensione minima
        setMinSize(0, 0);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Impostiamo una proporzione ideale
        setPrefSize(88.0, 88.0);

        backgroundImageView.setPreserveRatio(true);
        backgroundImageView.setSmooth(true);
        backgroundImageView.fitWidthProperty().bind(widthProperty());
        backgroundImageView.fitHeightProperty().bind(heightProperty());

        totemOverlay.setAlignment(Pos.CENTER);
        this.getChildren().addAll(backgroundImageView, totemOverlay);
    }

    private void loadTileImage() {
        if (tileData == null) return;
        var dto = AssetManager.getInstance().getOfferTile(tileData.getId());
        if (dto != null && dto.getAssetPath() != null) {
            Image img = ImageCache.getInstance().getImage(dto.getAssetPath());
            if (img != null) backgroundImageView.setImage(img);
        }
    }

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

    public void updateTile(LightOfferTile newTile, TotemColor totemColor) {
        this.tileData = newTile;
        renderTotemState(totemColor);
    }

    public LightOfferTile getTileData() {
        return tileData;
    }
}