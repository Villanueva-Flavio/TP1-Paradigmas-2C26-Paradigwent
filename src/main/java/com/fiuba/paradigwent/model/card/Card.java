package com.fiuba.paradigwent.model.card;

public abstract class Card {
    private final String id;
    private final String name;

    private final int stackLimite;

    public Card(String id, String name, int stackLimite) {
        this.id = id;
        this.name = name;
        this.stackLimite = stackLimite;
    }


    public abstract void play(GameContext context);
}
