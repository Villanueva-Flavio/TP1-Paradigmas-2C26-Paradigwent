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
    private Artwork artwork = new Artwork();
    protected int basePower;

    public double getArtworkScale() {
        return artwork == null ? 1.1 : artwork.scale;
    }

    public double getArtworkOffsetX() {
        return artwork == null ? 0 : artwork.offsetX;
    }

    public double getArtworkOffsetY(boolean compact) {
        if (artwork == null) return 0;
        return compact ? artwork.compactOffsetY : artwork.offsetY;
    }

    public double getArtworkTitleOffsetY() {
        return artwork == null ? 0 : artwork.titleOffsetY;
    }

    private static class Artwork {
        private double scale = 1.1;
        private double offsetX;
        private double offsetY;
        private double compactOffsetY;
        private double titleOffsetY;
    }

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

    public String getName() {
        return name;
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
