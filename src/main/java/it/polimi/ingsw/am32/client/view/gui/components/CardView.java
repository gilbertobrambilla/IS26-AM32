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
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Custom JavaFX component representing a single game card (Tribe card, Building card,
 * or Event card).
 *
 * <p>Uses {@link ImageCache} (Flyweight) for memory-efficient image loading.
 * When constructed with a {@code null} id the card renders its back face.</p>
 *
 * <p>Hover feedback consists of a scale-up animation; an optional vertical slide-up
 * can be enabled for hand/dashboard cards via {@link #setSlideUpEnabled(boolean)}.</p>
 */
public class CardView extends StackPane {

    // --- layout ---
    private static final double DEFAULT_WIDTH    = 80.0;
    private static final double DEFAULT_HEIGHT   = 120.0;

    // --- hover animation ---
    private static final double HOVER_SCALE      = 1.1;
    private static final double SLIDE_UP_PX      = -40.0;   // negative = upward
    private static final int    ANIMATION_MS     = 150;
    private static final double SHADOW_RADIUS_PX = 20.0;

    // ---

    private final ImageView imageView = new ImageView();

    /**
     * The card identifier used to look up assets.
     * {@code null} signals "render card back".
     */
    private final String cardId;

    private final double cardWidth;
    private final double cardHeight;
    private boolean slideUpEnabled = false;

    // --- constructors ---

    public CardView(String cardId) {
        this(cardId, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public CardView(String cardId, double cardWidth, double cardHeight) {
        this.cardId    = cardId;
        this.cardWidth = cardWidth;
        this.cardHeight = cardHeight;
        initLayout();
        loadImage();
        setupHoverEffects();
    }

    // --- public API ---

    /**
     * Enables or disables the vertical slide-up animation on hover.
     * Should be {@code true} for cards displayed in the player's hand.
     *
     * @param enabled {@code true} to slide the card upward on hover
     */
    public void setSlideUpEnabled(boolean enabled) {
        this.slideUpEnabled = enabled;
    }

    /**
     * Returns the card identifier, or {@code null} if this view shows the card back.
     */
    public String getCardId() {
        return cardId;
    }

    /**
     * Applies or removes the golden selection highlight (CSS class {@code card-selected}).
     *
     * @param selected {@code true} to highlight, {@code false} to clear
     */
    public void setHighlight(boolean selected) {
        if (selected) {
            if (!getStyleClass().contains("card-selected")) {
                getStyleClass().add("card-selected");
            }
        } else {
            getStyleClass().remove("card-selected");
        }
    }

    // --- initialization ---

    private void initLayout() {
        setPrefSize(cardWidth, cardHeight);
        setMinSize(cardWidth, cardHeight);
        setMaxSize(cardWidth, cardHeight);
        getStyleClass().add("card-view");

        imageView.setFitWidth(cardWidth);
        imageView.setFitHeight(cardHeight);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        getChildren().add(imageView);
    }

    private void loadImage() {
        String path = resolveAssetPath();
        if (path == null) return;

        Image image = ImageCache.getInstance().getImage(path);
        if (image == null) {
            GuiLogger.warning("[CardView] image load failed for path: " + path);
            return;
        }
        imageView.setImage(image);
    }

    /**
     * Resolves the classpath image path for this card's {@link #cardId}.
     * Falls back to the card-back asset when {@code cardId} is {@code null}.
     *
     * @return the resolved path, or {@code null} if no asset was found
     */
    private String resolveAssetPath() {
        if (cardId == null) {
            return resolveCardBack();
        }

        String id = normalizeId(cardId);

        if (id.startsWith("building")) {
            var dto = AssetManager.getInstance().getBuildingCard(id);
            if (dto != null) return dto.getAssetPath();

        } else if (id.startsWith("character")) {
            var dto = AssetManager.getInstance().getCharacterCard(id);
            if (dto != null) return dto.getAssetPath();

        } else if (id.startsWith("event")) {
            var dto = AssetManager.getInstance().getEventCard(id);
            if (dto != null) return dto.getAssetPath();
        }

        GuiLogger.warning("[CardView] no asset found for id: " + id);
        return null;
    }

    private String resolveCardBack() {
        String path = AssetManager.getInstance().getCardBack("card_back");
        if (path == null) {
            GuiLogger.warning("[CardView] card-back asset not registered in AssetManager");
        }
        return path;
    }

    /**
     * Normalises legacy id prefixes sent by the server that differ from the asset-registry
     * convention used in {@code assets.json}.
     *
     * <p><em>Note:</em> this translation is a transitional shim. The correct long-term fix
     * is to align the server DTO id format with the asset registry so the view layer
     * does not need to know about id conventions.</p>
     */
    private static String normalizeId(String rawId) {
        return rawId
                .replace("char_",  "character_")
                .replace("build_", "building_");
    }

    // --- hover / interaction ---

    private void setupHoverEffects() {
        ScaleTransition scaleIn   = buildScale(HOVER_SCALE);
        ScaleTransition scaleOut  = buildScale(1.0);
        TranslateTransition slideUp   = buildTranslateY(SLIDE_UP_PX);
        TranslateTransition slideDown = buildTranslateY(0.0);
        DropShadow shadow = new DropShadow(SHADOW_RADIUS_PX, Color.BLACK);

        setOnMouseEntered(e -> {
            setCursor(Cursor.HAND);
            setEffect(shadow);
            scaleOut.stop();
            scaleIn.playFromStart();
            if (slideUpEnabled) {
                setViewOrder(-1.0);
                slideDown.stop();
                slideUp.playFromStart();
            }
        });

        setOnMouseExited(e -> {
            setCursor(Cursor.DEFAULT);
            setEffect(null);
            scaleIn.stop();
            scaleOut.playFromStart();
            if (slideUpEnabled) {
                setViewOrder(0.0);
                slideUp.stop();
                slideDown.playFromStart();
            }
        });
    }

    private ScaleTransition buildScale(double target) {
        ScaleTransition t = new ScaleTransition(Duration.millis(ANIMATION_MS), this);
        t.setToX(target);
        t.setToY(target);
        return t;
    }

    private TranslateTransition buildTranslateY(double toY) {
        TranslateTransition t = new TranslateTransition(Duration.millis(ANIMATION_MS), this);
        t.setToY(toY);
        return t;
    }
}
