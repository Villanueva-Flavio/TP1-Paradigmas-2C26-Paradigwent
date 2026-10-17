package com.fiuba.paradigwent.controller;

import com.fiuba.paradigwent.model.board.Board;

/**
 * Prints lifecycle messages for actions that affect the match.
 */
public final class GameStatePrinter {
    /**      * Constructs a new GameStatePrinter.      */
    private GameStatePrinter() {
    }
    /**
     * Displays the information handled by printRejected.
     * @param reason the value used by the method
     */
    public static void printRejected(String reason) {
        System.out.printf("[GAME] Jugada rechazada: %s%n", reason);
    }
    /**
     * Displays the information handled by printTurn.
     * @param playerName the value used by the method
     */
    public static void printTurn(String playerName) {
        System.out.printf("[GAME] Turno actual: %s%n", playerName);
    }
    /**
     * Displays the information handled by printInitialSituation.
     * @param board the value used by the method
     */
    public static void printInitialSituation(Board board) {
        System.out.println("[GAME] Partida iniciada");
        System.out.printf("[GAME] Jugador: %s | cartas en mano: %d%n",
            board.getPlayerSide().getOwner().getName(),
            board.getPlayerSide().getOwner().getHand().size());
        System.out.printf("[GAME] Oponente: %s%n",
            board.getOpponentSide().getOwner().getName());
        System.out.printf("[GAME] Clima actual: %s%n", board.getCurrentWeather());
    }
    /**
     * Displays the information handled by printSurrender.
     * @param playerName the value used by the method
     */
    public static void printSurrender(String playerName) {
        System.out.printf("[GAME] %s se rindió. Resultado: DERROTA%n", playerName);
    }
}
