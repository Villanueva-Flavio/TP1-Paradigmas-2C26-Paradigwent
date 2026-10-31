package com.fiuba.paradigwent.view.game;

import java.util.Random;

import com.fiuba.paradigwent.model.game.TurnScheduler;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

/**
 * Takes every turn after a short random pause, so the moves of the AI can be
 * followed on screen. The model decides whose turn it is; this class only
 * decides when it happens.
 */
public class DelayedTurnScheduler implements TurnScheduler {
    private static final int MIN_DELAY_MILLIS = 2000;
    private static final int EXTRA_DELAY_MILLIS = 2000;

    private final Random random;

    /**
     * @param random source used to vary the "thinking" time
     */
    public DelayedTurnScheduler(Random random) {
        this.random = random;
    }

    /** {@inheritDoc} */
    @Override
    public void schedule(Runnable turn) {
        int delay = MIN_DELAY_MILLIS + random.nextInt(EXTRA_DELAY_MILLIS + 1);
        PauseTransition pause = new PauseTransition(Duration.millis(delay));
        pause.setOnFinished(event -> turn.run());
        pause.play();
    }
}
