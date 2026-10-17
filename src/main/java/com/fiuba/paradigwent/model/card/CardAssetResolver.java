package com.fiuba.paradigwent.model.card;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class CardAssetResolver {
    private static final String FALLBACK_ASSET = "/images/cards/noimage.png";

    /**      * Constructs a new CardAssetResolver.      */
    private CardAssetResolver() {
    }
    /**
     * Executes the operation represented by resolveFactionFrame.
     * @param card the value used by the method
     * @return the value produced by the method
     */
    public static String resolveFactionFrame(Card card) {
        if (card == null) {
            return FALLBACK_ASSET;
        }
        String declaredFrame = resolveDeclaredPath(card.getFramePath());
        if (!FALLBACK_ASSET.equals(declaredFrame)) {
            return declaredFrame;
        }
        return resolveDeclaredPath(card.getFaction() == null
            ? null : "images/cards/card-" + card.getFaction().trim().toLowerCase(Locale.ROOT) + ".png");
    }
    /**
     * Executes the operation represented by resolve.
     * @param card the value used by the method
     * @return the value produced by the method
     */
    public static String resolve(Card card) {
        if (card == null || card.getImagePath() == null || card.getImagePath().isBlank()) {
            return FALLBACK_ASSET;
        }

        return resolveDeclaredPath(card.getImagePath());
    }
    /**
     * Executes the operation represented by resolveDeclaredPath.
     * @param declaredPath the value used by the method
     * @return the value produced by the method
     */
    private static String resolveDeclaredPath(String declaredPath) {
        if (declaredPath == null || declaredPath.isBlank()) {
            return FALLBACK_ASSET;
        }

        String normalized = declaredPath.trim().replace('\\', '/')
            .replaceFirst("^/+", "")
            .replaceFirst("^image/", "images/");
        String fileName = normalized.substring(normalized.lastIndexOf('/') + 1);

        List<String> candidates = new ArrayList<>();
        candidates.add("/" + normalized);
        candidates.add("/images/" + fileName);
        candidates.add("/images/cards/" + fileName);
        candidates.add("/images/cards/preview/" + fileName);
        for (String candidate : candidates) {
            if (resourceExists(candidate)) {
                return candidate;
            }
        }
        return FALLBACK_ASSET;
    }
    /**
     * Executes the operation represented by resourceExists.
     * @param path the value used by the method
     * @return the value produced by the method
     */
    private static boolean resourceExists(String path) {
        return CardAssetResolver.class.getResource(path) != null;
    }
}
