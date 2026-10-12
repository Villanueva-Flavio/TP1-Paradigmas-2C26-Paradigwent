package com.fiuba.paradigwent.model.card;

import java.util.Locale;

public final class CardAssetResolver {
    private static final String FALLBACK_ASSET = "/images/cards/noimage.png";

    private CardAssetResolver() {
    }

    public static boolean usesFallback(Card card) {
        return FALLBACK_ASSET.equals(resolve(card));
    }

    public static String resolveFactionFrame(Card card) {
        if (card == null || card.getFaction() == null) {
            return FALLBACK_ASSET;
        }

        String faction = card.getFaction().trim().toLowerCase(Locale.ROOT);
        String frame = "/images/cards/card-" + faction + ".png";
        if ("highelf".equals(faction)) {
            frame = "/images/cards/card-highelf.png";
        } else if ("murgol".equals(faction) || "murgo".equals(faction)) {
            frame = "/images/cards/card-murgol.png";
        }
        return resourceExists(frame) ? frame : FALLBACK_ASSET;
    }

    public static String resolve(Card card) {
        if (card == null || card.getImagePath() == null || card.getImagePath().isBlank()) {
            return FALLBACK_ASSET;
        }

        String normalizedPath = card.getImagePath().replace('\\', '/');
        String assetPath = normalizedPath.startsWith("/") ? normalizedPath : "/" + normalizedPath;
        assetPath = assetPath.replaceFirst("^/image/", "/images/");
        if (resourceExists(assetPath)) {
            return assetPath;
        }

        String fileName = assetPath.substring(assetPath.lastIndexOf('/') + 1);
        String cardsPath = "/images/cards/" + fileName;
        return resourceExists(cardsPath) ? cardsPath : FALLBACK_ASSET;
    }

    private static boolean resourceExists(String path) {
        return CardAssetResolver.class.getResource(path) != null;
    }
}
