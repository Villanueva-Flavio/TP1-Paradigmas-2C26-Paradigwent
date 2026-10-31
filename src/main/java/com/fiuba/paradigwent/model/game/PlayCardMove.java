package com.fiuba.paradigwent.model.game;

import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.player.Player;

/**
 * Move that plays a card together with its chosen target.
 */
public class PlayCardMove implements Move {
    private final Card card;
    private final Minion target;
    private final AttackLineType line;

    /**
     * @param card   card to play
     * @param target chosen minion, may be {@code null}
     * @param line   chosen line, may be {@code null}
     */
    public PlayCardMove(Card card, Minion target, AttackLineType line) {
        this.card = card;
        this.target = target;
        this.line = line;
    }

    /** {@inheritDoc} */
    @Override
    public boolean performBy(Player player, TurnActions actions) {
        return actions.play(player, card, target, line);
    }
}
