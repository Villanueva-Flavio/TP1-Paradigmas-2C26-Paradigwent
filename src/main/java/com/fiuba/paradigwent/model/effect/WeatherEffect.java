package com.fiuba.paradigwent.model.effect;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.enums.AttackLineType;


public class WeatherEffect implements Effect {
    private final AttackLineType lineType;

    public WeatherEffect(AttackLineType lineType){
        this.lineType = lineType;
    }

    @Override
    public void apply(GameContext context){
        context.getBoard().setCurrentWeather(lineType);
    }
}
