package com.fiuba.paradigwent.model.ai;

import java.util.List;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.game.Move;

/**
 * Algorithm an {@link AI} uses to decide its whole turn.
 */
public interface AIStrategy {

    /**
     * Decides the complete move of the turn: which card to play and on which
     * target, or whether to pass.
     *
     * @param hand    cards the AI can play
     * @param context information about the board from the AI's point of view
     * @return the move to make
     */
    Move decideMove(List<Card> hand, GameContext context);
}
