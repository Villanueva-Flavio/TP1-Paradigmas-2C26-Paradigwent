package com.fiuba.paradigwent.model.effect;

import com.fiuba.paradigwent.model.GameContext;
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
        for (int i = 0; i < amount && !player.getDiscard().isEmpty(); i++) {
            Card cardToRevive = player.getDiscard().get(player.getDiscard().size() - 1);
            player.reviveCard(cardToRevive);
        }
    }
}
