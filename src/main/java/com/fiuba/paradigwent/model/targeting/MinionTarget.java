package com.fiuba.paradigwent.model.targeting;

import java.util.List;

import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.enums.AttackLineType;

/**
 * Targeting of effects that act on a single minion of the board.
 */
public class MinionTarget implements Targeting {
    private final String prompt;
    private final boolean optional;
    private final TargetSide preferred;

    /**
     * Creates a minion targeting.
     *
     * @param prompt    text shown to whoever chooses the minion
     * @param optional  whether the card can be played without a minion
     * @param preferred side the effect is intended for
     */
    public MinionTarget(String prompt, boolean optional, TargetSide preferred) {
        this.prompt = prompt;
        this.optional = optional;
        this.preferred = preferred;
    }

    /** {@inheritDoc} */
    @Override
    public void request(TargetSelector selector) {
        selector.selectMinion(prompt, optional, preferred);
    }

    /**
     * A missing minion is only accepted when the target is optional or when
     * the board is empty; a chosen minion must be on the board.
     */
    @Override
    public boolean isSatisfiedBy(Minion target, AttackLineType line, List<Minion> candidates) {
        if (target == null) {
            return optional || candidates.isEmpty();
        }
        return candidates.contains(target);
    }

    /** {@inheritDoc} */
    @Override
    public Targeting orElse(Targeting other) {
        return this;
    }
}
