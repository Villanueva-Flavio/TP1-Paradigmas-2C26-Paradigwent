package com.fiuba.paradigwent.model.targeting;

/**
 * Receives the target request of a card. Whoever has to choose a target
 * (the human through the view, or the AI through its strategy) implements
 * this interface, so cards never need to be asked what kind of target they
 * need: they tell the selector instead (double dispatch).
 */
public interface TargetSelector {

    /**
     * Called when the card can be played without choosing anything.
     */
    void selectNothing();

    /**
     * Called when the card needs a minion currently on the board.
     *
     * @param prompt    text that explains what the minion will be used for
     * @param optional  {@code true} when the card may be played without a minion
     * @param preferred side the effect is meant for (allies for buffs, enemies for removal)
     */
    void selectMinion(String prompt, boolean optional, TargetSide preferred);

    /**
     * Called when the card needs one of the attack lines of its owner.
     *
     * @param prompt text that explains what the line will be used for
     */
    void selectLine(String prompt);
}
