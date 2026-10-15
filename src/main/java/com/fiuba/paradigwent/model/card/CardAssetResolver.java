package com.fiuba.paradigwent.model.card;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class CardAssetResolver {
    private static final String FALLBACK_ASSET = "/images/cards/noimage.png";

    private CardAssetResolver() {
    }

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

    public static String resolve(Card card) {
        if (card == null || card.getImagePath() == null || card.getImagePath().isBlank()) {
            return FALLBACK_ASSET;
        }

        return resolveDeclaredPath(card.getImagePath());
    }

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

    private static boolean resourceExists(String path) {
        return CardAssetResolver.class.getResource(path) != null;
    }
}
