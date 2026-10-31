package com.fiuba.paradigwent.model.game;

/**
 * Takes every turn right away. Used when there is no user interface,
 * for example in tests that play a whole match.
 */
public class ImmediateTurnScheduler implements TurnScheduler {

    /** {@inheritDoc} */
    @Override
    public void schedule(Runnable turn) {
        turn.run();
    }
}
