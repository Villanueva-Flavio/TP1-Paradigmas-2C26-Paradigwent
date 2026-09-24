package com.fiuba.paradigwent.model.card;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.effect.Effect;

public abstract class Card implements Effect {
    private final String id;
    private final String name;

    private final int stackLimite;

    public Card(String id, String name, int stackLimite) {
        this.id = id;
        this.name = name;
        this.stackLimite = stackLimite;
    }


    public abstract void play(GameContext context);

    public void apply(GameContext context) {
    }
}
