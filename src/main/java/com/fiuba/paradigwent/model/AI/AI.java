package com.fiuba.paradigwent.model.AI;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.player.Deck;
import com.fiuba.paradigwent.model.player.Player;


public class AI extends Player {

    private final AIStrategy strategy;

    public AI(String name, Deck deck, AIStrategy strategy) {
        super(name, deck);
        this.strategy = strategy;
    }

    @Override
    public void play(GameContext context) {
        if (hasPassedRound) {
            throw new IllegalStateException(
                    "A player who passed cannot play during this round");
        }

        Card chosen = strategy.selectCard(hand, context);

        if (chosen == null) {
            passRound();
            return;
        }
        play(chosen, context);
    }
}

