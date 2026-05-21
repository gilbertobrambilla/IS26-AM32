package it.polimi.ingsw.am32.client.view.gui.components;

import it.polimi.ingsw.am32.client.view.gui.utils.ImageCache;
import it.polimi.ingsw.am32.client.view.gui.utils.GuiLogger;
import it.polimi.ingsw.am32.model.enums.TotemColor;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Custom JavaFX component representing a player's totem marker.
 *
 * <p>Automatically crops the full totem sprite to display only its square head
 * (top portion equal to the image width) and overlays the player's two-letter
 * initials for additional readability.</p>
 *
 * <p><em>Note on asset loading:</em> totem images are not registered in
 * {@code AssetManager} / {@code assets.json}; they are loaded directly via a
 * conventional path pattern. If totems are added to the asset registry in future,
 * {@link #TOTEM_IMAGE_PATH_PATTERN} should be replaced by an {@code AssetManager}
 * lookup.</p>
 */
public class TotemView extends StackPane {

    private static final double TOTEM_SIZE = 26.0;

    /**
     * Classpath path pattern for totem images.
     * {@code %s} is replaced by the lowercase color name (e.g. {@code "red"}).
     */
    private static final String TOTEM_IMAGE_PATH_PATTERN = "/assets/images/totems/totem_%s.png";

    private final ImageView imageView = new ImageView();

    /**
     * Constructs a TotemView for the given player color and nickname.
     *
     * @param color    the player's totem color; {@code null} renders an empty view
     * @param nickname the player's name; used only to generate initials overlay
     */
    public TotemView(TotemColor color, String nickname) {
        setPrefSize(TOTEM_SIZE, TOTEM_SIZE);
        setMinSize(TOTEM_SIZE, TOTEM_SIZE);
        setMaxSize(TOTEM_SIZE, TOTEM_SIZE);

        if (!loadTotemSprite(color)) {
            getChildren().add(buildColorFallback(color));
        } else {
            getChildren().add(imageView);
        }

        if (nickname != null && !nickname.isEmpty()) {
            getChildren().add(buildInitialsLabel(nickname));
        }
    }

    // --- private helpers ---

    /**
     * Loads the totem sprite for the given color and crops it to a square head region.
     */
    private boolean loadTotemSprite(TotemColor color) {
        if (color == null) return false;

        String path = String.format(TOTEM_IMAGE_PATH_PATTERN, color.name().toLowerCase());
        Image image = ImageCache.getInstance().getImage(path);

        if (image == null) {
            GuiLogger.warning("[TotemView] sprite not found for color: " + color);
            return false;
        }

        // The totem texture is taller than it is wide; we crop a square from the top
        // so only the head is visible, discarding the elongated body below.
        double width = image.getWidth();
        imageView.setViewport(new Rectangle2D(0, 0, width, width));
        imageView.setImage(image);
        imageView.setFitWidth(TOTEM_SIZE);
        imageView.setFitHeight(TOTEM_SIZE);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        return true;
    }

    private static Circle buildColorFallback(TotemColor color) {
        Circle marker = new Circle(TOTEM_SIZE / 2.0);
        marker.setFill(resolveColor(color));
        marker.setStroke(Color.web("#f0f0f0"));
        marker.setStrokeWidth(1.5);
        return marker;
    }

    private static Color resolveColor(TotemColor color) {
        if (color == null) return Color.GRAY;

        return switch (color) {
            case RED -> Color.web("#c62828");
            case BLUE -> Color.web("#1565c0");
            case YELLOW -> Color.web("#f9a825");
            case WHITE -> Color.web("#eeeeee");
            case BLACK -> Color.web("#212121");
        };
    }

    /**
     * Builds the initials overlay label using the {@code totem-label} CSS class.
     */
    private static Label buildInitialsLabel(String nickname) {
        String initials = nickname.length() <= 2
                ? nickname.toUpperCase()
                : nickname.substring(0, 2).toUpperCase();

        Label label = new Label(initials);
        label.getStyleClass().add("totem-label");
        return label;
    }
}
