package com.fiuba.paradigwent.model.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.fiuba.paradigwent.model.ModelTestFixtures;
import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.enums.WeatherType;
import com.fiuba.paradigwent.model.player.Player;

class BoardAndPlayerSideTest {

    /**
     *  Test: A player side creates one attack line for each type of attack line.
    **/
    @Test
    void playerSideCreatesOneLineForEachAttackLineType() {
        PlayerSide side = new PlayerSide(ModelTestFixtures.emptyPlayer("player"));

        for (AttackLineType type : AttackLineType.values()) {
            assertEquals(type, side.getAttackLine(type).getType());
        }
    }

    /**
     *  Test: A player side calculates its total power across all attack lines, including any horn effects.
    **/
    @Test
    void playerSideCalculatesPowerAcrossLines() {
        PlayerSide side = new PlayerSide(ModelTestFixtures.emptyPlayer("player"));
        AttackLine melee = side.getAttackLine(AttackLineType.MELEE);
        AttackLine ranged = side.getAttackLine(AttackLineType.RANGED);
        melee.addMinion(ModelTestFixtures.minion("melee", 5, AttackLineType.MELEE));
        ranged.addMinion(ModelTestFixtures.minion("ranged", 4, AttackLineType.RANGED));
        ranged.activateHorn();

        assertEquals(13, side.totalPower());
    }

    /**
     *  Test: Discarding a player side clears all attack lines, removes any horn effects, and sends all minions to the owner's discard pile.
    **/
    @Test
    void discardingPlayerSideClearsLinesRemovesHornsAndSendsCardsToOwner() {
        Player player = ModelTestFixtures.emptyPlayer("player");
        PlayerSide side = new PlayerSide(player);
        AttackLine line = side.getAttackLine(AttackLineType.SIEGE);
        Minion card = ModelTestFixtures.minion("card", 6, AttackLineType.SIEGE);
        line.addMinion(card);
        line.activateHorn();

        side.discard();

        assertEquals(0, line.calculateTotalPower());
        assertFalse(line.hasHorn());
        assertTrue(player.getDiscard().contains(card));
    }

    /**
     *  Test: A board owns both player sides and resets the weather to clear when discarded.
    **/
    @Test
    void boardOwnsBothSidesAndResetsWeatherOnDiscard() {
        Player player = ModelTestFixtures.emptyPlayer("player");
        Player opponent = ModelTestFixtures.emptyPlayer("opponent");
        Board board = new Board(player, opponent);

        assertSame(player, board.getPlayerSide().getOwner());
        assertSame(opponent, board.getOpponentSide().getOwner());
        board.setCurrentWeather(WeatherType.FROST);
        board.discard();

        assertEquals(WeatherType.CLEAR, board.getCurrentWeather());
    }

    /**
     *  Test: The strongest minion on a player side is determined by its current power, and returns null if there are no minions.
    **/
    @Test
    void strongestMinionReturnsNullWhenBothSidesAreEmpty() {
        Board board = ModelTestFixtures.boardWithEmptyPlayers();

        assertNull(board.getPlayerSide().getStrongestMinion());
        assertNull(board.getOpponentSide().getStrongestMinion());
    }
}
