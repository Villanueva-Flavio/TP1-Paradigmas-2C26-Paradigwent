package com.fiuba.paradigwent.model.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.ModelTestFixtures;
import com.fiuba.paradigwent.model.board.Board;
import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.enums.AttackLineType;

class PlayerRoundTest {

    /**
     *  Test: A player starts the round able to play cards and not having passed the round.
    **/
    @Test
    void playerStartsAbleToPlay() {
        Player player = ModelTestFixtures.emptyPlayer("player");

        assertFalse(player.hasPassedRound());
        assertTrue(player.canPlayThisRound());
    }

    /**
     *  Test: A player passing the round marks them as having passed and unable to play, while leaving the opponent unaffected.
    **/
    @Test
    void passingMarksOnlyThatPlayerAsFinishedForTheRound() {
        Player player = ModelTestFixtures.emptyPlayer("player");
        Player opponent = ModelTestFixtures.emptyPlayer("opponent");

        player.passRound();

        assertTrue(player.hasPassedRound());
        assertFalse(player.canPlayThisRound());
        assertFalse(opponent.hasPassedRound());
        assertTrue(opponent.canPlayThisRound());
    }

    /**
     *  Test: A player who has passed the round cannot play cards until the round is started again, at which point they can play cards again.
    **/
    @Test
    void passedPlayerCannotPlayUntilRoundStartsAgain() {
        Minion card = ModelTestFixtures.minion("card", 4, AttackLineType.MELEE);
        Player player = new Player("player", new Deck(java.util.List.of(card)));
        Player opponent = ModelTestFixtures.emptyPlayer("opponent");
        Board board = new Board(player, opponent);
        GameContext context = new GameContext(player, opponent, board, card);

        player.passRound();
        assertThrows(IllegalStateException.class, () -> player.play(context));
        assertTrue(player.getHand().contains(card));

        player.startRound();
        player.play(context);
        assertEquals(4, board.getPlayerSide().getAttackLine(AttackLineType.MELEE).calculateTotalPower());
    }

    /**
     *  Test: Passing and starting a round resets the player's state, allowing them to play cards again.
    **/
    @Test
    void passingAndStartingARoundResetsThePlayerState() {
        Player player = ModelTestFixtures.emptyPlayer("player");

        player.passRound();
        assertTrue(player.hasPassedRound());
        player.startRound();
        assertFalse(player.hasPassedRound());
        assertTrue(player.canPlayThisRound());
    }
}
