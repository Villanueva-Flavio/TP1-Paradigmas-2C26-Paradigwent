package com.fiuba.paradigwent.model.card;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.enums.WeatherType;

import java.util.Collections;

public class Weather extends  Card{
    private final WeatherType weatherType;

    /**
     * Constructs a Weather card with the specified parameters. If the provided weatherType is null, it defaults to CLEAR.
     * @param id
     * @param name
     * @param stackLimite
     * @param weatherType
     * @param description
    **/
    public Weather(String id, String name, int stackLimite, WeatherType weatherType, String description) {
        super(id, name, stackLimite, AttackLineType.NONE, description);
        this.weatherType = weatherType == null ? WeatherType.CLEAR : weatherType;
    }

    /**
     * Returns the WeatherType associated with this Weather card.
     * @return The WeatherType of this Weather card.
    **/
    public WeatherType getWeatherType() {
        return weatherType;
    }

    @Override
    public void play(GameContext context) {
        context.getBoard().setCurrentWeather(this);
        context.getCurrentPlayer().sendToDiscard(Collections.singletonList(this));
    }
}
