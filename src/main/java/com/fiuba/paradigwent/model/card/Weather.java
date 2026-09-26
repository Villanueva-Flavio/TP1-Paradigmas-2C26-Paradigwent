package com.fiuba.paradigwent.model.card;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.enums.AttackLineType;

import java.util.Collections;

public class Weather extends  Card{

    public Weather(String id, String name, int stackLimite, AttackLineType attackLineType)
    {
        super(id,name,stackLimite, attackLineType);
    }

    @Override
    public void play(GameContext context) {

        context.getBoard().setCurrentWeather(this.getAttackLineType());
        context.getCurrentPlayer().sendToDiscard(Collections.singletonList(this));

    }

}
