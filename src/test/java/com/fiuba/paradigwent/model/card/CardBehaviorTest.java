package com.fiuba.paradigwent.model.card;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.ModelTestFixtures;
import com.fiuba.paradigwent.model.board.Board;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.enums.WeatherType;
import com.fiuba.paradigwent.model.player.Deck;
import com.fiuba.paradigwent.model.player.Player;

class CardBehaviorTest {

    /**
     * Test: Playing a minion adds it to the correct attack line on the board and removes it from the player's hand.
    **/
    @Test
    void minionPlayAddsCardToItsAttackLine() {
        Minion minion = ModelTestFixtures.minion("minion", 7, AttackLineType.RANGED);
        Player player = new Player("player", new Deck(List.of(minion)));
        Player opponent = ModelTestFixtures.emptyPlayer("opponent");
        Board board = new Board(player, opponent);

        player.play(new GameContext(player, opponent, board, minion));

        assertEquals(7, board.getPlayerSide().getAttackLine(AttackLineType.RANGED).calculateTotalPower());
        assertTrue(player.getHand().isEmpty());
    }

    /**
     * Test: Modifying a minion's power accumulates the changes and reflects in its current power.
    **/
    @Test
    void minionPowerModifiersAccumulate() {
        Minion minion = ModelTestFixtures.minion("minion", 7, AttackLineType.MELEE);

        minion.modifyPower(-3);
        minion.modifyPower(5);

        assertEquals(9, minion.getCurrentPower());
    }

    /**
     * Test: Playing a weather card sets the board's current weather to the card's weather type and discards the card.
    **/
    @Test
    void weatherCardUsesItsWeatherTypeAndDiscard() {
        Weather weather = new Weather("weather", "Rain", 1, WeatherType.RAIN);
        Player player = ModelTestFixtures.emptyPlayer("player");
        Player opponent = ModelTestFixtures.emptyPlayer("opponent");
        Board board = new Board(player, opponent);

        weather.play(new GameContext(player, opponent, board, weather));

        assertEquals(WeatherType.RAIN, weather.getWeatherType());
        assertEquals(WeatherType.RAIN, board.getCurrentWeather());
        assertTrue(player.getDiscard().contains(weather));
    }

    /**
     *  Test: Creating a weather card with a null weather type defaults to clear weather and no attack line.
    **/
    @Test
    void nullWeatherDefaultsToClear() {
        Weather weather = new Weather("weather", "Clear", 1, null);

        assertEquals(WeatherType.CLEAR, weather.getWeatherType());
        assertEquals(AttackLineType.NONE, weather.getAttackLineType());
    }
}
