package com.fiuba.paradigwent.model.event;

import java.util.ArrayList;
import java.util.List;

import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.enums.Result;
import com.fiuba.paradigwent.model.enums.WeatherType;
import com.fiuba.paradigwent.model.player.Player;

/**
 * Composite listener that forwards every event to all registered listeners.
 */
public class GameListeners implements GameListener {
    private final List<GameListener> listeners = new ArrayList<>();

    /**
     * Registers a new listener.
     * @param listener the listener to notify from now on
     */
    public void add(GameListener listener) {
        listeners.add(listener);
    }

    @Override
    public void minionDestroyed(Minion minion) {
        listeners.forEach(listener -> listener.minionDestroyed(minion));
    }

    @Override
    public void minionRevived(Minion minion) {
        listeners.forEach(listener -> listener.minionRevived(minion));
    }

    @Override
    public void powerModified(Minion minion, int amount) {
        listeners.forEach(listener -> listener.powerModified(minion, amount));
    }

    @Override
    public void lineDuplicated(AttackLineType line) {
        listeners.forEach(listener -> listener.lineDuplicated(line));
    }

    @Override
    public void weatherChanged(WeatherType weather) {
        listeners.forEach(listener -> listener.weatherChanged(weather));
    }

    @Override
    public void cardsStolen(int amount) {
        listeners.forEach(listener -> listener.cardsStolen(amount));
    }

    @Override
    public void roundStarted(Player startingPlayer) {
        listeners.forEach(listener -> listener.roundStarted(startingPlayer));
    }

    @Override
    public void cardPlayed(Player player, Card card) {
        listeners.forEach(listener -> listener.cardPlayed(player, card));
    }

    @Override
    public void turnPassed(Player player) {
        listeners.forEach(listener -> listener.turnPassed(player));
    }

    @Override
    public void roundFinished(Result result) {
        listeners.forEach(listener -> listener.roundFinished(result));
    }

    @Override
    public void matchFinished(Result result) {
        listeners.forEach(listener -> listener.matchFinished(result));
    }
}
