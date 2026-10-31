package com.fiuba.paradigwent.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Random;

import org.junit.jupiter.api.Test;

import com.fiuba.paradigwent.model.enums.Faction;
import com.fiuba.paradigwent.model.game.ImmediateTurnScheduler;
import com.fiuba.paradigwent.model.player.DeckBuilder;
import com.fiuba.paradigwent.model.player.Player;

class GameSessionFactoryTest {

    @Test
    void theAiNeverGetsTheFactionOfTheHuman() {
        for (int seed = 0; seed < 20; seed++) {
            GameSessionFactory factory = new GameSessionFactory(new Random(seed));
            for (Faction faction : Faction.values()) {
                GameSessionFactory.Session session = factory.create(faction, new ImmediateTurnScheduler());

                assertEquals(faction, session.player().getFaction());
                assertNotEquals(faction, session.opponent().getFaction());
            }
        }
    }

    @Test
    void bothPlayersStartWithAFullDeckAndThreeLives() {
        GameSessionFactory.Session session = new GameSessionFactory(new Random(9))
            .create(Faction.ORC, new ImmediateTurnScheduler());
        int remaining = DeckBuilder.DECK_SIZE - Player.INITIAL_HAND_SIZE;

        assertEquals(Player.INITIAL_HAND_SIZE, session.player().handSize());
        assertEquals(remaining, session.player().getDeckSize());
        assertEquals(remaining, session.opponent().getDeckSize());
        assertEquals(3, session.player().getLives());
        assertEquals(3, session.opponent().getLives());
    }
}
