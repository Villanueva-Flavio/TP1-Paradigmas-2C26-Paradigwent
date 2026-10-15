package com.fiuba.paradigwent.model.effect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.ModelTestFixtures;
import com.fiuba.paradigwent.model.board.Board;
import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.enums.WeatherType;
import com.fiuba.paradigwent.model.player.Deck;
import com.fiuba.paradigwent.model.player.Player;

class EffectBehaviorTest {

    /**
     *  Test: The effect factory creates a horn effect for a specific attack line, and applying it activates the horn on that line.
    **/
    @Test
    void factoryCreatesHornForAnExplicitLine() {
        Board board = ModelTestFixtures.boardWithEmptyPlayers();
        Player player = board.getPlayerSide().getOwner();
        Player opponent = board.getOpponentSide().getOwner();
        GameContext context = context(player, opponent, board, ModelTestFixtures.minion("horn", 1, AttackLineType.MELEE, null));

        EffectFactory.createEffect("HORN", "SIEGE").apply(context);

        assertTrue(board.getPlayerSide().getAttackLine(AttackLineType.SIEGE).hasHorn());
        assertFalse(board.getPlayerSide().getAttackLine(AttackLineType.MELEE).hasHorn());
    }

    /**
     *  Test: The effect factory creates a horn effect without specifying an attack line, and applying it activates the horn on the line of the played card.
    **/
    @Test
    void hornWithoutLineUsesPlayedCardLine() {
        Board board = ModelTestFixtures.boardWithEmptyPlayers();
        Player player = board.getPlayerSide().getOwner();
        Player opponent = board.getOpponentSide().getOwner();
        GameContext context = context(player, opponent, board, ModelTestFixtures.minion("horn", 1, AttackLineType.RANGED, null));

        EffectFactory.createEffect("DUPLICATELINE", null).apply(context);

        assertTrue(board.getPlayerSide().getAttackLine(AttackLineType.RANGED).hasHorn());
    }

    @Test
    void hornIsImplementedByDuplicateLineEffect() {
        assertInstanceOf(
            DuplicateLineEffect.class,
            EffectFactory.createEffect("HORN", "MELEE"));
    }

    @Test
    void duplicateLineWithoutParameterUsesSelectedLine() {
        Board board = ModelTestFixtures.boardWithEmptyPlayers();
        Player player = board.getPlayerSide().getOwner();
        Player opponent = board.getOpponentSide().getOwner();
        Minion playedCard = ModelTestFixtures.minion("horn", 1, AttackLineType.MELEE, null);
        GameContext context = new GameContext(
            player, opponent, board, playedCard, null, AttackLineType.SIEGE);

        EffectFactory.createEffect("DUPLICATELINE", null).apply(context);

        assertTrue(board.getPlayerSide().getAttackLine(AttackLineType.SIEGE).hasHorn());
        assertFalse(board.getPlayerSide().getAttackLine(AttackLineType.MELEE).hasHorn());
    }

    /**
     *  Test: The effect factory creates a weather effect for a specific weather type, and applying it sets the board's current weather to that type.
    **/
    @Test
    void weatherEffectChangesBoardWeather() {
        Board board = ModelTestFixtures.boardWithEmptyPlayers();
        Player player = board.getPlayerSide().getOwner();
        Player opponent = board.getOpponentSide().getOwner();
        GameContext context = context(player, opponent, board, ModelTestFixtures.minion("weather", 1, AttackLineType.NONE, null));

        new WeatherEffect(WeatherType.FROST).apply(context);

        assertEquals(WeatherType.FROST, board.getCurrentWeather());
    }

    @Test
    void weatherSetsAffectedLinesToOneForBothSidesAndClearRemovesIt() {
        Board board = ModelTestFixtures.boardWithEmptyPlayers();
        Player player = board.getPlayerSide().getOwner();
        Player opponent = board.getOpponentSide().getOwner();
        Minion playerMelee = ModelTestFixtures.minion("player-melee", 6, AttackLineType.MELEE, null);
        Minion opponentMelee = ModelTestFixtures.minion("opponent-melee", 8, AttackLineType.MELEE, null);
        board.getPlayerSide().getAttackLine(AttackLineType.MELEE).addMinion(playerMelee);
        board.getOpponentSide().getAttackLine(AttackLineType.MELEE).addMinion(opponentMelee);

        new WeatherEffect(WeatherType.FROST).apply(context(player, opponent, board, playerMelee));

        assertEquals(1, board.getPlayerSide().getAttackLine(AttackLineType.MELEE).calculateTotalPower());
        assertEquals(1, board.getOpponentSide().getAttackLine(AttackLineType.MELEE).calculateTotalPower());

        new WeatherEffect(WeatherType.CLEAR).apply(context(player, opponent, board, playerMelee));

        assertEquals(6, board.getPlayerSide().getAttackLine(AttackLineType.MELEE).calculateTotalPower());
        assertEquals(8, board.getOpponentSide().getAttackLine(AttackLineType.MELEE).calculateTotalPower());
    }

    /**
     *   Test: The effect factory creates a steal card effect that draws up to a specified number of cards from the player's deck, and applying it adds those cards to the player's hand.
    **/
    @Test
    void stealCardEffectMovesCardsFromOpponentHand() {
        List<Minion> cards = new java.util.ArrayList<>();
        for (int i = 0; i < 12; i++) {
            cards.add(ModelTestFixtures.minion("card-" + i, i, AttackLineType.MELEE, null));
        }
        Player player = ModelTestFixtures.emptyPlayer("player");
        Player opponent = new Player("opponent", new Deck(new java.util.ArrayList<>(cards)));
        Board board = new Board(player, opponent);
        int handBefore = player.getHand().size();
        int opponentHandBefore = opponent.getHand().size();

        new StealCardEffect(5).apply(context(player, opponent, board, cards.get(0)));

        assertEquals(handBefore + 5, player.getHand().size());
        assertEquals(opponentHandBefore - 5, opponent.getHand().size());
    }

    /**
     *  Test: The effect factory creates a revive effect that moves the most recently discarded card back to the player's hand, and applying it performs this action.
    **/
    @Test
    void reviveEffectMovesTheMostRecentDiscardedCardToHand() {
        Player player = ModelTestFixtures.emptyPlayer("player");
        Player opponent = ModelTestFixtures.emptyPlayer("opponent");
        Board board = new Board(player, opponent);
        Minion card = ModelTestFixtures.minion("discarded", 3, AttackLineType.MELEE, null);
        player.sendToDiscard(List.of(card));

        new ReviveEffect(1).apply(context(player, opponent, board, card));

        assertTrue(player.getHand().contains(card));
        assertFalse(player.getDiscard().contains(card));
    }

    /**
     *  Test: The effect factory rejects null and unknown effect types.
    **/
    @Test
    void factoryRejectsNullAndUnknownEffects() {
        assertThrows(IllegalArgumentException.class, () -> EffectFactory.createEffect(null, null));
        assertThrows(IllegalArgumentException.class, () -> EffectFactory.createEffect("UNKNOWN", null));
    }

    private static GameContext context(Player player, Player opponent, Board board, Minion playedCard) {
        return new GameContext(player, opponent, board, playedCard);
    }
}
