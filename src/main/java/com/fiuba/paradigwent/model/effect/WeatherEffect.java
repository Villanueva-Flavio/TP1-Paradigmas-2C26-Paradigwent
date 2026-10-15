package com.fiuba.paradigwent.model.effect;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.enums.WeatherType;


public class WeatherEffect implements Effect {
    private final WeatherType weatherType;

    /**
     * Constructs a WeatherEffect with the specified WeatherType. If the provided weatherType is null, it defaults to CLEAR.
     * @param weatherType
    **/
    public WeatherEffect(WeatherType weatherType){
        this.weatherType = weatherType == null ? WeatherType.CLEAR : weatherType;
    }

    @Override 
    public String getDescription() {
        return "Applies " + weatherType + " weather.";
    }

    @Override
    public void apply(GameContext context){
        context.getBoard().setCurrentWeather(weatherType);
    }
}
