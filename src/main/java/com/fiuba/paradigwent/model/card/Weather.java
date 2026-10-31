package com.fiuba.paradigwent.model.card;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.enums.WeatherType;

/**
 * Weather card: it changes the weather of the whole board and goes to the discard pile.
 */
public class Weather extends Card {
    private final WeatherType weatherType;

    /**
     * Creates a weather card. A null weather defaults to {@link WeatherType#CLEAR}.
     *
     * @param id          unique identifier
     * @param name        display name
     * @param stackLimit  maximum copies allowed in a deck
     * @param weatherType weather applied by the card
     * @param description flavour text
     */
    public Weather(String id, String name, int stackLimit, WeatherType weatherType, String description) {
        super(id, name, stackLimit, AttackLineType.NONE, description);
        this.weatherType = weatherType == null ? WeatherType.CLEAR : weatherType;
    }

    private Weather(Weather other) {
        super(other);
        this.weatherType = other.weatherType;
    }

    /**
     * @return the weather applied by this card
     */
    public WeatherType getWeatherType() {
        return weatherType;
    }

    /**
     * Changes the board weather and discards the card.
     *
     * @param context operations available for the current play
     */
    @Override
    public void play(GameContext context) {
        context.changeWeather(this);
        context.discard(this);
    }

    /** {@inheritDoc} */
    @Override
    public Card copy() {
        return new Weather(this);
    }

    /** {@inheritDoc} */
    @Override
    public void classifyInto(CardPools pools) {
        pools.addWeather(this);
    }

    /** {@inheritDoc} */
    @Override
    public String getCardType() {
        return "WEATHER";
    }
}
