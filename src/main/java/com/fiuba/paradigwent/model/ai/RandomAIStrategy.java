package com.fiuba.paradigwent.model.ai;

import java.util.List;
import java.util.Random;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.game.Move;
import com.fiuba.paradigwent.model.game.PassMove;
import com.fiuba.paradigwent.model.game.PlayCardMove;

/**
 * Strategy that plays a random card on a random suitable target. It passes
 * when it has no cards, when it is already winning a round its rival left,
 * or, rarely, at random.
 */
public class RandomAIStrategy implements AIStrategy {
    private static final int RANDOM_PASS_PERCENT = 3;

    private final Random random;

    /**
     * @param random source of randomness
     */
    public RandomAIStrategy(Random random) {
        this.random = random;
    }

    /** {@inheritDoc} */
    @Override
    public Move decideMove(List<Card> hand, GameContext context) {
        if (hand.isEmpty() || shouldPass(context)) {
            return new PassMove();
        }

        Card card = hand.get(random.nextInt(hand.size()));
        RandomTargetChooser chooser = new RandomTargetChooser(context, random);
        card.targeting().request(chooser);
        return new PlayCardMove(card, chooser.chosenMinion(), chooser.chosenLine());
    }

    private boolean shouldPass(GameContext context) {
        boolean winningAlone = context.enemyHasPassed() && context.ownPower() > context.enemyPower();
        boolean randomPass = random.nextInt(100) < RANDOM_PASS_PERCENT;
        return winningAlone || randomPass;
    }
}
