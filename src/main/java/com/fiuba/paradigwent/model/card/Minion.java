package com.fiuba.paradigwent.model.card;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.board.PlayerSide;
import com.fiuba.paradigwent.model.effect.Effect;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.event.EffectListener;

import java.util.List;

/**
 * Creature card: it stays on its attack line adding its power and may carry
 * an ability (effects applied when it is deployed).
 */
public class Minion extends Card {

    private int powerModifier;

    /**
     * Creates a minion without abilities.
     *
     * @param id             unique identifier
     * @param name           display name
     * @param stackLimit     maximum copies allowed in a deck
     * @param basePower      printed power
     * @param attackLineType line where the minion is deployed
     * @param description    flavour text
     */
    public Minion(String id, String name, int stackLimit, int basePower,
                  AttackLineType attackLineType, String description) {
        this(id, name, stackLimit, basePower, attackLineType, description, List.of());
    }

    /**
     * Creates a minion with abilities.
     *
     * @param id             unique identifier
     * @param name           display name
     * @param stackLimit     maximum copies allowed in a deck
     * @param basePower      printed power
     * @param attackLineType line where the minion is deployed
     * @param description    flavour text
     * @param effects        abilities applied when the minion is deployed
     */
    public Minion(String id, String name, int stackLimit, int basePower,
                  AttackLineType attackLineType, String description, List<Effect> effects) {
        super(id, name, stackLimit, attackLineType, description, effects);
        this.basePower = basePower;
        this.powerModifier = 0;
    }

    private Minion(Minion other) {
        super(other);
        this.powerModifier = 0;
    }

    /**
     * @return the base power plus every modifier applied to the minion
     */
    public int getCurrentPower() {
        return basePower + powerModifier;
    }

    /** {@inheritDoc} */
    @Override
    public int getDisplayedPower() {
        return getCurrentPower();
    }

    /**
     * Adds a modifier to the power of the minion.
     *
     * @param modifier amount to add (negative values weaken the minion)
     */
    public void modifyPower(int modifier) {
        this.powerModifier += modifier;
    }

    /**
     * Removes every modifier. Called when the minion leaves the board, so a
     * destroyed or discarded minion comes back with its printed power.
     */
    public void resetPower() {
        this.powerModifier = 0;
    }

    /**
     * Deploys the minion on its line and then triggers its abilities.
     *
     * @param context operations available for the current play
     */
    @Override
    public void play(GameContext context) {
        context.deploy(this);
        applyEffects(context);
    }

    /** {@inheritDoc} */
    @Override
    public Card copy() {
        return new Minion(this);
    }

    /**
     * Minions with abilities and common minions go to different pools.
     */
    @Override
    public void classifyInto(CardPools pools) {
        if (hasEffects()) {
            pools.addAbilityMinion(this);
        } else {
            pools.addCommonMinion(this);
        }
    }

    /**
     * A revived minion goes straight back to its attack line.
     */
    @Override
    public boolean reviveOnto(PlayerSide side, EffectListener listener) {
        side.deploy(this);
        listener.minionRevived(this);
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public String getCardType() {
        return "MINION";
    }
}
