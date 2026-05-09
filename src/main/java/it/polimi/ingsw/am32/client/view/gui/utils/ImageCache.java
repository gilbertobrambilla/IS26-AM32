package it.polimi.ingsw.am32.client.view.gui.utils;

import javafx.scene.image.Image;

import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Singleton class responsible for loading and caching JavaFX Images.
 * Implements the Flyweight pattern: each unique image is loaded only once.
 *
 * Thread-safety: getInstance() uses the initialization-on-demand holder idiom,
 * guaranteed by the JVM class loader — no synchronization overhead on the hot path.
 * The internal cache uses ConcurrentHashMap to tolerate concurrent reads and writes
 * from both the FX thread and background threads.
 *
 * Image loading: uses background loading (new Image(url, true)) to avoid blocking
 * the JavaFX Application Thread during rendering.
 */
public class ImageCache {

    private static final String IMAGES_BASE_PATH = "/assets/images/";
    private static final String FILE_EXTENSION = ".png";

    private final ConcurrentMap<String, Image> cache = new ConcurrentHashMap<>();

    // Initialization-on-demand holder: thread-safe lazy init, zero synchronization cost.
    private static final class Holder {
        static final ImageCache INSTANCE = new ImageCache();
    }

    private ImageCache() {}

    public static ImageCache getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Retrieves an image from the cache, loading it asynchronously if not yet present.
     * Returns immediately with the Image object (which may still be loading in background).
     * Returns null only if the resource path does not exist in the classpath.
     *
     * @param identifier unique image identifier (e.g. "CHAR_01"), without path or extension.
     * @return the JavaFX Image (possibly still loading), or null if the resource is not found.
     */
    public Image getImage(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            return null;
        }

        // ConcurrentHashMap.get() is non-blocking on the hot path (cache hit).
        Image cached = cache.get(identifier);
        if (cached != null) {
            return cached;
        }

        String fullPath = IMAGES_BASE_PATH + identifier + FILE_EXTENSION;
        URL imageUrl = getClass().getResource(fullPath);

        if (imageUrl == null) {
            System.err.println("[GUI WARNING] Image not found at path: " + fullPath);
            return null;
        }

        // Background loading (second arg = true): returns immediately, loads off the FX thread.
        // putIfAbsent avoids creating a duplicate Image if two callers race on the same identifier.
        Image image = new Image(imageUrl.toExternalForm(), true);
        Image existing = cache.putIfAbsent(identifier, image);
        return existing != null ? existing : image;
    }

    /**
     * Clears the image cache.
     * Must be called only after all ImageView nodes referencing cached images
     * have been removed from the scene graph, to allow garbage collection.
     */
    public void clearCache() {
        cache.clear();
        System.out.println("[GUI] Image cache cleared.");
    }
}
