package com.fiuba.paradigwent.model.effect;

import com.fiuba.paradigwent.model.Card;
import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.Player;

public class ReviveEffect implements Effect {
    private final int amount;

    public ReviveEffect(int amount){
        this.amount = amount;
    }

    @Override
    public void apply(GameContext context){
        Player player = context.getCurrentPlayer();
        Card targetCard = context.getTargetCard();

        if (targetCard != null && player.getDiscard().contains(targetCard)){
            player.getDiscard().removeCard(targetCard);
            player.getHand().addCard(targetCard);
        } else {
            for(int i = 0; i < amount; i++){
                Card revived = player.getDiscard().popRandomNonSpecialCard();
                if(revived != null){
                    player.getHand().addCard(revived);
                }
            }
        }
    }
}
