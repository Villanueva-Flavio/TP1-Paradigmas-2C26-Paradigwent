package com.fiuba.paradigwent.model.targeting;

import java.util.List;

import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.enums.AttackLineType;

/**
 * Describes what a card needs to be chosen before being played.
 * Each implementation knows how to request its target and how to validate
 * the chosen one, so nobody has to inspect the concrete effect.
 */
public interface Targeting {

    /**
     * Tells the selector what has to be chosen.
     *
     * @param selector the object in charge of choosing the target
     */
    void request(TargetSelector selector);

    /**
     * Validates a chosen target.
     *
     * @param target     chosen minion, or {@code null} when none was chosen
     * @param line       chosen line, or {@code null} when none was chosen
     * @param candidates minions currently on the board
     * @return {@code true} if the choice satisfies this targeting
     */
    boolean isSatisfiedBy(Minion target, AttackLineType line, List<Minion> candidates);

    /**
     * Combines two targetings keeping the first one that needs a target.
     *
     * @param other targeting used when this one needs nothing
     * @return the targeting that must be requested
     */
    Targeting orElse(Targeting other);
}
