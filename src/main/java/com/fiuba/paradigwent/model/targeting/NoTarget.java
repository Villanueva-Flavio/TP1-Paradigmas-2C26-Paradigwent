package com.fiuba.paradigwent.model.targeting;

import java.util.List;

import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.enums.AttackLineType;

/**
 * Null object for cards that do not need a target.
 */
public class NoTarget implements Targeting {

    /** {@inheritDoc} */
    @Override
    public void request(TargetSelector selector) {
        selector.selectNothing();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSatisfiedBy(Minion target, AttackLineType line, List<Minion> candidates) {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public Targeting orElse(Targeting other) {
        return other;
    }
}
