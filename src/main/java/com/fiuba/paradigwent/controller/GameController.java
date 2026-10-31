package com.fiuba.paradigwent.controller;

import com.fiuba.paradigwent.model.ai.AI;
import com.fiuba.paradigwent.model.board.AttackLine;
import com.fiuba.paradigwent.model.board.Board;
import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.card.Weather;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.enums.Faction;
import com.fiuba.paradigwent.model.enums.Result;
import com.fiuba.paradigwent.model.enums.WeatherType;
import com.fiuba.paradigwent.model.event.GameListener;
import com.fiuba.paradigwent.model.game.Game;
import com.fiuba.paradigwent.model.game.TurnScheduler;
import com.fiuba.paradigwent.model.player.Player;

import java.util.List;

/**
 * Translates the actions of the human into model operations and offers the
 * view the queries it needs. It does not contain game rules: turns, AI moves
 * and round resolution live in {@link Game}.
 */
public final class GameController {

    private final Game game;
    private final Board board;
    private final Player player;
    private final AI opponent;

    /**
     * Creates a new match for the chosen faction.
     *
     * @param selectedFaction faction chosen by the human
     * @param scheduler       decides when each turn is taken
     */
    public GameController(Faction selectedFaction, TurnScheduler scheduler) {
        GameSessionFactory.Session session = new GameSessionFactory().create(selectedFaction, scheduler);
        game = session.game();
        board = session.board();
        player = session.player();
        opponent = session.opponent();
        game.addListener(new ConsoleGameLogger(player, opponent, board));
    }

    /**
     * Registers an observer of the match.
     *
     * @param listener observer to notify
     */
    public void addListener(GameListener listener) {
        game.addListener(listener);
    }

    /**
     * Starts the match. Call it once every observer is registered.
     */
    public void startMatch() {
        game.startMatch();
    }

    /**
     * Plays a card of the human's hand.
     *
     * @param card   card to play
     * @param target chosen minion, may be {@code null}
     * @param line   chosen line, may be {@code null}
     * @return {@code true} if the play was accepted
     */
    public boolean playCard(Card card, Minion target, AttackLineType line) {
        return game.play(player, card, target, line);
    }

    /**
     * Passes the round on behalf of the human.
     *
     * @return {@code true} if the pass was accepted
     */
    public boolean passTurn() {
        return game.pass(player);
    }

    /**
     * Surrenders the match on behalf of the human.
     */
    public void surrender() {
        game.surrender(player);
    }

    /**
     * @return {@code true} if the human can act now
     */
    public boolean isPlayerTurn() {
        return game.isTurnOf(player);
    }

    /**
     * @param somePlayer a player of the match
     * @return {@code true} if it is the AI
     */
    public boolean isOpponent(Player somePlayer) {
        return somePlayer == opponent;
    }

    /**
     * @return the name of the player whose turn it is
     */
    public String getCurrentPlayerName() {
        Player current = game.getCurrentPlayer();
        return current == null ? "" : current.getName();
    }

    /**
     * @return the cards in the human's hand
     */
    public List<Card> getPlayerHand() {
        return player.getHand();
    }

    /**
     * @return number of cards in the AI's hand
     */
    public int getOpponentHandSize() {
        return opponent.handSize();
    }

    /**
     * @return minions that can be chosen as target
     */
    public List<Minion> getTargetCandidates() {
        return board.allMinions();
    }

    /**
     * @return faction of the human
     */
    public Faction getPlayerFaction() {
        return player.getFaction();
    }

    /**
     * @return faction of the AI
     */
    public Faction getOpponentFaction() {
        return opponent.getFaction();
    }

    /**
     * @return power of the human in the current round
     */
    public int getPlayerPower() {
        return board.calculateTotalPower(player);
    }

    /**
     * @return power of the AI in the current round
     */
    public int getOpponentPower() {
        return board.calculateTotalPower(opponent);
    }

    /**
     * @return remaining lives of the human
     */
    public int getPlayerLives() {
        return player.getLives();
    }

    /**
     * @return remaining lives of the AI
     */
    public int getOpponentLives() {
        return opponent.getLives();
    }

    /**
     * @return cards left in the human's deck
     */
    public int getPlayerDeckSize() {
        return player.getDeckSize();
    }

    /**
     * @return cards left in the AI's deck
     */
    public int getOpponentDeckSize() {
        return opponent.getDeckSize();
    }

    /**
     * @return the last card discarded by the human, or {@code null}
     */
    public Card getPlayerLastDiscarded() {
        return player.lastDiscarded();
    }

    /**
     * @return the last card discarded by the AI, or {@code null}
     */
    public Card getOpponentLastDiscarded() {
        return opponent.lastDiscarded();
    }

    /**
     * @param type an attack line type
     * @return that line of the human's side
     */
    public AttackLine getPlayerLine(AttackLineType type) {
        return board.getSideFor(player).getAttackLine(type);
    }

    /**
     * @param type an attack line type
     * @return that line of the AI's side
     */
    public AttackLine getOpponentLine(AttackLineType type) {
        return board.getSideFor(opponent).getAttackLine(type);
    }

    /**
     * @return the weather of the board
     */
    public WeatherType getCurrentWeather() {
        return board.getCurrentWeather();
    }

    /**
     * @return the weather card on the board, or {@code null}
     */
    public Weather getCurrentWeatherCard() {
        return board.getCurrentWeatherCard();
    }

    /**
     * @return {@code true} once the match is over
     */
    public boolean gameFinished() {
        return game.isFinished();
    }

    /**
     * @return the result of the match from the human's point of view (player 1)
     */
    public Result getMatchResult() {
        return game.getMatchResult();
    }
}
