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
    private Card lastOpponentCard;

    public GameController(CardRace selectedRace) {
        List<Card> cards = JSONCardLoader.loadCards().stream()
            .filter(card -> selectedRace.name().equals(card.getFaction()))
            .toList();
        player = new Player("Player", new Deck(new ArrayList<>(cards)));
        player.setFaction(selectedRace.name());
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
        opponent.setFaction(opponentRace.name());
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
        return playCard(card, null);
    }

    public boolean playCard(Card card, Card targetCard) {
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

        if (card.requiresTarget() && targetCard == null) {
            GameStatePrinter.printRejected("el hechizo requiere una carta objetivo");
            return false;
        }
        if (card.requiresTarget() && !getTargetCards().contains(targetCard)) {
            GameStatePrinter.printRejected("la carta objetivo no está en el tablero");
            return false;
        }
        round.playCard(card, targetCard);
        boardController.printSituation(player.getName(), card.getName());
        GameStatePrinter.printTurn(round.getCurrentPlayer().getName());
        return true;
    }

    public boolean requiresTarget(Card card) {
        return card != null && card.requiresTarget();
    }

    public List<Card> getTargetCards() {
        List<Card> targets = new ArrayList<>();
        targets.addAll(getBoard().getPlayerSide().getAttackLine(
            com.fiuba.paradigwent.model.enums.AttackLineType.MELEE).getMinions());
        targets.addAll(getBoard().getPlayerSide().getAttackLine(
            com.fiuba.paradigwent.model.enums.AttackLineType.RANGED).getMinions());
        targets.addAll(getBoard().getPlayerSide().getAttackLine(
            com.fiuba.paradigwent.model.enums.AttackLineType.SIEGE).getMinions());
        targets.addAll(getBoard().getOpponentSide().getAttackLine(
            com.fiuba.paradigwent.model.enums.AttackLineType.MELEE).getMinions());
        targets.addAll(getBoard().getOpponentSide().getAttackLine(
            com.fiuba.paradigwent.model.enums.AttackLineType.RANGED).getMinions());
        targets.addAll(getBoard().getOpponentSide().getAttackLine(
            com.fiuba.paradigwent.model.enums.AttackLineType.SIEGE).getMinions());
        return targets;
    }

    public boolean playOpponentCard() {
        lastOpponentCard = null;
        if (round.isFinished() || round.getCurrentPlayer() != opponent) {
            return false;
        }
        if (opponent.getHand().isEmpty()) {
            round.passTurn();
            return finishRoundIfNeeded();
        }
        Card card = opponent.selectCard(null);
        if (card == null) {
            round.passTurn();
            return finishRoundIfNeeded();
        }
        Card target = card.requiresTarget()
            ? getTargetCards().stream().findFirst().orElse(null) : null;
        if (card.requiresTarget() && target == null) {
            round.passTurn();
            return finishRoundIfNeeded();
        }
        round.playCard(card, target);
        lastOpponentCard = card;
        boardController.printSituation(opponent.getName(), card.getName());
        GameStatePrinter.printTurn(round.getCurrentPlayer().getName());
        return true;
    }

    public Card getLastOpponentCard() {
        return lastOpponentCard;
    }

    public boolean passTurn() {
        if (round.isFinished() || game.isFinished()) {
            GameStatePrinter.printRejected("la partida ya terminó");
            return false;
        }

        round.passPlayer(player);
        round.passPlayer(opponent);

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
                System.out.printf("[GAME] Nueva ronda: %s robó hasta 10 cartas (mano=%d, mazo=%d); "
                        + "%s robó hasta 10 cartas (mano=%d, mazo=%d)%n",
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

    public Result getMatchResult() {
        if (player.getLives() > opponent.getLives()) {
            return Result.PLAYER1_WIN;
        }
        if (player.getLives() < opponent.getLives()) {
            return Result.PLAYER2_WIN;
        }
        return Result.DRAW;
    }
}
