package com.fiuba.paradigwent.model.card;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.enums.AttackLineType;

public abstract class Card {
    private String id;
    private String name;
    private final String description;
    private final AttackLineType attackLineType;
    private int stackLimit;
    private String faction;
    private String imagePath;
    private String framePath;
    protected int basePower;

    /**
     * Constructs a Card object with the specified parameters.
     * @param id
     * @param name
     * @param stackLimit
     * @param attackLineType
     * @param description
    **/
    public Card(String id, String name, int stackLimit, AttackLineType attackLineType, String description) {
        this.id = id;
        this.name = name;
        this.description = description != null ? description : "";
        this.stackLimit = stackLimit;
        this.attackLineType = attackLineType;
    }

    /**
     * @return The unique identifier of the card.
    **/
    public AttackLineType getAttackLineType() {
        return attackLineType;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getStackLimit() {
        return stackLimit;
    }

    public String getFaction() {
        return faction;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getFramePath() {
        return framePath;
    }

    public int getBasePower() {
        return basePower;
    }

    /**
     * @return The unique identifier of the card.
    **/
    public abstract void play(GameContext context);

    public boolean requiresTarget() {
        return false;
    }

    public String getEffectDescription() {
        return "";
    }

    public String getTargetDescription() {
        return "";
    }

    public String getCardType() {
        return getClass().getSimpleName().toUpperCase();
    }

    @Override
    public String toString() {
        return name + " [" + getCardType() + "]";
    }

    /**
     * @return The description of the card.
    **/
    public String getDescription() {
        return description;
    }

}
