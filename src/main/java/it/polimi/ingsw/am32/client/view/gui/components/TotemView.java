package it.polimi.ingsw.am32.client.view.gui.components;

import it.polimi.ingsw.am32.client.view.gui.utils.ImageCache;
import it.polimi.ingsw.am32.model.enums.TotemColor;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * A JavaFX totem marker is represented with sprite and color fallback support.
 */
public class TotemView extends StackPane {

    private static final String TOTEM_IMAGE_PATH_PATTERN = "/assets/images/totems/totem_%s.png";
    private final ImageView imageView = new ImageView();


    public TotemView(TotemColor color) {
        this(color, false);
    }


    public TotemView(TotemColor color, boolean cropTop) {

        setMinSize(0, 0);


        if (cropTop) {
            setMaxSize(20.0, 20.0);
        } else {
            setMaxSize(45.0, 45.0);
        }

        setPrefSize(30.0, 30.0);

        if (!loadTotemSprite(color, cropTop)) {
            getChildren().add(buildColorFallback(color));
        } else {
            getChildren().add(imageView);
        }
    }

    /**
     * The totem sprite is loaded and optionally cropped.
     *
     * @param color the totem color
     * @param cropTop true if only the upper sprite portion should be shown, false otherwise
     * @return true if the sprite was loaded, false otherwise
     */
    private boolean loadTotemSprite(TotemColor color, boolean cropTop) {
        if (color == null) return false;
        String path = String.format(TOTEM_IMAGE_PATH_PATTERN, color.name().toLowerCase());
        Image image = ImageCache.getInstance().getImage(path);

        if (image == null) return false;

        if (cropTop) {
            double width = image.getWidth();

            double cropHeight = width * 0.75;

            if (cropHeight > image.getHeight()) {
                cropHeight = image.getHeight();
            }

            imageView.setViewport(new Rectangle2D(0, 0, width, cropHeight));
        }

        imageView.setImage(image);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.fitWidthProperty().bind(widthProperty());
        imageView.fitHeightProperty().bind(heightProperty());
        return true;
    }

    /**
     * A circular color fallback is built when no sprite is available.
     *
     * @param color the totem color
     * @return the fallback marker
     */
    private static Circle buildColorFallback(TotemColor color) {
        Circle marker = new Circle();
        marker.radiusProperty().bind(marker.parentProperty().map(p -> ((StackPane)p).heightProperty().doubleValue() / 2.0).orElse(13.0));
        marker.setFill(resolveColor(color));
        marker.setStroke(Color.web("#f0f0f0"));
        marker.setStrokeWidth(1.5);
        return marker;
    }

    /**
     * The JavaFX color associated with a totem color is resolved.
     *
     * @param color the totem color
     * @return the javafx color to display
     */
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
}
