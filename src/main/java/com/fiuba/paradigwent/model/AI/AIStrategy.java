package com.fiuba.paradigwent.model.ai;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.player.Hand;

public interface AIStrategy {

    public Card selectCard(Hand hand,GameContext gameContext);
}
