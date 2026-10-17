package com.fiuba.paradigwent.model.card;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.effect.Effect;
import java.util.List;
import java.util.Optional;

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
    /**
     * Returns the value provided by getArtworkScale.
     * @return the value produced by the method
     */
    public double getArtworkScale() {
        return artwork == null ? 1.1 : artwork.scale;
    }
    /**
     * Returns the value provided by getArtworkOffsetX.
     * @return the value produced by the method
     */
    public double getArtworkOffsetX() {
        return artwork == null ? 0 : artwork.offsetX;
    }
    /**
     * Returns the value provided by getArtworkOffsetY.
     * @param compact the value used by the method
     * @return the value produced by the method
     */
    public double getArtworkOffsetY(boolean compact) {
        if (artwork == null) return 0;
        return compact ? artwork.compactOffsetY : artwork.offsetY;
    }
    /**
     * Returns the value provided by getArtworkTitleOffsetY.
     * @return the value produced by the method
     */
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
    /**
     * Returns the value provided by getName.
     * @return the value produced by the method
     */
    public String getName() {
        return name;
    }
    /**
     * Returns the value provided by getFaction.
     * @return the value produced by the method
     */
    public String getFaction() {
        return faction;
    }
    /**
     * Returns the value provided by getImagePath.
     * @return the value produced by the method
     */
    public String getImagePath() {
        return imagePath;
    }
    /**
     * Returns the value provided by getFramePath.
     * @return the value produced by the method
     */
    public String getFramePath() {
        return framePath;
    }
    /**
     * Returns the value provided by getBasePower.
     * @return the value produced by the method
     */
    public int getBasePower() {
        return basePower;
    }
    /**
     * Returns the value provided by getDisplayedPower.
     * @return the value produced by the method
     */
    public int getDisplayedPower() {
        return basePower;
    }
    /**
     * Returns the value provided by getEffects.
     * @return the value produced by the method
     */
    public List<Effect> getEffects() {
        return List.of();
    }
    /**
     * Executes the operation represented by asMinion.
     * @return the value produced by the method
     */
    public Optional<Minion> asMinion() {
        return Optional.empty();
    }
    /**
     * Reports the state represented by hasOptionalTarget.
     * @return the value produced by the method
     */
    public boolean hasOptionalTarget() {
        return false;
    }

    /**
     * @return The unique identifier of the card.
    **/
    public abstract void play(GameContext context);
    /**
     * Executes the operation represented by requiresTarget.
     * @return the value produced by the method
     */
    public boolean requiresTarget() {
        return false;
    }
    /**
     * Returns the value provided by getEffectDescription.
     * @return the value produced by the method
     */
    public String getEffectDescription() {
        return "";
    }
    /**
     * Returns the value provided by getTargetDescription.
     * @return the value produced by the method
     */
    public String getTargetDescription() {
        return "";
    }
    /**
     * Returns the value provided by getCardType.
     * @return the value produced by the method
     */
    public String getCardType() {
        return getClass().getSimpleName().toUpperCase();
    }
    /**
     * Executes the operation represented by toString.
     * @return the value produced by the method
     */
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
