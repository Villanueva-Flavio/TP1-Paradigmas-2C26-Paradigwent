package com.fiuba.paradigwent.model.game;

/**
 * Decides when the next turn is taken. The rule "the player whose turn it is
 * takes it" lives in {@link Game}; the scheduler only decides the timing,
 * so the user interface can add pauses without driving the match itself.
 */
public interface TurnScheduler {

    /**
     * Schedules the next turn.
     *
     * @param turn action that takes the turn
     */
    void schedule(Runnable turn);
}
