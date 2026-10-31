package com.fiuba.paradigwent.model.game;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.player.Player;

/**
 * Actions a player can take during its turn. Players depend on this narrow
 * interface instead of on the whole {@link Game}.
 */
public interface TurnActions {

    /**
     * Plays a card on behalf of a player.
     *
     * @param player player that plays
     * @param card   card of the player's hand
     * @param target chosen minion, may be {@code null}
     * @param line   chosen line, may be {@code null}
     * @return {@code true} if the play was accepted
     */
    boolean play(Player player, Card card, Minion target, AttackLineType line);

    /**
     * Passes the round on behalf of a player.
     *
     * @param player player that passes
     * @return {@code true} if the pass was accepted
     */
    boolean pass(Player player);

    /**
     * @param player a player of the match
     * @return the information that player can use to decide its move
     */
    GameContext contextFor(Player player);
}
