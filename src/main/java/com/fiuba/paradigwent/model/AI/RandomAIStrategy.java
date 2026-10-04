package com.fiuba.paradigwent.model.AI;
import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.player.Hand;

import java.util.Random;

public class RandomAIStrategy implements AIStrategy {

    private final Random random;

    public RandomAIStrategy(Random random) {
        this.random = random;
    }

    @Override
    public Card selectCard(Hand hand, GameContext context) {
        var cards = hand.getHand();
        if (cards.isEmpty() || random.nextInt(10) <= 2) {
            return null;            // pasar
        }
        return cards.get(random.nextInt(cards.size()));
    }
}
