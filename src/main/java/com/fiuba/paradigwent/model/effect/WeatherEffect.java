package com.fiuba.paradigwent.model.effect;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.enums.WeatherType;

/**
 * Changes the weather of the board.
 */
public class WeatherEffect implements Effect {
    private final WeatherType weatherType;

    /**
     * @param weatherType weather to apply, {@code null} means clear
     */
    public WeatherEffect(WeatherType weatherType) {
        this.weatherType = weatherType == null ? WeatherType.CLEAR : weatherType;
    }

    /** {@inheritDoc} */
    @Override
    public void apply(GameContext context) {
        context.changeWeather(weatherType);
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return "Applies " + weatherType + " weather.";
    }
}
