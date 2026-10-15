package com.fiuba.paradigwent.model.board;

import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.card.Weather;
import com.fiuba.paradigwent.model.enums.WeatherType;
import com.fiuba.paradigwent.model.player.Player;
import java.util.ArrayList;
import java.util.List;

public class Board {

    private PlayerSide playerSide;
    private PlayerSide opponentSide;
    private WeatherType currentWeather;
    private Weather currentWeatherCard;

    /**
     * Constructor for the Board class, initializing the player and opponent sides with their respective players.
     *
     * @param player
     * @param opponent
     **/
    public Board(Player player, Player opponent) {
        this.playerSide = new PlayerSide(player);
        this.opponentSide = new PlayerSide(opponent);
        this.currentWeather = WeatherType.CLEAR;
        this.currentWeatherCard = null;
    }

    /**
     * Returns the PlayerSide object representing the player's side of the board.
     *
     * @return The PlayerSide object for the player's side.
     **/
    public PlayerSide getPlayerSide() {
        return playerSide;
    }

    /**
     * Returns the PlayerSide object representing the opponent's side of the board.
     *
     * @return The PlayerSide object for the opponent's side.
     **/
    public PlayerSide getOpponentSide() {
        return opponentSide;
    }

    /**
     * Sets the current weather on the board. If the provided weather type is null, it defaults to CLEAR.
     *
     * @param currentWeather The WeatherType to set as the current weather on the board
     **/
    public void setCurrentWeather(WeatherType currentWeather) {
        this.currentWeather = currentWeather == null ? WeatherType.CLEAR : currentWeather;
        playerSide.applyWeather(this.currentWeather);
        opponentSide.applyWeather(this.currentWeather);
        if (this.currentWeather == WeatherType.CLEAR) {
            this.currentWeatherCard = null;
        }
    }

    public void setCurrentWeather(Weather weather) {
        this.currentWeatherCard = weather;
        setCurrentWeather(weather == null ? WeatherType.CLEAR : weather.getWeatherType());
    }

    public Weather getCurrentWeatherCard() {
        return currentWeatherCard;
    }

    /**
     * Returns the current weather on the board.
     *
     * @return The current WeatherType on the board.
     **/
    public WeatherType getCurrentWeather() {
        return currentWeather;
    }

    /**
     * Clears the current weather on the board, setting it to CLEAR.
     **/
    public void clearWeather() {
        setCurrentWeather(WeatherType.CLEAR);
        this.currentWeatherCard = null;
    }

    /**
     * Discards the board, which involves discarding both player sides and resetting the current weather to CLEAR.
     **/
    public void discard() {
        playerSide.discard();
        opponentSide.discard();
        clearWeather();

    }

    /**
     * Returns the strongest minion on the opponent's side of the board based on its current power.
     *
     * @param opponent The PlayerSide representing the opponent's side of the board.
     * @return The strongest minion on the opponent's side, or null if there are no minions or if the opponent is null.
     **/
    public Minion getStrongestMiniondOf(PlayerSide opponent) {
        return opponent == null ? null : opponent.getStrongestMinion();
    }

    public Minion strongestMinionOf(Player player) {
        return player == null ? null : getSideFor(player).getStrongestMinion();
    }

    public int calculateTotalPower(Player player) {
        if (playerSide.getOwner().equals(player)) {
            return playerSide.totalPower();
        }
        if (opponentSide.getOwner().equals(player)) {
            return opponentSide.totalPower();
        }
        return 0;
    }

    public PlayerSide getSideFor(Player player) {
        if (player == playerSide.getOwner()) {
            return playerSide;
        }
        if (player == opponentSide.getOwner()) {
            return opponentSide;
        }
        throw new IllegalArgumentException("El jugador no pertenece a este tablero");

    }

    public List<Minion> allMinions() {
        List<Minion> result = new ArrayList<>();
        result.addAll(playerSide.allMinions());
        result.addAll(opponentSide.allMinions());
        return List.copyOf(result);
    }

    public List<Minion> minionsOf(Player player) {
        return List.copyOf(getSideFor(player).allMinions());
    }

    public boolean removeMinion(Minion minion) {
        return playerSide.removeMinion(minion) || opponentSide.removeMinion(minion);
    }

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
