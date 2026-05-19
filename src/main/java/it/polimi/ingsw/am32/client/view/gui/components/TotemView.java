package it.polimi.ingsw.am32.client.view.gui.components;

import it.polimi.ingsw.am32.client.view.gui.utils.ImageCache;
import it.polimi.ingsw.am32.model.enums.TotemColor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class TotemView extends StackPane {

    // dimensions strictly enforced
    private static final double TOTEM_WIDTH = 30.0;
    private static final double TOTEM_HEIGHT = 15.0;

    private final ImageView imageView;

    public TotemView(TotemColor color) {
        this.imageView = new ImageView();

        // bounding box of the stackpane is strictly locked
        this.setPrefSize(TOTEM_WIDTH, TOTEM_HEIGHT);
        this.setMinSize(TOTEM_WIDTH, TOTEM_HEIGHT);
        this.setMaxSize(TOTEM_WIDTH, TOTEM_HEIGHT);

        this.imageView.setFitWidth(TOTEM_WIDTH);
        this.imageView.setFitHeight(TOTEM_HEIGHT);
        this.imageView.setPreserveRatio(true);
        this.imageView.setSmooth(true);

        // a temporary fallback shape is added to prevent invisible layout collapse
        Rectangle fallbackRect = new Rectangle(TOTEM_WIDTH, TOTEM_HEIGHT);
        fallbackRect.setFill(getFallbackColor(color));
        fallbackRect.setStroke(Color.BLACK);
        this.getChildren().add(fallbackRect);

        loadTotemSprite(color);

        this.getChildren().add(this.imageView);
    }

    private void loadTotemSprite(TotemColor color) {
        if (color == null) return;

        String basePath = "/assets/images/totems/totem_";
        String colorName = color.name().toLowerCase();
        String fullPath = basePath + colorName + ".png";

        Image image = ImageCache.getInstance().getImage(fullPath);

        if (image != null) {
            this.imageView.setImage(image);
            // fallback rectangle is hidden if image loads successfully
            this.getChildren().get(0).setVisible(false);
        } else {
            System.err.println("[totemview] sprite not found for color: " + colorName);
        }
    }

    // temporary helper for testing layout without pngs
    private Color getFallbackColor(TotemColor color) {
        if (color == null) return Color.GRAY;
        switch (color.name().toUpperCase()) {
            case "RED": return Color.web("#e63946");
            case "YELLOW": return Color.web("#f4a261");
            case "BLUE": return Color.web("#457b9d");
            case "BLACK": return Color.web("#1d3557");
            case "WHITE": return Color.web("#f1faee");
            default: return Color.GRAY;
        }
    }
}
