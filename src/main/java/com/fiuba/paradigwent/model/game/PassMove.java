package com.fiuba.paradigwent.model.game;

import com.fiuba.paradigwent.model.player.Player;

/**
 * Move that passes the round.
 */
public class PassMove implements Move {

    /** {@inheritDoc} */
    @Override
    public boolean performBy(Player player, TurnActions actions) {
        return actions.pass(player);
    }
}
