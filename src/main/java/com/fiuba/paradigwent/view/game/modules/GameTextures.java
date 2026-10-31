package com.fiuba.paradigwent.view.game.modules;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;

/**
 * Loads and caches the optional textures of the board. Each game view owns
 * one instance and hands it to the components that draw textures, so the
 * cache is not global state.
 */
public final class GameTextures {
    public static final String SKYBOX = "/images/environment/skybox.png";
    public static final String BOARD_WOOD = "/images/environment/board-wood.jpg";
    public static final String SLOT_WOOD = "/images/environment/slot-wood.jpg";
    private static final double SKYBOX_MAX_WIDTH = 1920;
    private static final double SKYBOX_MAX_HEIGHT = 1080;
    private static final double PATTERN_MAX_SIZE = 1024;

    private final Map<String, Image> imageCache = new HashMap<>();
    private final Map<String, Paint> patternCache = new HashMap<>();

    /**
     * Loads an image, reusing it if it was already loaded.
     *
     * @param resourcePath classpath of the image
     * @return the image, or {@code null} if it does not exist or cannot be decoded
     */
    public Image load(String resourcePath) {
        Image cached = imageCache.get(resourcePath);
        if (cached != null) {
            return cached;
        }

        URL resource = GameTextures.class.getResource(resourcePath);
        if (resource == null) {
            return null;
        }

        boolean skybox = SKYBOX.equals(resourcePath);
        double maxWidth = skybox ? SKYBOX_MAX_WIDTH : PATTERN_MAX_SIZE;
        double maxHeight = skybox ? SKYBOX_MAX_HEIGHT : PATTERN_MAX_SIZE;
        Image image = new Image(resource.toExternalForm(), maxWidth, maxHeight, true, true, false);
        if (image.isError() || image.getWidth() <= 0 || image.getHeight() <= 0) {
            return null;
        }

        imageCache.put(resourcePath, image);
        return image;
    }

    /**
     * Creates a paint that tiles an image.
     *
     * @param resourcePath classpath of the image
     * @param fallback     paint used when the image is missing
     * @return the textured paint, or the fallback
     */
    public Paint pattern(String resourcePath, Paint fallback) {
        Paint cached = patternCache.get(resourcePath);
        if (cached != null) {
            return cached;
        }

        Image image = load(resourcePath);
        if (image == null) {
            return fallback;
        }

        Paint pattern = new ImagePattern(image);
        patternCache.put(resourcePath, pattern);
        return pattern;
    }
}
