package com.fiuba.paradigwent.model.ai;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.player.Deck;
import com.fiuba.paradigwent.model.player.Player;


public class AI extends Player {

    private final AIStrategy strategy;

    /**      * Constructs a new AI.      */
    public AI(String name, Deck deck, AIStrategy strategy) {
        super(name, deck);
        this.strategy = strategy;
    }
    /**
     * Executes the operation represented by selectCard.
     * @param context the value used by the method
     * @return the value produced by the method
     */
    public Card selectCard(GameContext context) {
        return strategy.selectCard(hand, context);
    }
    /**
     * Executes the operation represented by play.
     * @param context the value used by the method
     */
    @Override
    public void play(GameContext context) {
        if (hasPassedRound) {
            throw new IllegalStateException(
                    "A player who passed cannot play during this round");
        }

        Card chosen = context.getPlayedCard();
        if (chosen == null || !hand.contains(chosen)) {
            chosen = strategy.selectCard(hand, context);
        }

        if (chosen == null) {
            passRound();
            return;
        }
        play(chosen, context);
    }
}
