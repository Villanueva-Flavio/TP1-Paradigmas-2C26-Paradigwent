package com.fiuba.paradigwent.model.board;

import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.card.Weather;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.enums.WeatherType;
import com.fiuba.paradigwent.model.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Board shared by both players. It is composed of one {@link PlayerSide}
 * per player and the current weather, and offers the domain operations
 * that effects need instead of letting them walk through its parts.
 */
public class Board {

    private final PlayerSide playerSide;
    private final PlayerSide opponentSide;
    private WeatherType currentWeather;
    private Weather currentWeatherCard;

    /**
     * Creates an empty board with clear weather.
     *
     * @param player   owner of the lower side
     * @param opponent owner of the upper side
     */
    public Board(Player player, Player opponent) {
        this.playerSide = new PlayerSide(player);
        this.opponentSide = new PlayerSide(opponent);
        this.currentWeather = WeatherType.CLEAR;
        this.currentWeatherCard = null;
    }

    /**
     * @return the side of the first player
     */
    public PlayerSide getPlayerSide() {
        return playerSide;
    }

    /**
     * @return the side of the second player
     */
    public PlayerSide getOpponentSide() {
        return opponentSide;
    }

    /**
     * Applies a weather to both sides. Clear weather also removes the weather card.
     *
     * @param currentWeather the new weather, {@code null} means clear
     */
    public void setCurrentWeather(WeatherType currentWeather) {
        this.currentWeather = currentWeather == null ? WeatherType.CLEAR : currentWeather;
        playerSide.applyWeather(this.currentWeather);
        opponentSide.applyWeather(this.currentWeather);
        if (this.currentWeather == WeatherType.CLEAR) {
            this.currentWeatherCard = null;
        }
    }

    /**
     * Applies the weather of a weather card and remembers the card.
     *
     * @param weather the played weather card, {@code null} means clear
     */
    public void setCurrentWeather(Weather weather) {
        this.currentWeatherCard = weather;
        setCurrentWeather(weather == null ? WeatherType.CLEAR : weather.getWeatherType());
    }

    /**
     * @return the last played weather card, or {@code null}
     */
    public Weather getCurrentWeatherCard() {
        return currentWeatherCard;
    }

    /**
     * @return the weather affecting the board
     */
    public WeatherType getCurrentWeather() {
        return currentWeather;
    }

    /**
     * Removes the weather from the board.
     */
    public void clearWeather() {
        setCurrentWeather(WeatherType.CLEAR);
        this.currentWeatherCard = null;
    }

    /**
     * Sends every minion to its owner's discard pile and clears the weather.
     */
    public void discard() {
        playerSide.discard();
        opponentSide.discard();
        clearWeather();
    }

    /**
     * Places a minion on its line on the side of the given player.
     *
     * @param owner  player that owns the minion
     * @param minion minion to deploy
     */
    public void deploy(Player owner, Minion minion) {
        getSideFor(owner).deploy(minion);
    }

    /**
     * Activates the horn of a line of the given player.
     *
     * @param owner player that owns the line
     * @param line  line to duplicate
     */
    public void activateHorn(Player owner, AttackLineType line) {
        getSideFor(owner).getAttackLine(line).activateHorn();
    }

    /**
     * @param player a player of the match
     * @return the strongest minion of that player, or {@code null}
     */
    public Minion strongestMinionOf(Player player) {
        return player == null ? null : getSideFor(player).getStrongestMinion();
    }

    /**
     * @param player a player of the match
     * @param line   line to search, or {@link AttackLineType#NONE} for every line
     * @return the strongest minion of that player in the line, or {@code null}
     */
    public Minion strongestMinionOf(Player player, AttackLineType line) {
        if (line == null || line == AttackLineType.NONE) {
            return strongestMinionOf(player);
        }
        return getSideFor(player).getAttackLine(line).getStrongestMinion();
    }

    /**
     * @param player a player of the match
     * @return the power of that player, or 0 if the player is not on this board
     */
    public int calculateTotalPower(Player player) {
        if (playerSide.isOwnedBy(player)) {
            return playerSide.totalPower();
        }
        if (opponentSide.isOwnedBy(player)) {
            return opponentSide.totalPower();
        }
        return 0;
    }

    /**
     * @param player a player of the match
     * @return the side of that player
     * @throws IllegalArgumentException if the player is not on this board
     */
    public PlayerSide getSideFor(Player player) {
        if (playerSide.isOwnedBy(player)) {
            return playerSide;
        }
        if (opponentSide.isOwnedBy(player)) {
            return opponentSide;
        }
        throw new IllegalArgumentException("The player does not belong to this board");
    }

    /**
     * @return every minion on the board
     */
    public List<Minion> allMinions() {
        List<Minion> result = new ArrayList<>();
        result.addAll(playerSide.allMinions());
        result.addAll(opponentSide.allMinions());
        return List.copyOf(result);
    }

    /**
     * @param player a player of the match
     * @return the minions of that player
     */
    public List<Minion> minionsOf(Player player) {
        return List.copyOf(getSideFor(player).allMinions());
    }

    /**
     * Removes a minion from whichever side it is on.
     *
     * @param minion minion to remove
     * @return {@code true} if the minion was on the board
     */
    public boolean removeMinion(Minion minion) {
        return playerSide.removeMinion(minion) || opponentSide.removeMinion(minion);
    }

    /**
     * Removes a minion from the board and sends it to its owner's discard pile.
     *
     * @param minion minion to destroy
     * @return {@code true} if the minion was on the board
     */
    public boolean destroy(Minion minion) {
        return playerSide.destroy(minion) || opponentSide.destroy(minion);
    }

    /**
     * @param minion a minion on the board
     * @return the owner of the minion
     * @throws IllegalArgumentException if the minion is not on this board
     */
    public Player ownerOf(Minion minion) {
        if (playerSide.contains(minion)) {
            return playerSide.getOwner();
        }
        if (opponentSide.contains(minion)) {
            return opponentSide.getOwner();
        }
        throw new IllegalArgumentException("The minion does not belong to this board");
    }
}
