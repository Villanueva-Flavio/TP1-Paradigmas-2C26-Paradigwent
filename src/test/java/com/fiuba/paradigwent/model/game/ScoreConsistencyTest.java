package com.fiuba.paradigwent.model.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import com.fiuba.paradigwent.model.ModelTestFixtures;
import com.fiuba.paradigwent.model.ai.AI;
import com.fiuba.paradigwent.model.ai.RandomAIStrategy;
import com.fiuba.paradigwent.model.board.AttackLine;
import com.fiuba.paradigwent.model.board.Board;
import com.fiuba.paradigwent.model.board.PlayerSide;
import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.card.JSONCardLoader;
import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.enums.Faction;
import com.fiuba.paradigwent.model.player.DeckBuilder;
import com.fiuba.paradigwent.model.player.Player;

/**
 * The score of each player must always be the sum of what its visible lines
 * show, so the score panel never disagrees with the table.
 */
class ScoreConsistencyTest {
    private static final List<AttackLineType> VISIBLE_LINES =
        List.of(AttackLineType.MELEE, AttackLineType.RANGED, AttackLineType.SIEGE);

    private final List<Card> catalog = JSONCardLoader.loadCards();

    @Test
    void theScoreMatchesTheVisibleLinesDuringWholeMatches() {
        for (int seed = 0; seed < 60; seed++) {
            Random random = new Random(seed);
            DeckBuilder builder = new DeckBuilder(catalog, random);
            Faction[] factions = Faction.values();
            AI first = new AI("first", builder.buildFor(factions[seed % factions.length]),
                new RandomAIStrategy(random));
            AI second = new AI("second", builder.buildFor(factions[(seed + 1) % factions.length]),
                new RandomAIStrategy(random));
            Board board = new Board(first, second);
            TurnScheduler checkingScheduler = turn -> {
                assertConsistent(board, first);
                assertConsistent(board, second);
                turn.run();
            };
            Game game = new Game(first, second, board, random, checkingScheduler);

            game.startMatch();

            assertTrue(game.isFinished(), "seed " + seed);
        }
    }

    @Test
    void aDestroyedMinionComesBackWithItsPrintedPower() {
        Player player = ModelTestFixtures.emptyPlayer("player");
        Board board = new Board(player, ModelTestFixtures.emptyPlayer("opponent"));
        Minion minion = ModelTestFixtures.minion("buffed", 3, AttackLineType.MELEE, null);
        board.deploy(player, minion);
        minion.modifyPower(4);

        board.destroy(minion);

        assertEquals(3, minion.getCurrentPower());
    }

    private void assertConsistent(Board board, Player player) {
        PlayerSide side = board.getSideFor(player);
        int visible = 0;
        for (AttackLineType type : VISIBLE_LINES) {
            AttackLine line = side.getAttackLine(type);
            for (Minion minion : line.getMinions()) {
                visible += line.displayedPowerOf(minion);
            }
        }
        assertTrue(side.getAttackLine(AttackLineType.NONE).getMinions().isEmpty());
        assertEquals(board.calculateTotalPower(player), visible);
    }
}
