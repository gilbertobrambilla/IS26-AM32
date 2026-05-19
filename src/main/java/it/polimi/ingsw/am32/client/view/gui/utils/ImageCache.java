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
        // internal map is initialized
        this.cache = new HashMap<>();
    }

    public static ImageCache getInstance() {
        // instance is created if null
        if (instance == null) {
            instance = new ImageCache();
        }
        // instance is returned
        return instance;
    }

    public Image getImage(String path) {
        // path is checked for nullity
        if (path == null || path.isEmpty()) {
            return null;
        }

        // image is retrieved from cache
        Image img = cache.get(path);

        // image is loaded if null
        if (img == null) {
            // resource stream is opened from classpath
            InputStream is = ImageCache.class.getResourceAsStream(path);
            if (is != null) {
                // new image is instantiated
                img = new Image(is);
                // image is stored in cache
                cache.put(path, img);
            } else {
                // error is logged if resource is missing
                System.err.println("[ImageCache] missing resource: " + path);
            }
        }

        // image is returned
        return img;
    }
}
