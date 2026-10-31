package com.fiuba.paradigwent.model.effect;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.targeting.NoTarget;
import com.fiuba.paradigwent.model.targeting.Targeting;

/**
 * Effect that a card applies when it is played. Cards compose effects and
 * apply them without knowing which concrete effect they hold.
 */
public interface Effect {

    /**
     * Applies the effect.
     *
     * @param context operations available for the current play
     */
    void apply(GameContext context);

    /**
     * @return a short text that explains what the effect does
     */
    String getDescription();

    /**
     * @return a short text that explains what the effect acts on
     */
    default String getTargetDescription() {
        return "";
    }

    /**
     * @return what must be chosen before the effect is applied
     */
    default Targeting targeting() {
        return new NoTarget();
    }
}
