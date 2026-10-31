package com.fiuba.paradigwent.controller;

import com.fiuba.paradigwent.model.ai.AI;
import com.fiuba.paradigwent.model.ai.RandomAIStrategy;
import com.fiuba.paradigwent.model.board.Board;
import com.fiuba.paradigwent.model.card.JSONCardLoader;
import com.fiuba.paradigwent.model.enums.Faction;
import com.fiuba.paradigwent.model.game.Game;
import com.fiuba.paradigwent.model.game.TurnScheduler;
import com.fiuba.paradigwent.model.player.DeckBuilder;
import com.fiuba.paradigwent.model.player.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Builds everything a match needs: the decks, both players, the board and the game.
 */
public final class GameSessionFactory {
    private final Random random;

    /**
     * Creates a factory with its own source of randomness.
     */
    public GameSessionFactory() {
        this(new Random());
    }

    /**
     * @param random source of randomness used for decks, factions and turns
     */
    public GameSessionFactory(Random random) {
        this.random = random;
    }

    /**
     * Creates a match for the chosen faction. The AI gets a random faction
     * different from the human's.
     *
     * @param selectedFaction faction chosen by the human
     * @param scheduler       decides when each turn is taken
     * @return the pieces of the new match
     */
    public Session create(Faction selectedFaction, TurnScheduler scheduler) {
        if (selectedFaction == null) {
            throw new IllegalArgumentException("The selected faction cannot be null.");
        }

        DeckBuilder deckBuilder = new DeckBuilder(JSONCardLoader.loadCards(), random);
        Player player = new Player("Player", deckBuilder.buildFor(selectedFaction));
        player.setFaction(selectedFaction);

        Faction opponentFaction = randomFactionOtherThan(selectedFaction);
        AI opponent = new AI("Opponent", deckBuilder.buildFor(opponentFaction),
            new RandomAIStrategy(random));
        opponent.setFaction(opponentFaction);

        Board board = new Board(player, opponent);
        Game game = new Game(player, opponent, board, random, scheduler);
        return new Session(game, player, opponent, board);
    }

    private Faction randomFactionOtherThan(Faction excluded) {
        List<Faction> candidates = Arrays.stream(Faction.values())
            .filter(faction -> faction != excluded)
            .toList();
        return candidates.get(random.nextInt(candidates.size()));
    }

    /**
     * Pieces of a new match.
     *
     * @param game     the match
     * @param player   the human player
     * @param opponent the AI player
     * @param board    the board of the match
     */
    public record Session(Game game, Player player, AI opponent, Board board) {
    }
}
