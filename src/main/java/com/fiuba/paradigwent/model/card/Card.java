package com.fiuba.paradigwent.model.card;

public class Card {
    private final String id;
    private final String name;
    private final int basePower;
    private int powerModifier;
    private final int stackLimite;

    public Card(String id, String name, int basePower, int stackLimite) {
        this.id = id;
        this.name = name;
        this.basePower = basePower;
        this.stackLimite = stackLimite;
        this.powerModifier = 0;
    }

    public int getCurrentPower() {
        return basePower + powerModifier;
    }

    public void modifyPower(int modifier) {
        this.powerModifier += modifier;
    }
    public void play(GameContext context){}
}
