package it.polimi.ingsw.am32.client.view.gui.utils;

import javafx.scene.image.Image;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Gui image cache is implemented to store physical images in memory.
 */
public class ImageCache {
    private static ImageCache instance;
    private final Map<String, Image> cache;

    /**
     * Gui image cache is instantiated.
     */
    private ImageCache() {
        // internal map is initialized
        this.cache = new HashMap<>();
    }

    /**
     * Singleton instance is retrieved.
     *
     * @return the gui image cache instance.
     */
    public static ImageCache getInstance() {
        // instance is created if null
        if (instance == null) {
            instance = new ImageCache();
        }
        // instance is returned
        return instance;
    }

    /**
     * Image is retrieved from the cache or loaded if absent.
     *
     * @param path the path of the image.
     * @return the loaded javafx image.
     */
    public Image getImage(String path) {
        // path is checked for nullity
        if (path == null || path.isEmpty()) {
            // null is returned
            return null;
        }

        // image is retrieved from cache
        Image img = cache.get(path);

        // image is loaded if null
        if (img == null) {
            URL resourceUrl = getClass().getResource(path);
            if (resourceUrl == null) {
                System.err.println("[ImageCache] Could not find resource in classpath: " + path);
                return null;
            }
            img = new Image(resourceUrl.toExternalForm());
            // image is stored in cache
            cache.put(path, img);
        }

        // image is returned
        return img;
    }
}
