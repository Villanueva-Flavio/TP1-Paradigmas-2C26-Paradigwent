package com.fiuba.paradigwent.model.effect;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.player.Player;

public class ReviveEffect implements Effect {
    private final int amount;

    /**
     * Constructs a ReviveEffect with the specified amount of cards to revive from the discard pile.
     * @param amount
    **/
    public ReviveEffect(int amount) {
        this.amount = amount;
    }

    @Override 
    public String getDescription() {
        return "Revives " + (amount == 1 ? "a card" : amount + " cards");
    }

    @Override
    public void apply(GameContext context) {
        Player player = context.getCurrentPlayer();
        for (int revived = 0; revived < amount; revived++) {
            Minion minionToRevive = findMostRecentMinion(player.getDiscard());
            if (minionToRevive == null
                    || !player.reviveCardToBoard(
                        minionToRevive,
                        context.getBoard().getSideFor(player))) {
                break;
            }
        }
    }

    private Minion findMostRecentMinion(java.util.List<Card> discard) {
        for (int i = discard.size() - 1; i >= 0; i--) {
            if (discard.get(i) instanceof Minion minion) {
                return minion;
            }
        }
        return null;
    }
}
