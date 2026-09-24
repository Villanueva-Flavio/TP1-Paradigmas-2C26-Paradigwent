package com.fiuba.paradigwent.model.card;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.enums.AttackLineType;

public class Minion extends Card{

    private final int basePower;
    private int powerModifier;
    private AttackLineType attackLineType

    public Minion(String id, String name,int stackLimite, int basePower, AttackLineType attackLineType)
    {
        super(id, name,stackLimite);
        this.basePower = basePower;
        this.powerModifier = 0;
        this.attackLineType = attackLineType;
    }

    public int getCurrentPower() {
        return basePower + powerModifier;
    }

    public void modifyPower(int modifier) {
        this.powerModifier += modifier;
    }

    @Override
    public void play(GameContext context) {

    }

}
