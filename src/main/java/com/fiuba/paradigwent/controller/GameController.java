package com.fiuba.paradigwent.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.fiuba.paradigwent.model.board.Board;
import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.card.JSONCardLoader;
import com.fiuba.paradigwent.model.enums.Result;
import com.fiuba.paradigwent.model.game.Game;
import com.fiuba.paradigwent.model.ai.AI;
import com.fiuba.paradigwent.model.ai.AIStrategy;
import com.fiuba.paradigwent.model.player.Deck;
import com.fiuba.paradigwent.model.player.Player;
import com.fiuba.paradigwent.model.round.Round;
import com.fiuba.paradigwent.view.game.modules.CardRace;

/**
 * Application controller for the match shown by the game view.
 */
public final class GameController {
    private final Game game;
    private Round round;
    private final BoardController boardController;
    private final Player player;
    private final AI opponent;
    private final CardRace opponentRace;

    public GameController(CardRace selectedRace) {
        List<Card> cards = JSONCardLoader.loadCards().stream()
            .filter(card -> selectedRace.name().equals(card.getFaction()))
            .toList();
        player = new Player("Player", new Deck(new ArrayList<>(cards)));
        Random random = new Random();
        opponentRace = CardRace.values()[random.nextInt(CardRace.values().length)];
        AIStrategy firstCardStrategy = (hand, context) -> {
            List<Card> handCards = hand.getHand();
            return handCards.isEmpty() ? null : handCards.get(0);
        };
        List<Card> opponentCards = JSONCardLoader.loadCards().stream()
            .filter(card -> opponentRace.name().equals(card.getFaction()))
            .toList();
        opponent = new AI("Opponent",
            new Deck(new ArrayList<>(opponentCards)),
            firstCardStrategy);
        Board board = new Board(player, opponent);
        game = new Game(player, opponent, board);
        game.startMatch();
        round = new Round(player, opponent, board);
        boardController = new BoardController(board);

        GameStatePrinter.printInitialSituation(board);
        GameStatePrinter.printTurn(round.getCurrentPlayer().getName());
    }

    public Board getBoard() {
        return boardController.getBoard();
    }

    public Game getGame() {
        return game;
    }

    public List<Card> getPlayerHand() {
        return player.getHand();
    }

    public boolean playCard(Card card) {
        if (card == null) {
            GameStatePrinter.printRejected("la carta es nula");
            return false;
        }
        if (round.isFinished()) {
            GameStatePrinter.printRejected("la ronda ya terminó");
            return false;
        }
        if (round.getCurrentPlayer() != player) {
            GameStatePrinter.printRejected("no es el turno del jugador");
            return false;
        }
        if (!player.getHand().contains(card)) {
            GameStatePrinter.printRejected("la carta no está en la mano");
            return false;
        }

        round.playCard(card);
        boardController.printSituation(player.getName(), card.getName());
        GameStatePrinter.printTurn(round.getCurrentPlayer().getName());
        return true;
    }

    public boolean playOpponentCard() {
        if (round.isFinished() || round.getCurrentPlayer() != opponent) {
            return false;
        }
        if (opponent.getHand().isEmpty()) {
            round.passTurn();
            return finishRoundIfNeeded();
        }
        Card card = opponent.getHand().get(0);
        round.playCard(card);
        boardController.printSituation(opponent.getName(), card.getName());
        GameStatePrinter.printTurn(round.getCurrentPlayer().getName());
        return true;
    }

    public boolean passTurn() {
        if (round.isFinished() || round.getCurrentPlayer() != player) {
            return false;
        }
        round.passTurn();
        if (!round.isFinished() && round.getCurrentPlayer() == opponent) {
            round.passTurn();
        }
        return finishRoundIfNeeded();
    }

    private boolean finishRoundIfNeeded() {
        if (!round.isFinished()) {
            return false;
        }
        Result result = round.getRoundResult();
        boolean resolved = game.resolveRound(round);
        if (resolved) {
            System.out.printf("[GAME] Ronda finalizada: %s%n", result);
            System.out.printf("[GAME] Vidas - %s: %d | %s (%s): %d%n",
                player.getName(), player.getLives(), opponent.getName(),
                opponentRace, opponent.getLives());
            if (!game.isFinished()) {
                System.out.printf("[GAME] Nueva ronda: %s robó 1 carta (mano=%d, mazo=%d); "
                        + "%s robó 1 carta (mano=%d, mazo=%d)%n",
                    player.getName(), player.getHand().size(), player.getDeckSize(),
                    opponent.getName(), opponent.getHand().size(), opponent.getDeckSize());
                round = new Round(player, opponent, game.getBoard());
                GameStatePrinter.printTurn(round.getCurrentPlayer().getName());
            }
        }
        return resolved;
    }

    public void surrender() {
        if (gameFinished()) {
            return;
        }
        game.surrender(player);
        GameStatePrinter.printSurrender(player.getName());
    }

    public boolean gameFinished() {
        return game.isFinished() || player.getLives() <= 0;
    }
}
