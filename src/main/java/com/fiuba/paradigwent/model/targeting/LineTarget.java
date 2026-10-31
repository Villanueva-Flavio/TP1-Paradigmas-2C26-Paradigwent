package com.fiuba.paradigwent.model.targeting;

import java.util.List;

import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.enums.AttackLineType;

/**
 * Targeting of effects that act on one of the attack lines of the player.
 */
public class LineTarget implements Targeting {
    private final String prompt;

    /**
     * Creates a line targeting.
     *
     * @param prompt text shown to whoever chooses the line
     */
    public LineTarget(String prompt) {
        this.prompt = prompt;
    }

    /** {@inheritDoc} */
    @Override
    public void request(TargetSelector selector) {
        selector.selectLine(prompt);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSatisfiedBy(Minion target, AttackLineType line, List<Minion> candidates) {
        return line != null && line != AttackLineType.NONE;
    }

    /** {@inheritDoc} */
    @Override
    public Targeting orElse(Targeting other) {
        return this;
    }
}
