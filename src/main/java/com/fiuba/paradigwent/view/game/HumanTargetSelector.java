package com.fiuba.paradigwent.view.game;

import java.util.List;

import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.targeting.TargetSelector;
import com.fiuba.paradigwent.model.targeting.TargetSide;
import com.fiuba.paradigwent.view.game.modules.Board3DView;

/**
 * Lets the human choose the target that a card asks for, by clicking the
 * board, and then plays the card. The card tells this selector what it
 * needs, so the view never inspects the card's effects.
 */
public class HumanTargetSelector implements TargetSelector {

    /**
     * Receives the card once its target has been chosen.
     */
    @FunctionalInterface
    public interface CardPlay {
        /**
         * @param card   card to play
         * @param target chosen minion, may be {@code null}
         * @param line   chosen line, may be {@code null}
         */
        void play(Card card, Minion target, AttackLineType line);
    }

    private final Card card;
    private final List<Minion> candidates;
    private final Board3DView boardView;
    private final ActionPrompt prompt;
    private final CardPlay cardPlay;

    /**
     * @param card       card waiting for its target
     * @param candidates minions that can be chosen
     * @param boardView  board where the human clicks
     * @param prompt     banner that tells the human what to choose
     * @param cardPlay   action that plays the card with the chosen target
     */
    public HumanTargetSelector(Card card, List<Minion> candidates, Board3DView boardView,
                               ActionPrompt prompt, CardPlay cardPlay) {
        this.card = card;
        this.candidates = candidates;
        this.boardView = boardView;
        this.prompt = prompt;
        this.cardPlay = cardPlay;
    }

    /** {@inheritDoc} */
    @Override
    public void selectNothing() {
        cardPlay.play(card, null, null);
    }

    /**
     * Without minions on the board the card is played without target.
     */
    @Override
    public void selectMinion(String message, boolean optional, TargetSide preferred) {
        if (candidates.isEmpty()) {
            cardPlay.play(card, null, null);
            return;
        }
        prompt.show(message);
        boardView.setTargetSelection(candidates, target -> {
            finishSelection();
            cardPlay.play(card, target, null);
        });
    }

    /** {@inheritDoc} */
    @Override
    public void selectLine(String message) {
        prompt.show(message);
        boardView.setLineSelection(line -> {
            finishSelection();
            cardPlay.play(card, null, line);
        });
    }

    private void finishSelection() {
        prompt.hide();
        boardView.clearTargetSelection();
        boardView.clearLineSelection();
    }
}
