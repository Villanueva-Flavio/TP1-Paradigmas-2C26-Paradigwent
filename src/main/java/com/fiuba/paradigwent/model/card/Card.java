package com.fiuba.paradigwent.model.card;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.effect.Effect;

public abstract class Card implements Effect {
    private final String id;
    private final String name;

    private final int stackLimit;

    public Card(String id, String name, int stackLimit) {
        this.id = id;
        this.name = name;
        this.stackLimit = stackLimit;
    }


    public abstract void play(GameContext context);

    public void apply(GameContext context) {
    }
}
