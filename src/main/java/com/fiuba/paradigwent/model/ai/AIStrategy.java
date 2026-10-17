package com.fiuba.paradigwent.model.ai;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.player.Hand;

public interface AIStrategy {
    /**
     * Executes the operation represented by selectCard.
     * @param hand the value used by the method
     * @param gameContext the value used by the method
     * @return the value produced by the method
     */
    public Card selectCard(Hand hand,GameContext gameContext);
}
