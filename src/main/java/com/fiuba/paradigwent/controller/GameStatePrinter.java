package com.fiuba.paradigwent.controller;

import com.fiuba.paradigwent.model.board.Board;

/**
 * Prints lifecycle messages for actions that affect the match.
 */
public final class GameStatePrinter {
    private GameStatePrinter() {
    }

    public static void printRejected(String reason) {
        System.out.printf("[GAME] Jugada rechazada: %s%n", reason);
    }

    public static void printTurn(String playerName) {
        System.out.printf("[GAME] Turno actual: %s%n", playerName);
    }

    public static void printInitialSituation(Board board) {
        System.out.println("[GAME] Partida iniciada");
        System.out.printf("[GAME] Jugador: %s | cartas en mano: %d%n",
            board.getPlayerSide().getOwner().getName(),
            board.getPlayerSide().getOwner().getHand().size());
        System.out.printf("[GAME] Oponente: %s%n",
            board.getOpponentSide().getOwner().getName());
        System.out.printf("[GAME] Clima actual: %s%n", board.getCurrentWeather());
    }

    public static void printSurrender(String playerName) {
        System.out.printf("[GAME] %s se rindió. Resultado: DERROTA%n", playerName);
    }
}
