package com.fiuba.paradigwent.model.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import com.fiuba.paradigwent.model.ModelTestFixtures;
import com.fiuba.paradigwent.model.ai.AI;
import com.fiuba.paradigwent.model.ai.RandomAIStrategy;
import com.fiuba.paradigwent.model.board.Board;
import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.card.Spell;
import com.fiuba.paradigwent.model.effect.DestroyCreatureEffect;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.enums.Result;
import com.fiuba.paradigwent.model.event.GameListener;
import com.fiuba.paradigwent.model.player.Deck;
import com.fiuba.paradigwent.model.player.Player;

/**
 * The match advances by itself: the AI takes its turns and rounds are
 * resolved without any user interface.
 */
class GameTest {

    @Test
    void aWholeMatchCanBePlayedWithoutTheUserInterface() {
        Player human = new Player("human", minionDeck(4));
        AI ai = new AI("ai", minionDeck(6), new RandomAIStrategy(new LastChoiceRandom()));
        Game game = new Game(human, ai, new Board(human, ai), new Random(42), new ImmediateTurnScheduler());
        RecordingListener events = new RecordingListener();
        game.addListener(events);

        game.startMatch();
        while (!game.isFinished()) {
            assertTrue(game.isTurnOf(human));
            assertTrue(game.pass(human));
        }

        assertEquals(0, human.getLives());
        assertEquals(Result.PLAYER2_WIN, game.getMatchResult());
        assertEquals(List.of(Result.PLAYER2_WIN), events.matchResults);
        assertEquals(Player.INITIAL_LIVES, events.roundResults.size());
    }

    @Test
    void theAiPlaysItsTurnsWhenItOpensTheRound() {
        Player human = new Player("human", minionDeck(4));
        AI ai = new AI("ai", minionDeck(6), new RandomAIStrategy(new LastChoiceRandom()));
        Game game = new Game(human, ai, new Board(human, ai), new FixedRandom(false), new ImmediateTurnScheduler());
        RecordingListener events = new RecordingListener();
        game.addListener(events);

        game.startMatch();

        assertSame(ai, events.startingPlayers.get(0));
        assertEquals(List.of(ai), events.cardPlayers);
        assertTrue(game.isTurnOf(human));
    }

    @Test
    void theStartingPlayerIsDrawnEveryRound() {
        Player human = new Player("human", minionDeck(4));
        AI ai = new AI("ai", minionDeck(6), new RandomAIStrategy(new Random(1)));
        Game game = new Game(human, ai, new Board(human, ai), new FixedRandom(true), new ImmediateTurnScheduler());
        RecordingListener events = new RecordingListener();
        game.addListener(events);

        game.startMatch();

        assertSame(human, events.startingPlayers.get(0));
    }

    @Test
    void theAiChoosesTheTargetOfItsCardsByItself() {
        Player human = new Player("human", minionDeck(5));
        AI ai = new AI("ai", new Deck(destroySpells()), new RandomAIStrategy(new LastChoiceRandom()));
        Board board = new Board(human, ai);
        Game game = new Game(human, ai, board, new FixedRandom(true), new ImmediateTurnScheduler());
        RecordingListener events = new RecordingListener();
        game.addListener(events);
        game.startMatch();
        Card humanMinion = human.getHand().get(0);

        assertTrue(game.play(human, humanMinion, null, null));

        assertTrue(board.minionsOf(human).isEmpty());
        assertTrue(human.getDiscard().contains(humanMinion));
        assertEquals(1, events.destroyed.size());
        assertTrue(game.isTurnOf(human));
    }

    @Test
    void playsOutOfTurnOrWithAnInvalidTargetAreRejected() {
        Player human = new Player("human", minionDeck(5));
        Player other = new Player("other", new Deck(destroySpells()));
        Board board = new Board(human, other);
        Game game = new Game(human, other, board, new FixedRandom(true), new ImmediateTurnScheduler());
        game.startMatch();
        Card humanMinion = human.getHand().get(0);
        game.play(human, humanMinion, null, null);
        Card spell = other.getHand().get(0);

        assertFalse(game.play(human, human.getHand().get(0), null, null));
        assertFalse(game.play(other, spell, null, null));
        assertTrue(game.play(other, spell, board.minionsOf(human).get(0), null));
    }

    @Test
    void surrenderingEndsTheMatchForTheOtherPlayer() {
        Player human = new Player("human", minionDeck(5));
        AI ai = new AI("ai", minionDeck(5), new RandomAIStrategy(new Random(5)));
        Game game = new Game(human, ai, new Board(human, ai), new FixedRandom(true), new ImmediateTurnScheduler());
        RecordingListener events = new RecordingListener();
        game.addListener(events);
        game.startMatch();

        game.surrender(human);

        assertTrue(game.isFinished());
        assertEquals(List.of(Result.PLAYER2_WIN), events.matchResults);
        assertFalse(game.pass(human));
    }

    private static Deck minionDeck(int power) {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            cards.add(ModelTestFixtures.minion("minion-" + i, power, AttackLineType.MELEE, null));
        }
        return new Deck(cards);
    }

    private static List<Card> destroySpells() {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            cards.add(new Spell("destroy-" + i, "Destroy " + i, 1, AttackLineType.NONE, null,
                List.of(new DestroyCreatureEffect(AttackLineType.NONE))));
        }
        return cards;
    }

    /** Random source that always returns the same coin. */
    private static final class FixedRandom extends Random {
        private final boolean coin;

        FixedRandom(boolean coin) {
            this.coin = coin;
        }

        @Override
        public boolean nextBoolean() {
            return coin;
        }
    }

    /** Random source that always picks the last option (so the AI never passes at random). */
    private static final class LastChoiceRandom extends Random {
        @Override
        public int nextInt(int bound) {
            return bound - 1;
        }
    }

    private static final class RecordingListener implements GameListener {
        private final List<Player> startingPlayers = new ArrayList<>();
        private final List<Player> cardPlayers = new ArrayList<>();
        private final List<Minion> destroyed = new ArrayList<>();
        private final List<Result> roundResults = new ArrayList<>();
        private final List<Result> matchResults = new ArrayList<>();

        @Override
        public void roundStarted(Player startingPlayer) {
            startingPlayers.add(startingPlayer);
        }

        @Override
        public void cardPlayed(Player player, Card card) {
            cardPlayers.add(player);
        }

        @Override
        public void minionDestroyed(Minion minion) {
            destroyed.add(minion);
        }

        @Override
        public void roundFinished(Result result) {
            roundResults.add(result);
        }

        @Override
        public void matchFinished(Result result) {
            matchResults.add(result);
        }
    }
}
