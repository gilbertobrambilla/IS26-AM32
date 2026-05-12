package it.polimi.ingsw.am32.client.view.gui.utils;

import javafx.scene.image.Image;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Gui image cache is implemented to store physical images in memory.
 */
public class ImageCache {
    private static ImageCache instance;
    private final Map<String, Image> cache;

    private ImageCache() {
        this.cache = new HashMap<>();
    }

    public static ImageCache getInstance() {
        if (instance == null) {
            instance = new ImageCache();
        }
        return instance;
    }

    public Image getImage(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }

        Image img = cache.get(path);

        if (img == null) {
            try (InputStream is = getClass().getResourceAsStream(path)) {
                if (is == null) {
                    System.err.println("[ImageCache] Could not find resource in classpath: " + path);
                    return null;
                }
                img = new Image(is);
                cache.put(path, img);
            } catch (Exception e) {
                System.err.println("[ImageCache] Error loading image from stream: " + path);
                e.printStackTrace();
            }
        }

        return img;
    }
}
