package com.fiuba.paradigwent.model.game;

import com.fiuba.paradigwent.model.player.Player;

/**
 * A complete decision for a turn: everything needed to carry it out.
 */
public interface Move {

    /**
     * Carries out the move.
     *
     * @param player  player that makes the move
     * @param actions actions available during the turn
     * @return {@code true} if the move was accepted
     */
    boolean performBy(Player player, TurnActions actions);
}
