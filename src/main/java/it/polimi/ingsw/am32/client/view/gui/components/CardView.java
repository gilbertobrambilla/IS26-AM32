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

public class CardView extends StackPane {

    private static final double HOVER_SCALE      = 1.1;
    private static final double SLIDE_UP_PX      = -20.0;
    private static final int    ANIMATION_MS     = 150;
    private static final double SHADOW_RADIUS_PX = 20.0;

    private final ImageView imageView = new ImageView();
    private final String cardId;
    private boolean slideUpEnabled = false;

    public CardView(String cardId) {
        this.cardId = cardId;
        initLayout();
        loadImage();
        setupHoverEffects();
    }

    public CardView(String cardId, double initialWidth, double initialHeight) {
        this.cardId = cardId;
        initLayout();
        setPrefSize(initialWidth, initialHeight);
        loadImage();
        setupHoverEffects();
    }

    public void setSlideUpEnabled(boolean enabled) {
        this.slideUpEnabled = enabled;
    }

    public String getCardId() {
        return cardId;
    }

    public void setHighlight(boolean selected) {
        if (selected) {
            if (!getStyleClass().contains("card-selected")) {
                getStyleClass().add("card-selected");
            }
        } else {
            getStyleClass().remove("card-selected");
        }
    }

    private void initLayout() {
        getStyleClass().add("card-view");

        // LA SOLUZIONE AL GIGANTISMO: sblocco della dimensione minima
        setMinSize(0, 0);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        // Binding responsivo
        imageView.fitWidthProperty().bind(widthProperty());
        imageView.fitHeightProperty().bind(heightProperty());

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

    private String resolveAssetPath() {
        if (cardId == null) return resolveCardBack();
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
        return null;
    }

    private String resolveCardBack() {
        return AssetManager.getInstance().getCardBack("card_back");
    }

    private static String normalizeId(String rawId) {
        return rawId.replace("char_",  "character_").replace("build_", "building_");
    }

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