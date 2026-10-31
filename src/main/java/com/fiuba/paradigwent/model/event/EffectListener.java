package com.fiuba.paradigwent.model.event;

import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.enums.WeatherType;

/**
 * Observer of the changes that card effects produce on the board.
 * Every method has an empty default so listeners only override what they need.
 */
public interface EffectListener {

    /**
     * A minion was destroyed and sent to its owner's discard pile.
     * @param minion the destroyed minion
     */
    default void minionDestroyed(Minion minion) { }

    /**
     * A minion came back from the discard pile to the board.
     * @param minion the revived minion
     */
    default void minionRevived(Minion minion) { }

    /**
     * The power of a minion changed.
     * @param minion the affected minion
     * @param amount the applied modifier
     */
    default void powerModified(Minion minion, int amount) { }

    /**
     * The power of an attack line was duplicated.
     * @param line the duplicated line
     */
    default void lineDuplicated(AttackLineType line) { }

    /**
     * The weather of the board changed.
     * @param weather the new weather
     */
    default void weatherChanged(WeatherType weather) { }

    /**
     * Cards moved from the opponent's hand to the current player's hand.
     * @param amount number of stolen cards
     */
    default void cardsStolen(int amount) { }
}
