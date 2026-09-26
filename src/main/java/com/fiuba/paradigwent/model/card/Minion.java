package com.fiuba.paradigwent.model.card;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.board.AttackLine;
import com.fiuba.paradigwent.model.enums.AttackLineType;

public class Minion extends Card{

    private final int basePower;
    private int powerModifier;


    public Minion(String id, String name,int stackLimite, int basePower, AttackLineType attackLineType)
    {
        super(id, name,stackLimite, attackLineType);
        this.basePower = basePower;
        this.powerModifier = 0;
    }

    public int getCurrentPower() {
        return basePower + powerModifier;
    }

    public void modifyPower(int modifier) {
        this.powerModifier += modifier;
    }


    @Override
    public void play(GameContext context) {

        AttackLine line =  context.getBoard().getPlayerSide().getAttackLine(context.getPlayedCard().getAttackLineType());
        line.addMinion((Minion) context.getPlayedCard());
    }

}
