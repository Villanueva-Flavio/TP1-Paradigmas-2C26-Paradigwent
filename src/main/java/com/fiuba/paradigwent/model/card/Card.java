package com.fiuba.paradigwent.model.card;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.enums.AttackLineType;

public abstract class Card {
    private final String id;
    private final String name;
    private final AttackLineType attackLineType;

    private final int stackLimit;

    public Card(String id, String name, int stackLimit, AttackLineType attackLineType) {
        this.id = id;
        this.name = name;
        this.stackLimit = stackLimit;
        this.attackLineType = attackLineType;
    }

    public AttackLineType getAttackLineType() {
        return attackLineType;
    }

    public abstract void play(GameContext context);

}
