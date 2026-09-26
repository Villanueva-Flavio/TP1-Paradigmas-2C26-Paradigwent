package com.fiuba.paradigwent.model.effect;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.player.Player;

public class ReviveEffect implements Effect {
    private final int amount;

    public ReviveEffect(int amount) {
        this.amount = amount;
    }

    @Override
    public void apply(GameContext context) {
        Player player = context.getCurrentPlayer();
        Card targetCard = context.getTargetCard();

        player.reviveCard(targetCard);

    }
}
