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

public class TotemView extends StackPane {

    private static final String TOTEM_IMAGE_PATH_PATTERN = "/assets/images/totems/totem_%s.png";
    private final ImageView imageView = new ImageView();


    public TotemView(TotemColor color, String nickname) {
        this(color, nickname, false);
    }


    public TotemView(TotemColor color, String nickname, boolean cropTop) {

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

    private static Circle buildColorFallback(TotemColor color) {
        Circle marker = new Circle();
        marker.radiusProperty().bind(marker.parentProperty().map(p -> ((StackPane)p).heightProperty().doubleValue() / 2.0).orElse(13.0));
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

    private static Label buildInitialsLabel(String nickname) {
        String initials = nickname.length() <= 2 ? nickname.toUpperCase() : nickname.substring(0, 2).toUpperCase();
        Label label = new Label(initials);
        label.getStyleClass().add("totem-label");
        return label;
    }
}