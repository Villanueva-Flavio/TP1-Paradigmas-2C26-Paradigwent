package com.fiuba.paradigwent.model.ai;
import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.player.Hand;

import java.util.Random;

public class RandomAIStrategy implements AIStrategy {

    private final Random random;

    /**      * Constructs a new RandomAIStrategy.      */
    public RandomAIStrategy(Random random) {
        this.random = random;
    }
    /**
     * Executes the operation represented by selectCard.
     * @param hand the value used by the method
     * @param context the value used by the method
     * @return the value produced by the method
     */
    @Override
    public Card selectCard(Hand hand, GameContext context) {
        var cards = hand.view();


        if (cards.isEmpty() || shouldPass(context,random)) {
            return null;
        }

        return cards.get(random.nextInt(cards.size()));
    }
    /**
     * Executes the operation represented by shouldPass.
     * @param context the value used by the method
     * @param random the value used by the method
     * @return the value produced by the method
     */
    private boolean shouldPass(GameContext context, Random random) {

        int playerPower =  context.getBoard().getPlayerSide().totalPower();
        int IAPower = context.getBoard().getOpponentSide().totalPower();

        boolean powerDifference = IAPower >= playerPower;

        boolean soloRound = context.getOpponent().hasPassedRound();
        boolean randomPass = random.nextInt(100) <=2;


        return randomPass ||  (soloRound && powerDifference);
    }
}
