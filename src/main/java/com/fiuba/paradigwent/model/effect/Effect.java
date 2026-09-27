package com.fiuba.paradigwent.model.effect;

import com.fiuba.paradigwent.model.GameContext;

/**
 * Interface representing an effect that can be applied in the game context.
**/
public interface Effect {
    void apply(GameContext context);    
}
