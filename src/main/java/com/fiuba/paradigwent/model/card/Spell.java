package com.fiuba.paradigwent.model.card;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.enums.AttackLineType;

public class Spell extends Card {

    /**
     * Creates a new Spell card with the specified parameters.
     * @param id
     * @param name
     * @param stackLimite
     * @param attackLineType
    **/
    public Spell(String id, String name, int stackLimite, AttackLineType attackLineType) {
        super(id, name, stackLimite,attackLineType);
    }

    @Override
    public void play(GameContext context) {}
}
