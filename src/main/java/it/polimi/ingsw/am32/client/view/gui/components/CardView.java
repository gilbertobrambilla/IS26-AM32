package it.polimi.ingsw.am32.client.view.gui.components;

import it.polimi.ingsw.am32.client.assets.AssetManager;
import it.polimi.ingsw.am32.client.view.gui.utils.ImageCache;
import javafx.animation.ScaleTransition;
import javafx.scene.Cursor;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import java.util.Optional;

/**
 * Custom JavaFX component representing a game card (Tribe or Building).
 * It uses the Flyweight pattern through ImageCache to manage memory efficiently
 * and provides smooth visual feedback for user interactions.
 */
public class CardView extends StackPane {

    private final ImageView imageView;
    private final Optional<String> cardIdFallback;

    // UI Constants (Aligned with global.css)
    private static final double DEFAULT_WIDTH = 120.0;
    private static final double DEFAULT_HEIGHT = 180.0;
    private static final double HOVER_SCALE = 1.1;
    private static final int ANIMATION_DURATION_MS = 150;

    /**
     * Constructs a CardView from a given DTO.
     * @param cardId The DTO containing card information, specifically the ID for image lookup.
     */
    public CardView(String cardId) {
        this.cardIdFallback = Optional.ofNullable(cardId);
        this.imageView = new ImageView();

        initializeComponent();
        loadCardImage();
        setupInteractionEffects();
    }

    /**
     * Sets up the basic layout properties and CSS classes.
     */
    private void initializeComponent() {
        // Set dimensions
        this.setPrefSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.setMinSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.setMaxSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

        // Styling
        this.getStyleClass().add("card-view");

        // Image scaling properties
        imageView.setFitWidth(DEFAULT_WIDTH);
        imageView.setFitHeight(DEFAULT_HEIGHT);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        this.getChildren().add(imageView);
    }

    /**
     * Fetches the image from the singleton ImageCache using the card's ID.
     */
    private void loadCardImage() {
        cardIdFallback.ifPresentOrElse(
                id -> {
                    String validId = normalizeId(id);
                    String resolvedPath = AssetManager.getInstance().getUiAsset(validId);
                    if (resolvedPath == null) {
                        System.err.println("[CardView] No asset path for id: " + validId);
                        return;
                    }
                    Image image = ImageCache.getInstance().getImage(resolvedPath);
                    if (image != null) {
                        imageView.setImage(image);
                    } else {
                        System.err.println("[CardView] Missing asset for card id: " + validId);
                    }
                },
                () -> {
                    System.out.println("[CardView] Card id is absent. Rendering card back.");
                    Image backImage = ImageCache.getInstance().getImage("card_back");
                    if (backImage != null) {
                        imageView.setImage(backImage);
                    }
                }
        );
    }

    /**
     * Configures animations and cursor changes for mouse hover events.
     */
    private void setupInteractionEffects() {
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(ANIMATION_DURATION_MS), this);
        scaleIn.setToX(HOVER_SCALE);
        scaleIn.setToY(HOVER_SCALE);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(ANIMATION_DURATION_MS), this);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);

        DropShadow shadow = new DropShadow(15, Color.BLACK);
        shadow.setRadius(20);

        this.setOnMouseEntered(e -> {
            this.setCursor(Cursor.HAND);
            this.setEffect(shadow);
            scaleOut.stop();
            scaleIn.playFromStart();
        });

        this.setOnMouseExited(e -> {
            this.setCursor(Cursor.DEFAULT);
            this.setEffect(null);
            scaleIn.stop();
            scaleOut.playFromStart();
        });
    }

    /**
     * Gets the data associated with this card view.
     * @return The CardDTO object.
     */

    public Optional<String> getCardId() {
        return cardIdFallback;
    }

    /**
     * Highlights the card (e.g., when selected for an action).
     * @param selected True to apply highlight, false to remove.
     */
    public void setHighlight(boolean selected) {
        if (selected) {
            if (!this.getStyleClass().contains("card-selected")) {
                this.getStyleClass().add("card-selected");
            }
        } else {
            this.getStyleClass().remove("card-selected");
        }
    }

    private String normalizeId(String rawId) {
        if (rawId == null) return null;
        return rawId
                .replace("char_", "character_")
                .replace("build_", "building_");
    }
}
