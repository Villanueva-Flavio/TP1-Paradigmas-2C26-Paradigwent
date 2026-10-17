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

    default boolean isLineTargetEffect() {
        return false;
    }

    default boolean isOptionalTargetEffect() {
        return false;
    }

    default boolean isDestroyCreatureEffect() {
        return false;
    }

    default boolean isReviveEffect() {
        return false;
    }

    default boolean isModifyPowerEffect() {
        return false;
    }

    default boolean isDuplicateLineEffect() {
        return false;
    }

    default String getTargetPrompt() {
        return "";
    }
}
