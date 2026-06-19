package it.polimi.ingsw.am32.client.view.gui.components;

import it.polimi.ingsw.am32.client.assets.AssetManager;
import it.polimi.ingsw.am32.client.view.gui.utils.GuiLogger;
import it.polimi.ingsw.am32.client.view.gui.utils.ImageCache;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Cursor;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * A JavaFX card component backed by game card assets is represented.
 */
public class CardView extends StackPane {

    private static final double DEFAULT_WIDTH    = 80.0;
    private static final double DEFAULT_HEIGHT   = 120.0;
    private static final double HOVER_SCALE      = 1.1;
    private static final double SLIDE_UP_PX      = -5.0;
    private static final int    ANIMATION_MS     = 150;
    private static final double SHADOW_RADIUS_PX = 20.0;

    private final StackPane innerCard = new StackPane();
    private final ImageView imageView = new ImageView();
    private final String cardId;
    private final double cardWidth;
    private final double cardHeight;
    private boolean slideUpEnabled = false;

    /**
     * A card view with default dimensions is created.
     *
     * @param cardId the card identifier, or null for the default back
     */
    public CardView(String cardId) {
        this(cardId, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * A card view with explicit dimensions is created.
     *
     * @param cardId the card identifier, or null for the default back
     * @param cardWidth the preferred card width
     * @param cardHeight the preferred card height
     */
    public CardView(String cardId, double cardWidth, double cardHeight) {
        this.cardId = cardId;
        this.cardWidth = cardWidth;
        this.cardHeight = cardHeight;

        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        innerCard.getChildren().add(imageView);
        innerCard.getStyleClass().add("card-view");

        StackPane.setAlignment(innerCard, javafx.geometry.Pos.CENTER);

        getChildren().add(innerCard);

        setFluid(false);

        loadImage();
        setupHoverEffects();
    }

    /**
     * Fluid sizing is enabled or disabled for the card view.
     *
     * @param fluid true if the card should adapt to its parent, false otherwise
     */
    public void setFluid(boolean fluid) {
        if (fluid) {
            setMinSize(0, 0);
            setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            HBox.setHgrow(this, Priority.ALWAYS);
            VBox.setVgrow(this, Priority.ALWAYS);

            imageView.fitWidthProperty().unbind();
            imageView.fitHeightProperty().bind(heightProperty());

            innerCard.maxWidthProperty().bind(heightProperty().multiply(cardWidth / cardHeight));
            innerCard.maxHeightProperty().bind(heightProperty());

        } else {
            imageView.fitWidthProperty().unbind();
            imageView.fitHeightProperty().unbind();

            innerCard.maxWidthProperty().unbind();
            innerCard.maxHeightProperty().unbind();

            setPrefSize(cardWidth, cardHeight);
            setMinSize(cardWidth, cardHeight);
            setMaxSize(cardWidth, cardHeight);

            imageView.setFitWidth(cardWidth);
            imageView.setFitHeight(cardHeight);

            innerCard.setMaxSize(cardWidth, cardHeight);
        }
    }

    /**
     * The hover slide-up effect is enabled or disabled.
     *
     * @param enabled true if the card should slide up on hover, false otherwise
     */
    public void setSlideUpEnabled(boolean enabled) { this.slideUpEnabled = enabled; }

    /**
     * The card identifier represented by this view is returned.
     *
     * @return the card identifier
     */
    public String getCardId() { return cardId; }

    /**
     * The visual selected state is applied.
     *
     * @param selected true if the selected style class should be applied, false otherwise
     */
    public void setHighlight(boolean selected) {
        if (selected) {
            if (!innerCard.getStyleClass().contains("card-selected")) {
                innerCard.getStyleClass().add("card-selected");
            }
        } else {
            innerCard.getStyleClass().remove("card-selected");
        }
    }

    /**
     * The card image is loaded from the asset cache.
     */
    private void loadImage() {
        String path = resolveAssetPath();
        if (path == null) return;
        Image image = ImageCache.getInstance().getImage(path);
        if (image != null) imageView.setImage(image);
    }

    /**
     * The asset path associated with the card identifier is resolved.
     *
     * @return the classpath asset path, or null if no asset is available
     */
    private String resolveAssetPath() {
        if (cardId == null) return AssetManager.getInstance().getCardBack("card_back");
        String id = cardId.replace("char_",  "character_").replace("build_", "building_");
        if (id.startsWith("building")) {
            var dto = AssetManager.getInstance().getBuildingCard(id);
            return dto != null ? dto.getAssetPath() : null;
        } else if (id.startsWith("character")) {
            var dto = AssetManager.getInstance().getCharacterCard(id);
            return dto != null ? dto.getAssetPath() : null;
        } else if (id.startsWith("event")) {
            var dto = AssetManager.getInstance().getEventCard(id);
            return dto != null ? dto.getAssetPath() : null;
        }
        return null;
    }

    /**
     * Hover scale, shadow, and optional slide animations are configured.
     */
    private void setupHoverEffects() {
        ScaleTransition scaleIn   = new ScaleTransition(Duration.millis(ANIMATION_MS), innerCard);
        scaleIn.setToX(HOVER_SCALE); scaleIn.setToY(HOVER_SCALE);
        ScaleTransition scaleOut  = new ScaleTransition(Duration.millis(ANIMATION_MS), innerCard);
        scaleOut.setToX(1.0); scaleOut.setToY(1.0);
        TranslateTransition slideUp   = new TranslateTransition(Duration.millis(ANIMATION_MS), innerCard);
        slideUp.setToY(SLIDE_UP_PX);
        TranslateTransition slideDown = new TranslateTransition(Duration.millis(ANIMATION_MS), innerCard);
        slideDown.setToY(0.0);
        DropShadow shadow = new DropShadow(SHADOW_RADIUS_PX, Color.BLACK);

        setOnMouseEntered(e -> {
            setCursor(Cursor.HAND); innerCard.setEffect(shadow); scaleOut.stop(); scaleIn.playFromStart();
            if (slideUpEnabled) { this.setViewOrder(-1.0); slideDown.stop(); slideUp.playFromStart(); }
        });
        setOnMouseExited(e -> {
            setCursor(Cursor.DEFAULT); innerCard.setEffect(null); scaleIn.stop(); scaleOut.playFromStart();
            if (slideUpEnabled) { this.setViewOrder(0.0); slideUp.stop(); slideDown.playFromStart(); }
        });
    }
}
