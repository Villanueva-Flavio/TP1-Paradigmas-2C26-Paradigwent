package com.fiuba.paradigwent.controller;

import com.fiuba.paradigwent.model.board.AttackLine;
import com.fiuba.paradigwent.model.board.Board;
import com.fiuba.paradigwent.model.board.PlayerSide;
import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.card.Weather;
import com.fiuba.paradigwent.model.enums.AttackLineType;

/**
 * Coordinates board state queries and board-related diagnostics.
 */
public final class BoardController {
    private final Board board;

    public BoardController(Board board) {
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }

    public void printSituation(String playerName, String cardName) {
        System.out.printf("[GAME] %s colocó la carta \"%s\"%n", playerName, cardName);
        Weather weatherCard = board.getCurrentWeatherCard();
        String weatherName = weatherCard == null ? "(sin carta de clima)" : weatherCard.getName();
        System.out.printf("[GAME] Clima actual: %s (%s)%n",
            board.getCurrentWeather(), weatherName);
        printSide("Jugador", board.getPlayerSide());
        printSide("Oponente", board.getOpponentSide());
        System.out.printf("[GAME] Cartas restantes en mano de %s: %d%n",
            board.getPlayerSide().getOwner().getName(),
            board.getPlayerSide().getOwner().getHand().size());
        System.out.printf("[GAME] Descartes: %d / %d%n",
            board.getPlayerSide().getOwner().getDiscard().size(),
            board.getOpponentSide().getOwner().getDiscard().size());
        System.out.printf("[GAME] Mazos: %d / %d%n",
            board.getPlayerSide().getOwner().getDeckSize(),
            board.getOpponentSide().getOwner().getDeckSize());
    }

    private void printSide(String label, PlayerSide side) {
        System.out.printf("[GAME] %s (%s) - poder total: %d%n",
            label, side.getOwner().getName(), side.totalPower());
        printLine(side, AttackLineType.SIEGE);
        printLine(side, AttackLineType.RANGED);
        printLine(side, AttackLineType.MELEE);
    }

    private void printLine(PlayerSide side, AttackLineType type) {
        AttackLine line = side.getAttackLine(type);
        String cards = line.getMinions().stream()
            .map(Minion::getName)
            .reduce((left, right) -> left + ", " + right)
            .orElse("(vacía)");
        System.out.printf("[GAME]   %s: %s | poder=%d%n",
            type, cards, line.calculateTotalPower());
    }
}
