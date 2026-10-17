package com.fiuba.paradigwent.view.game.modules;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javafx.scene.image.Image;
import javafx.scene.paint.Paint;
import javafx.scene.paint.ImagePattern;

/**
 * Centralizes optional game textures so replacing the assets does not require
 * changes to the view code.
 */
public final class GameTextures {
    public static final String SKYBOX = "/images/environment/skybox.png";
    public static final String BOARD_WOOD = "/images/environment/board-wood.jpg";
    public static final String SLOT_WOOD = "/images/environment/slot-wood.jpg";
    private static final double SKYBOX_MAX_WIDTH = 1920;
    private static final double SKYBOX_MAX_HEIGHT = 1080;
    private static final double PATTERN_MAX_SIZE = 1024;
    private static final Map<String, Image> IMAGE_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, Paint> PATTERN_CACHE = new ConcurrentHashMap<>();

    /**      * Constructs a new GameTextures.      */
    private GameTextures() {
    }
    /**
     * Executes the operation represented by load.
     * @param resourcePath the value used by the method
     * @return the value produced by the method
     */
    public static Image load(String resourcePath) {
        Image cached = IMAGE_CACHE.get(resourcePath);
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

        IMAGE_CACHE.put(resourcePath, image);
        return image;
    }
    /**
     * Executes the operation represented by pattern.
     * @param resourcePath the value used by the method
     * @param fallback the value used by the method
     * @return the value produced by the method
     */
    public static Paint pattern(String resourcePath, Paint fallback) {
        Paint cached = PATTERN_CACHE.get(resourcePath);
        if (cached != null) {
            return cached;
        }

        Image image = load(resourcePath);
        if (image == null) {
            return fallback;
        }

        Paint pattern = new ImagePattern(image);
        PATTERN_CACHE.put(resourcePath, pattern);
        return pattern;
    }
}
