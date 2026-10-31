package com.fiuba.paradigwent.model.card;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.effect.Effect;
import com.fiuba.paradigwent.model.enums.AttackLineType;

import java.util.List;

/**
 * Effect card: it applies its effects and goes to the discard pile.
 */
public class Spell extends Card {

    /**
     * Creates a spell without effects.
     *
     * @param id             unique identifier
     * @param name           display name
     * @param stackLimit     maximum copies allowed in a deck
     * @param attackLineType line used by effects that depend on the played card
     * @param description    flavour text
     */
    public Spell(String id, String name, int stackLimit, AttackLineType attackLineType, String description) {
        this(id, name, stackLimit, attackLineType, description, List.of());
    }

    /**
     * Creates a spell with effects.
     *
     * @param id             unique identifier
     * @param name           display name
     * @param stackLimit     maximum copies allowed in a deck
     * @param attackLineType line used by effects that depend on the played card
     * @param description    flavour text
     * @param effects        effects applied when the spell is played
     */
    public Spell(String id, String name, int stackLimit, AttackLineType attackLineType,
                 String description, List<Effect> effects) {
        super(id, name, stackLimit, attackLineType, description, effects);
    }

    private Spell(Spell other) {
        super(other);
    }

    /**
     * Applies the effects of the spell and discards it.
     *
     * @param context operations available for the current play
     */
    @Override
    public void play(GameContext context) {
        applyEffects(context);
        context.discard(this);
    }

    /** {@inheritDoc} */
    @Override
    public Card copy() {
        return new Spell(this);
    }

    /** {@inheritDoc} */
    @Override
    public void classifyInto(CardPools pools) {
        pools.addSpell(this);
    }

    /** {@inheritDoc} */
    @Override
    public String getCardType() {
        return "SPELL";
    }
}
