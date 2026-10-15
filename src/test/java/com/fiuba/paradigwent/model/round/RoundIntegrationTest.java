package com.fiuba.paradigwent.model.round;

import com.fiuba.paradigwent.model.board.Board;
import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.enums.Result;
import com.fiuba.paradigwent.model.player.Deck;
import com.fiuba.paradigwent.model.player.Player;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the {@link Round} class.
 * These tests exercise the full round flow, turn management, and result evaluation
 * using real {@link Minion} cards and a real {@link Board}.
 */
class RoundIntegrationTest {

    /**
     * Tests a complete round in which Player 1 wins.
     * Both players play one card and then pass. Verifies that cards are placed
     * on the correct sides of the board, that the round finishes, that the result
     * is {@link Result#PLAYER1_WIN}, and that the board is properly cleaned up
     * after discarding.
     */
    @Test
    void testFullRoundFlowWithCardsAndPlayer1Winner() {
        List<Minion> deckP1 = createMinionDeck(10, 5, AttackLineType.MELEE);
        List<Minion> deckP2 = createMinionDeck(10, 2, AttackLineType.RANGED);

        Player p1 = new Player("Player 1", new Deck(new ArrayList<>(deckP1)));
        Player p2 = new Player("Player 2", new Deck(new ArrayList<>(deckP2)));
        Board board = new Board(p1, p2);

        Round round = new Round(p1, p2, board);

        Minion cardP1 = (Minion) p1.getHand().get(0);
        round.playCard(cardP1);

        assertFalse(p1.getHand().contains(cardP1));
        assertEquals(5, board.getPlayerSide().totalPower());
        assertEquals(0, board.getOpponentSide().totalPower());

        assertEquals(p2, round.getCurrentPlayer());
        Minion cardP2 = (Minion) p2.getHand().get(0);
        round.playCard(cardP2);

        assertEquals(5, board.getPlayerSide().totalPower());
        assertEquals(2, board.getOpponentSide().totalPower());

        assertEquals(p1, round.getCurrentPlayer());
        round.passTurn();
        round.passTurn();

        assertTrue(round.isFinished());
        assertEquals(Result.PLAYER1_WIN, round.getRoundResult());

        board.discard();
        assertEquals(1, p1.getDiscard().size());
        assertEquals(1, p2.getDiscard().size());
        assertEquals(0, board.getPlayerSide().totalPower());
        assertEquals(0, board.getOpponentSide().totalPower());
    }

    /**
     * Tests a round in which Player 2 wins.
     * Player 2 plays a higher-power minion than Player 1, so after both pass,
     * the round result must be {@link Result#PLAYER2_WIN}.
     */
    @Test
    void testPlayer2WinsRound() {
        Player p1 = new Player("Player 1",
                new Deck(new ArrayList<>(createMinionDeck(10, 2, AttackLineType.MELEE))));
        Player p2 = new Player("Player 2",
                new Deck(new ArrayList<>(createMinionDeck(10, 7, AttackLineType.RANGED))));
        Board board = new Board(p1, p2);
        Round round = new Round(p1, p2, board);

        Minion cardP1 = (Minion) p1.getHand().get(0);
        round.playCard(cardP1);

        assertEquals(p2, round.getCurrentPlayer());
        Minion cardP2 = (Minion) p2.getHand().get(0);
        round.playCard(cardP2);

        round.passTurn();
        round.passTurn();

        assertTrue(round.isFinished());
        assertEquals(2, board.getPlayerSide().totalPower());
        assertEquals(7, board.getOpponentSide().totalPower());
        assertEquals(Result.PLAYER2_WIN, round.getRoundResult());
    }

    /**
     * Tests a round that ends in a draw.
     * Both players play minions with the same power, so after both pass,
     * the round result must be {@link Result#DRAW}.
     */
    @Test
    void testRoundEndsInDraw() {
        Player p1 = new Player("Player 1",
                new Deck(new ArrayList<>(createMinionDeck(10, 4, AttackLineType.MELEE))));
        Player p2 = new Player("Player 2",
                new Deck(new ArrayList<>(createMinionDeck(10, 4, AttackLineType.RANGED))));
        Board board = new Board(p1, p2);
        Round round = new Round(p1, p2, board);

        Minion cardP1 = (Minion) p1.getHand().get(0);
        round.playCard(cardP1);

        assertEquals(p2, round.getCurrentPlayer());
        Minion cardP2 = (Minion) p2.getHand().get(0);
        round.playCard(cardP2);

        round.passTurn();
        round.passTurn();

        assertTrue(round.isFinished());
        assertEquals(4, board.getPlayerSide().totalPower());
        assertEquals(4, board.getOpponentSide().totalPower());
        assertEquals(Result.DRAW, round.getRoundResult());
    }

    /**
     * Tests that no card can be played after the round has finished.
     * Once both players have passed, {@link Round#playCard} must be a no-op.
     */
    @Test
    void testCannotPlayAfterRoundIsFinished() {
        Player p1 = new Player("Player 1",
                new Deck(new ArrayList<>(createMinionDeck(10, 5, AttackLineType.MELEE))));
        Player p2 = new Player("Player 2",
                new Deck(new ArrayList<>(createMinionDeck(10, 2, AttackLineType.RANGED))));
        Board board = new Board(p1, p2);
        Round round = new Round(p1, p2, board);

        Minion cardP1 = (Minion) p1.getHand().get(0);
        round.playCard(cardP1);
        round.passTurn();

        round.passTurn();

        assertTrue(round.isFinished());

        Minion extra = (Minion) p1.getHand().get(0);
        round.playCard(extra);
        assertEquals(5, board.getPlayerSide().totalPower());
        assertTrue(p1.getHand().contains(extra));
    }

    /**
     * Tests that attempting to evaluate the result of an ongoing round
     * throws an {@link IllegalStateException}.
     */
    @Test
    void testCannotEvaluateResultOfOngoingRound() {
        Player p1 = new Player("Player 1",
                new Deck(new ArrayList<>(createMinionDeck(10, 5, AttackLineType.MELEE))));
        Player p2 = new Player("Player 2",
                new Deck(new ArrayList<>(createMinionDeck(10, 2, AttackLineType.RANGED))));
        Board board = new Board(p1, p2);
        Round round = new Round(p1, p2, board);

        assertThrows(IllegalStateException.class, round::getRoundResult);
    }

    /**
     * Creates a list of {@link Minion} cards for testing purposes.
     *
     * @param amount   number of minions to create
     * @param power    base power of each minion
     * @param lineType attack line type of each minion
     * @return a list of minions with the given characteristics
     */
    private List<Minion> createMinionDeck(int amount, int power, AttackLineType lineType) {
        List<Minion> list = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            list.add(new Minion("minion-" + i, "Minion " + i, 1, power, lineType, null));
        }
        return list;
    }
}