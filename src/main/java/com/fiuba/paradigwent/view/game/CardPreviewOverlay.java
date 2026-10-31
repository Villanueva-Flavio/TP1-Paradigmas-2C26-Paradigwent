package com.fiuba.paradigwent.view.game;

import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.view.game.modules.CardHoverHandler;
import com.fiuba.paradigwent.view.game.modules.CardVisual;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/**
 * Shows a large version of the card under the pointer, either next to a card
 * of the board or on the right of the screen for cards of the hand.
 */
public class CardPreviewOverlay implements CardHoverHandler {
    private static final double PREVIEW_HEIGHT = 460;
    private static final double HAND_PREVIEW_MARGIN = 70;

    private final StackPane container;
    private StackPane overlay;

    /**
     * @param container node that hosts the preview (covers the whole window)
     */
    public CardPreviewOverlay(StackPane container) {
        this.container = container;
    }

    /**
     * Shows a card of the hand on the right of the screen. The overlay is not
     * managed by the layout (it never gets a size), so the preview is placed
     * explicitly, like the previews of the board.
     *
     * @param card card to preview
     */
    public void showHandCard(Card card) {
        hide();
        overlay = createOverlay();
        CardVisual preview = createPreview(card, card.getDisplayedPower());
        overlay.getChildren().add(preview);
        container.getChildren().add(overlay);

        double x = container.getWidth() - preview.getPrefWidth() - HAND_PREVIEW_MARGIN;
        double y = (container.getHeight() - preview.getPrefHeight()) / 2;
        preview.setManaged(false);
        preview.relocate(Math.max(8, x), Math.max(8, y));
    }

    /**
     * Shows a card of the board next to the node that displays it.
     */
    @Override
    public void cardHovered(Card card, Node source, int displayedPower) {
        hide();
        overlay = createOverlay();
        CardVisual preview = createPreview(card, displayedPower);
        overlay.getChildren().add(preview);
        container.getChildren().add(overlay);

        Bounds bounds = source.localToScene(source.getBoundsInLocal());
        Point2D point = container.sceneToLocal(bounds.getMinX() - preview.getPrefWidth() - 14, bounds.getMinY() - 10);
        double x = Math.max(8, Math.min(point.getX(), container.getWidth() - preview.getPrefWidth() - 8));
        double y = Math.max(8, Math.min(point.getY(), container.getHeight() - preview.getPrefHeight() - 8));
        preview.setManaged(false);
        preview.relocate(x, y);
    }

    /** {@inheritDoc} */
    @Override
    public void hoverEnded() {
        hide();
    }

    /**
     * Removes the preview, if any.
     */
    public void hide() {
        if (overlay != null) {
            container.getChildren().remove(overlay);
            overlay = null;
        }
    }

    private StackPane createOverlay() {
        StackPane result = new StackPane();
        result.setManaged(false);
        result.getStyleClass().add("collection-preview-overlay");
        result.setPickOnBounds(false);
        Region backdrop = new Region();
        backdrop.setMouseTransparent(true);
        backdrop.setOpacity(0);
        backdrop.getStyleClass().add("collection-preview-backdrop");
        result.getChildren().add(backdrop);
        return result;
    }

    private CardVisual createPreview(Card card, int displayedPower) {
        CardVisual preview = new CardVisual(card, CardVisual.widthForHeight(PREVIEW_HEIGHT),
            PREVIEW_HEIGHT, "collection-card-image");
        preview.addMetadata(card, false, displayedPower);
        preview.setMouseTransparent(true);
        return preview;
    }
}
