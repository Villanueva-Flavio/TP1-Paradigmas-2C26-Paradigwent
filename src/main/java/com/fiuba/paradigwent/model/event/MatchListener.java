package com.fiuba.paradigwent.model.event;

import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.enums.Result;
import com.fiuba.paradigwent.model.player.Player;

/**
 * Observer of the flow of a match: turns, rounds and the final result.
 * Every method has an empty default so listeners only override what they need.
 */
public interface MatchListener {

    /**
     * A round started.
     * @param startingPlayer the player chosen at random to open the round
     */
    default void roundStarted(Player startingPlayer) { }

    /**
     * A player is about to resolve a card.
     * @param player the player that played it
     * @param card   the played card
     */
    default void cardPlayed(Player player, Card card) { }

    /**
     * A player passed the round.
     * @param player the player that passed
     */
    default void turnPassed(Player player) { }

    /**
     * A round finished and its result was applied to the lives of the players.
     * @param result the result of the round
     */
    default void roundFinished(Result result) { }

    /**
     * The match finished.
     * @param result the final result of the match
     */
    default void matchFinished(Result result) { }
}
