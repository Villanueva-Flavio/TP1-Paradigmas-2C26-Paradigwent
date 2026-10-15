package com.fiuba.paradigwent.controller;

import com.fiuba.paradigwent.model.ai.AI;
import com.fiuba.paradigwent.model.ai.RandomAIStrategy;
import com.fiuba.paradigwent.model.board.Board;
import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.card.JSONCardLoader;
import com.fiuba.paradigwent.model.game.Game;
import com.fiuba.paradigwent.model.player.Deck;
import com.fiuba.paradigwent.model.player.Player;
import com.fiuba.paradigwent.model.round.Round;
import com.fiuba.paradigwent.view.game.modules.CardRace;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class GameSessionFactory {
    private final Random random;

    public GameSessionFactory() {
        this(new Random());
    }

    public GameSessionFactory(Random random) {
        this.random = random;
    }

    public Session create(CardRace selectedRace) {
        if (selectedRace == null) {
            throw new IllegalArgumentException("La facción seleccionada no puede ser nula.");
        }

        List<Card> allCards = JSONCardLoader.loadCards();
        Player player = new Player("Player",
            new Deck(new ArrayList<>(cardsForFaction(allCards, selectedRace))));
        player.setFaction(selectedRace.name());

        CardRace opponentRace = CardRace.values()[random.nextInt(CardRace.values().length)];
        AI opponent = new AI("Opponent",
            new Deck(new ArrayList<>(cardsForFaction(allCards, opponentRace))),
            new RandomAIStrategy(random));
        opponent.setFaction(opponentRace.name());

        Board board = new Board(player, opponent);
        Game game = new Game(player, opponent, board);
        game.startMatch();
        return new Session(game, player, opponent, opponentRace, new Round(player, opponent, board));
    }

    private List<Card> cardsForFaction(List<Card> cards, CardRace race) {
        return cards.stream()
            .filter(card -> race.name().equals(card.getFaction()))
            .toList();
    }

    public record Session(Game game, Player player, AI opponent,
                           CardRace opponentRace, Round round) {
    }
}
