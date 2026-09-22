package com.fiuba.paradigwent.model.card;

public class Minion extends Card{

    private final int basePower;
    private int powerModifier;

    public Minion(String id, String name,int stackLimite, int basePower, int powerModifier)
    {
        super(id, name,stackLimite);
        this.basePower = basePower;
        this.powerModifier = 0;
    }

    public int getCurrentPower() {
        return basePower + powerModifier;
    }

    public void modifyPower(int modifier) {
        this.powerModifier += modifier;
    }

}
