package com.fiuba.paradigwent.view.game.modules;

import com.fiuba.paradigwent.model.card.Card;

import javafx.scene.Node;

/**
 * Null object used until a real hover handler is registered.
 */
public class IgnoredCardHover implements CardHoverHandler {

    /** {@inheritDoc} */
    @Override
    public void cardHovered(Card card, Node source, int displayedPower) {
    }

    /** {@inheritDoc} */
    @Override
    public void hoverEnded() {
    }
}
