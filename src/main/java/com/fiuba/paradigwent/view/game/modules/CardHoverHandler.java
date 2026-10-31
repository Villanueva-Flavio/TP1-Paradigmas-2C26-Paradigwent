package com.fiuba.paradigwent.view.game.modules;

import com.fiuba.paradigwent.model.card.Card;

import javafx.scene.Node;

/**
 * Receives the hover events of the cards shown on the board.
 */
public interface CardHoverHandler {

    /**
     * The pointer entered a card.
     *
     * @param card           the hovered card
     * @param source         node that shows the card
     * @param displayedPower power shown for the card where it is placed
     */
    void cardHovered(Card card, Node source, int displayedPower);

    /**
     * The pointer left the card.
     */
    void hoverEnded();
}
