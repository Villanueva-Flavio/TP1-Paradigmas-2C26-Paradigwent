package com.fiuba.paradigwent.model;

import java.util.List;
import java.util.Optional;

import com.fiuba.paradigwent.model.board.Board;
import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.card.Weather;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.enums.WeatherType;
import com.fiuba.paradigwent.model.event.EffectListener;
import com.fiuba.paradigwent.model.player.Player;
import com.fiuba.paradigwent.model.targeting.TargetSide;

/**
 * Parameter object for a single play. Instead of handing out the board and
 * the players, it offers the domain operations that cards, effects and the AI
 * need, always from the point of view of the player whose turn it is.
 */
public class GameContext {
    private final Player currentPlayer;
    private final Player opponent;
    private final Board board;
    private final EffectListener listener;
    private final Card playedCard;
    private final Minion target;
    private final AttackLineType targetLine;

    /**
     * Creates the context of a play without target nor observers.
     *
     * @param currentPlayer player whose turn it is
     * @param opponent      the other player
     * @param board         board of the match
     * @param playedCard    card being played
     */
    public GameContext(Player currentPlayer, Player opponent, Board board, Card playedCard) {
        this(currentPlayer, opponent, board, playedCard, null, null);
    }

    /**
     * Creates the context of a play without observers.
     *
     * @param currentPlayer player whose turn it is
     * @param opponent      the other player
     * @param board         board of the match
     * @param playedCard    card being played
     * @param target        chosen minion, may be {@code null}
     * @param targetLine    chosen line, may be {@code null}
     */
    public GameContext(Player currentPlayer, Player opponent, Board board,
                       Card playedCard, Minion target, AttackLineType targetLine) {
        this(currentPlayer, opponent, board, new EffectListener() { }, playedCard, target, targetLine);
    }

    /**
     * Creates the context of a play.
     *
     * @param currentPlayer player whose turn it is
     * @param opponent      the other player
     * @param board         board of the match
     * @param listener      observer of the changes produced by effects
     * @param playedCard    card being played, {@code null} when only deciding a move
     * @param target        chosen minion, may be {@code null}
     * @param targetLine    chosen line, may be {@code null}
     */
    public GameContext(Player currentPlayer, Player opponent, Board board, EffectListener listener,
                       Card playedCard, Minion target, AttackLineType targetLine) {
        this.currentPlayer = currentPlayer;
        this.opponent = opponent;
        this.board = board;
        this.listener = listener;
        this.playedCard = playedCard;
        this.target = target;
        this.targetLine = targetLine;
    }

    /**
     * @return the minion chosen as target of the play, if any
     */
    public Optional<Minion> chosenTarget() {
        return Optional.ofNullable(target);
    }

    /**
     * @return the attack line chosen for the play, if any
     */
    public Optional<AttackLineType> chosenLine() {
        return Optional.ofNullable(targetLine).filter(line -> line != AttackLineType.NONE);
    }

    /**
     * @return the attack line of the played card
     */
    public AttackLineType playedCardLine() {
        return playedCard == null ? AttackLineType.NONE : playedCard.getAttackLineType();
    }

    /**
     * Places a minion on its line, on the current player's side.
     *
     * @param minion minion to deploy
     */
    public void deploy(Minion minion) {
        board.deploy(currentPlayer, minion);
    }

    /**
     * Sends a card to the current player's discard pile.
     *
     * @param card card to discard
     */
    public void discard(Card card) {
        currentPlayer.sendToDiscard(List.of(card));
    }

    /**
     * Applies the weather of a weather card to the whole board.
     *
     * @param weather the played weather card
     */
    public void changeWeather(Weather weather) {
        board.setCurrentWeather(weather);
        listener.weatherChanged(weather.getWeatherType());
    }

    /**
     * Applies a weather to the whole board.
     *
     * @param weather new weather
     */
    public void changeWeather(WeatherType weather) {
        board.setCurrentWeather(weather);
        listener.weatherChanged(board.getCurrentWeather());
    }

    /**
     * Finds the strongest minion of the opponent.
     *
     * @param line line to search, or {@link AttackLineType#NONE} to search every line
     * @return the strongest enemy minion, if there is one
     */
    public Optional<Minion> strongestEnemyMinion(AttackLineType line) {
        return Optional.ofNullable(board.strongestMinionOf(opponent, line));
    }

    /**
     * Removes a minion from the board and sends it to its owner's discard pile.
     *
     * @param minion minion to destroy
     */
    public void destroy(Minion minion) {
        if (board.destroy(minion)) {
            listener.minionDestroyed(minion);
        }
    }

    /**
     * Changes the power of a minion.
     *
     * @param minion minion to modify
     * @param amount modifier to add
     */
    public void modifyPower(Minion minion, int amount) {
        minion.modifyPower(amount);
        listener.powerModified(minion, amount);
    }

    /**
     * Duplicates the power of one of the current player's lines.
     *
     * @param line line to duplicate
     */
    public void duplicateLine(AttackLineType line) {
        board.activateHorn(currentPlayer, line);
        listener.lineDuplicated(line);
    }

    /**
     * Brings back the most recently discarded minions of the current player.
     *
     * @param amount maximum number of minions to revive
     */
    public void reviveMinions(int amount) {
        currentPlayer.reviveLatestMinions(amount, board.getSideFor(currentPlayer), listener);
    }

    /**
     * Moves cards from the opponent's hand to the current player's hand.
     *
     * @param amount maximum number of cards to steal
     */
    public void stealCards(int amount) {
        int stolen = currentPlayer.stealCardsFrom(opponent, amount);
        listener.cardsStolen(stolen);
    }

    /**
     * @return the power of the current player in this round
     */
    public int ownPower() {
        return board.calculateTotalPower(currentPlayer);
    }

    /**
     * @return the power of the opponent in this round
     */
    public int enemyPower() {
        return board.calculateTotalPower(opponent);
    }

    /**
     * @return {@code true} if the opponent already passed this round
     */
    public boolean enemyHasPassed() {
        return opponent.hasPassedRound();
    }

    /**
     * @return every minion currently on the board
     */
    public List<Minion> minionsOnBoard() {
        return board.allMinions();
    }

    /**
     * @param side side of the board relative to the current player
     * @return the minions on that side
     */
    public List<Minion> minionsOn(TargetSide side) {
        return board.minionsOf(side == TargetSide.ALLY ? currentPlayer : opponent);
    }
}
