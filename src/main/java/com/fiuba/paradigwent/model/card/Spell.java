package com.fiuba.paradigwent.model.card;

import com.fiuba.paradigwent.model.GameContext;

public class Spell extends Card {

    public Spell(String id, String name,int stackLimite) {
        super(id, name, stackLimite);
    }

    @Override
    public void play(GameContext context) {

    }
    @Override
    public void apply(GameContext context) {

    }

}
