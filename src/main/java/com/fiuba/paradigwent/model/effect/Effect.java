package com.fiuba.paradigwent.model.effect;

import com.fiuba.paradigwent.model.GameContext;

/**
 * Interface representing an effect that can be applied in the game context.
**/
public interface Effect {
    void apply(GameContext context);    

    default boolean requiresTarget() {
        return false;
    }

    default String getDescription() {
        return getClass().getSimpleName().replace("Effect", "");
    }

    default String getTargetDescription() {
        return "";
    }
}
