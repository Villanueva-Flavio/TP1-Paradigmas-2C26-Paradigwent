package com.fiuba.paradigwent.model;

import java.util.List;

import com.fiuba.paradigwent.model.board.Board;
import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.player.Deck;
import com.fiuba.paradigwent.model.player.Player;

public final class ModelTestFixtures {

    private ModelTestFixtures() {
    }

    public static Minion minion(String id, int power, AttackLineType line, String description) {
        return new Minion(id, id, 1, power, line, description);
    }

    public static Player emptyPlayer(String name) {
        return new Player(name, new Deck(List.of()));
    }

    public static Board boardWithEmptyPlayers() {
        return new Board(emptyPlayer("player"), emptyPlayer("opponent"));
    }
}
