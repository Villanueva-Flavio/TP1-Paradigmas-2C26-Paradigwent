package com.fiuba.paradigwent.model.card;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.board.AttackLine;
import com.fiuba.paradigwent.model.enums.AttackLineType;

public class Minion extends Card{

    private final int basePower;
    private int powerModifier;

    /**
     * Constructor for the Minion class, which represents a minion card in the game. It initializes the minion with its unique identifier, name, stack limit, base power, and attack line type.
     * @param id
     * @param name
     * @param stackLimite
     * @param basePower
     * @param attackLineType
    **/
    public Minion(String id, String name,int stackLimite, int basePower, AttackLineType attackLineType){
        super(id, name,stackLimite, attackLineType);
        this.basePower = basePower;
        this.powerModifier = 0;
    }

    /**
     * @return The current power of the minion, which is the sum of its base power and any modifiers that have been applied to it.
    **/
    public int getCurrentPower() {
        return basePower + powerModifier;
    }

    /**
     * Modifies the power of the minion by a specified amount. This method adjusts the power modifier, which affects the minion's current power.
     * @param modifier The amount to modify the minion's power by (can be positive or negative).
    **/
    public void modifyPower(int modifier) {
        this.powerModifier += modifier;
    }

    @Override
    public void play(GameContext context) {
        AttackLine line =  context.getBoard().getPlayerSide().getAttackLine(context.getPlayedCard().getAttackLineType());
        line.addMinion((Minion) context.getPlayedCard());
    }

}
