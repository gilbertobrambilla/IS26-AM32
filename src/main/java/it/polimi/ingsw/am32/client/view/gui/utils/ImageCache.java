package it.polimi.ingsw.am32.client.view.gui.utils;

import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Flyweight cache for JavaFX {@link Image} objects loaded from the classpath.
 *
 * <p>Uses the <em>initialization-on-demand holder</em> idiom for thread-safe lazy
 * instantiation with zero synchronization overhead on the hot path.
 * Internally backed by a {@link ConcurrentHashMap}, so concurrent reads never block.
 * Paths that fail to load are tracked in a separate set to avoid redundant I/O on
 * subsequent calls.</p>
 *
 * <p>All methods are safe to call from any thread, including the JavaFX Application Thread.</p>
 */
public final class ImageCache {

    private final ConcurrentMap<String, Image> cache = new ConcurrentHashMap<>();
    private final Set<String> missingPaths = ConcurrentHashMap.newKeySet();

    private ImageCache() {}

    /** Returns the singleton instance. Initialized lazily and exactly once by the JVM. */
    public static ImageCache getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Returns the {@link Image} for the given classpath {@code path}, loading and caching
     * it on first access.
     *
     * <p>If the resource is missing or {@code path} is blank, {@code null} is returned and
     * the path is registered so subsequent calls skip the I/O attempt.</p>
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

    // --- private helpers ---

    private Image loadAndCache(String path) {
        InputStream stream = ImageCache.class.getResourceAsStream(path);
        if (stream == null) {
            missingPaths.add(path);
            GuiLogger.warning("[ImageCache] missing resource: " + path);
            return null;
        }

        Image image = new Image(stream);
        // putIfAbsent is safe under concurrent access: if another thread raced and won,
        // we simply discard our copy and return the winner already in the map.
        cache.putIfAbsent(path, image);
        return cache.get(path);
    }

    // --- initialization-on-demand holder ---

    private static final class Holder {
        private static final ImageCache INSTANCE = new ImageCache();
    }
}
