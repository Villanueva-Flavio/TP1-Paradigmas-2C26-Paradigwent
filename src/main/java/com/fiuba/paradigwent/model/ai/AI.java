package com.fiuba.paradigwent.model.ai;

import com.fiuba.paradigwent.model.game.Move;
import com.fiuba.paradigwent.model.game.TurnActions;
import com.fiuba.paradigwent.model.player.Deck;
import com.fiuba.paradigwent.model.player.Player;

/**
 * Player controlled by the computer. It does not know which strategy it
 * uses; it only asks it for a move every time it is its turn.
 */
public class AI extends Player {

    private final AIStrategy strategy;

    /**
     * @param name     display name
     * @param deck     deck of the AI
     * @param strategy algorithm used to decide every move
     */
    public AI(String name, Deck deck, AIStrategy strategy) {
        super(name, deck);
        this.strategy = strategy;
    }

    /**
     * Decides and makes its move. If the move is rejected, the AI passes so
     * the match never gets stuck.
     *
     * @param actions the actions available during the turn
     */
    @Override
    public void takeTurn(TurnActions actions) {
        Move move = strategy.decideMove(hand.view(), actions.contextFor(this));
        if (!move.performBy(this, actions)) {
            actions.pass(this);
        }
    }
}
