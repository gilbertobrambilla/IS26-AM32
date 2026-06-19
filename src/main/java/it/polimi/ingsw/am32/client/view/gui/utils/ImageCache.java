package it.polimi.ingsw.am32.client.view.gui.utils;

import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A flyweight cache for JavaFX {@link Image} objects is represented.
 *
 * <p>The <em>initialization-on-demand holder</em> idiom is used for thread-safe
 * lazy instantiation. The cache is backed by a {@link ConcurrentHashMap}. Paths
 * that fail to load are tracked in a separate set to avoid redundant I/O.</p>
 *
 * <p>All methods can be safely called from any thread.</p>
 */
public final class ImageCache {

    private final ConcurrentMap<String, Image> cache = new ConcurrentHashMap<>();
    private final Set<String> missingPaths = ConcurrentHashMap.newKeySet();

    /**
     * Instantiation is controlled by the singleton holder.
     */
    private ImageCache() {}

    /**
     * The singleton instance is returned and initialized lazily by the jvm.
     *
     * @return the image cache instance
     */
    public static ImageCache getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * The {@link Image} for the given classpath {@code path} is returned.
     * The image is loaded and cached on first access.
     *
     * <p>If the resource is missing or {@code path} is blank, {@code null} is returned and
     * the path is registered so later calls skip the I/O attempt.</p>
     *
     * @param path absolute classpath path (e.g. {@code /assets/images/card_back.png})
     * @return the loaded image, or {@code null} if unavailable
     */
    public Image getImage(String path) {
        if (path == null || path.isBlank()) {
            return null;
        }
        if (missingPaths.contains(path)) {
            return null;
        }

        Image cached = cache.get(path);
        if (cached != null) {
            return cached;
        }

        return loadAndCache(path);
    }

    // private helpers

    /**
     * An image is loaded from the classpath and stored in the cache.
     *
     * @param path the absolute classpath path
     * @return the cached image, or null if unavailable
     */
    private Image loadAndCache(String path) {
        InputStream stream = ImageCache.class.getResourceAsStream(path);
        if (stream == null) {
            missingPaths.add(path);
            GuiLogger.warning("[ImageCache] missing resource: " + path);
            return null;
        }

        Image image = new Image(stream);
        // cached winner is returned if concurrent access already stored an image
        cache.putIfAbsent(path, image);
        return cache.get(path);
    }

    // initialization-on-demand holder

    /**
     * Lazy singleton holder is represented.
     */
    private static final class Holder {
        private static final ImageCache INSTANCE = new ImageCache();
    }
}
