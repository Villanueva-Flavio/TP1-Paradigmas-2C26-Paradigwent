package com.fiuba.paradigwent.controller;

import com.fiuba.paradigwent.model.board.Board;
import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.enums.Result;
import com.fiuba.paradigwent.model.enums.WeatherType;
import com.fiuba.paradigwent.model.event.GameListener;
import com.fiuba.paradigwent.model.player.Player;

/**
 * Prints the events of a match to the console, useful to follow a match
 * while debugging.
 */
public final class ConsoleGameLogger implements GameListener {
    private final Player player;
    private final Player opponent;
    private final Board board;

    /**
     * @param player   the human player
     * @param opponent the AI player
     * @param board    the board of the match
     */
    public ConsoleGameLogger(Player player, Player opponent, Board board) {
        this.player = player;
        this.opponent = opponent;
        this.board = board;
    }

    @Override
    public void roundStarted(Player startingPlayer) {
        log("New round. %s starts. Lives - %s: %d | %s: %d",
            startingPlayer.getName(), player.getName(), player.getLives(),
            opponent.getName(), opponent.getLives());
    }

    @Override
    public void cardPlayed(Player who, Card card) {
        log("%s plays \"%s\"", who.getName(), card.getName());
    }

    @Override
    public void turnPassed(Player who) {
        log("%s passes. Power - %s: %d | %s: %d", who.getName(),
            player.getName(), board.calculateTotalPower(player),
            opponent.getName(), board.calculateTotalPower(opponent));
    }

    @Override
    public void roundFinished(Result result) {
        log("Round finished: %s", result);
    }

    @Override
    public void matchFinished(Result result) {
        log("Match finished: %s", result);
    }

    @Override
    public void minionDestroyed(Minion minion) {
        log("%s was destroyed", minion.getName());
    }

    @Override
    public void minionRevived(Minion minion) {
        log("%s was revived", minion.getName());
    }

    @Override
    public void powerModified(Minion minion, int amount) {
        log("%s power changed by %d", minion.getName(), amount);
    }

    @Override
    public void lineDuplicated(AttackLineType line) {
        log("%s line duplicated", line);
    }

    @Override
    public void weatherChanged(WeatherType weather) {
        log("Weather is now %s", weather);
    }

    @Override
    public void cardsStolen(int amount) {
        log("%d card(s) stolen", amount);
    }

    private void log(String format, Object... arguments) {
        System.out.printf("[GAME] " + format + "%n", arguments);
    }
}
