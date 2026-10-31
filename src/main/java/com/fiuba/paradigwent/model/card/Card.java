package com.fiuba.paradigwent.model.card;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.board.PlayerSide;
import com.fiuba.paradigwent.model.effect.Effect;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.enums.Faction;
import com.fiuba.paradigwent.model.event.EffectListener;
import com.fiuba.paradigwent.model.targeting.NoTarget;
import com.fiuba.paradigwent.model.targeting.Targeting;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Base class of every card. Each subclass decides how it is played
 * ({@link #play(GameContext)}) and the effects it carries are composed
 * as {@link Effect} objects.
 */
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
    private List<Effect> effects = new ArrayList<>();
    protected int basePower;

    private static class Artwork {
        private double scale = 1.1;
        private double offsetX;
        private double offsetY;
        private double compactOffsetY;
        private double titleOffsetY;
    }

    /**
     * Creates a card without effects.
     *
     * @param id             unique identifier of the card
     * @param name           display name
     * @param stackLimit     maximum copies allowed in a deck
     * @param attackLineType line where the card is placed
     * @param description    flavour text
     */
    public Card(String id, String name, int stackLimit, AttackLineType attackLineType, String description) {
        this(id, name, stackLimit, attackLineType, description, List.of());
    }

    /**
     * Creates a card that carries the given effects.
     *
     * @param id             unique identifier of the card
     * @param name           display name
     * @param stackLimit     maximum copies allowed in a deck
     * @param attackLineType line where the card is placed
     * @param description    flavour text
     * @param effects        effects applied when the card is played
     */
    public Card(String id, String name, int stackLimit, AttackLineType attackLineType,
                String description, List<Effect> effects) {
        this.id = id;
        this.name = name;
        this.description = description != null ? description : "";
        this.stackLimit = stackLimit;
        this.attackLineType = attackLineType;
        this.effects = new ArrayList<>(effects);
    }

    /**
     * Copy constructor used by {@link #copy()} so every copy of a card in a
     * deck is an independent object.
     *
     * @param other card to copy
     */
    protected Card(Card other) {
        this.id = other.id;
        this.name = other.name;
        this.description = other.description;
        this.attackLineType = other.attackLineType;
        this.stackLimit = other.stackLimit;
        this.faction = other.faction;
        this.imagePath = other.imagePath;
        this.framePath = other.framePath;
        this.artwork = other.artwork;
        this.effects = new ArrayList<>(other.effects());
        this.basePower = other.basePower;
    }

    /**
     * Resolves the card on the board.
     *
     * @param context operations available for the current play
     */
    public abstract void play(GameContext context);

    /**
     * @return an independent copy of this card
     */
    public abstract Card copy();

    /**
     * Adds this card to the pool that corresponds to its kind (double dispatch).
     *
     * @param pools pools used to build a deck
     */
    public abstract void classifyInto(CardPools pools);

    /**
     * @return the card category shown to the user (MINION, SPELL or WEATHER)
     */
    public abstract String getCardType();

    /**
     * Tries to bring this card back from the discard pile to the board.
     * Only minions can be revived, so the default implementation refuses.
     *
     * @param side     side of the board where the card would be placed
     * @param listener observer notified when the card is revived
     * @return {@code true} if the card was placed on the board
     */
    public boolean reviveOnto(PlayerSide side, EffectListener listener) {
        return false;
    }

    /**
     * Tells what has to be chosen before playing this card. When several
     * effects need a target, the first one is requested.
     *
     * @return the targeting of the card
     */
    public Targeting targeting() {
        return effects().stream()
            .map(Effect::targeting)
            .reduce(new NoTarget(), Targeting::orElse);
    }

    /**
     * Applies every effect of the card.
     *
     * @param context operations available for the current play
     */
    protected void applyEffects(GameContext context) {
        for (Effect effect : effects()) {
            effect.apply(context);
        }
    }

    /**
     * @return {@code true} if the card has at least one effect
     */
    protected boolean hasEffects() {
        return !effects().isEmpty();
    }

    /**
     * Cards built by Gson skip field initialisers, so a missing "effects"
     * entry in the JSON leaves the list null.
     */
    private List<Effect> effects() {
        return effects == null ? List.of() : effects;
    }

    /**
     * @param faction a playable faction
     * @return {@code true} if the card belongs to the faction
     */
    public boolean belongsTo(Faction faction) {
        return faction != null && faction.name().equalsIgnoreCase(this.faction);
    }

    /**
     * @return the line where the card is placed
     */
    public AttackLineType getAttackLineType() {
        return attackLineType;
    }

    /**
     * @return the display name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the faction name as written in the card catalog
     */
    public String getFaction() {
        return faction;
    }

    /**
     * @return the declared artwork path
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * @return the declared frame path
     */
    public String getFramePath() {
        return framePath;
    }

    /**
     * @return the printed power of the card
     */
    public int getBasePower() {
        return basePower;
    }

    /**
     * @return the power shown on the card, including modifiers
     */
    public int getDisplayedPower() {
        return basePower;
    }

    /**
     * @return the flavour text
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the description of every effect of the card
     */
    public String getEffectDescription() {
        return effects().stream()
            .map(Effect::getDescription)
            .collect(Collectors.joining(", "));
    }

    /**
     * @return the description of the targets of the card effects
     */
    public String getTargetDescription() {
        return effects().stream()
            .map(Effect::getTargetDescription)
            .filter(description -> !description.isBlank())
            .distinct()
            .collect(Collectors.joining(" | "));
    }

    /**
     * @return the artwork scale used by the card renderer
     */
    public double getArtworkScale() {
        return artwork == null ? 1.1 : artwork.scale;
    }

    /**
     * @return the horizontal artwork offset
     */
    public double getArtworkOffsetX() {
        return artwork == null ? 0 : artwork.offsetX;
    }

    /**
     * @param compact whether the compact layout is used
     * @return the vertical artwork offset
     */
    public double getArtworkOffsetY(boolean compact) {
        if (artwork == null) return 0;
        return compact ? artwork.compactOffsetY : artwork.offsetY;
    }

    /**
     * @return the vertical offset of the card title
     */
    public double getArtworkTitleOffsetY() {
        return artwork == null ? 0 : artwork.titleOffsetY;
    }

    /**
     * @return a readable representation used in logs
     */
    @Override
    public String toString() {
        return name + " [" + getCardType() + "]";
    }
}
