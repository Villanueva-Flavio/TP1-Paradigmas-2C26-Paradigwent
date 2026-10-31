package com.fiuba.paradigwent.view;

import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fiuba.paradigwent.model.enums.WeatherType;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Owns application music and sound effects so view changes cannot leave
 * multiple background tracks playing at the same time.
 */
public final class AudioManager {
    private static final String MENU_MUSIC = "/audio/music/menu.mp3";
    private static final String GAME_MUSIC = "/audio/music/game.mp3";
    private static final String CARD_PLAY = "/audio/effects/card-play.mp3";
    private static final String CARD_DRAW = "/audio/effects/card-draw.mp3";
    private static final String DESTROY = "/audio/effects/destroy.mp3";
    private static final String REVIVE = "/audio/effects/heal.mp3";
    private static final String BUFF = "/audio/effects/buff.mp3";
    private static final String DUPLICATE_LINE = "/audio/effects/duplicate-line.mp3";
    private static final String PASS = "/audio/effects/pass.mp3";
    private static final String ROUND_END = "/audio/effects/round-end.mp3";
    private static final String VICTORY = "/audio/effects/victory.mp3";
    private static final String DEFEAT = "/audio/effects/defeat.mp3";

    private static final Map<WeatherType, String> WEATHER_MUSIC = Map.of(
        WeatherType.CLEAR, "/audio/music/clear.mp3",
        WeatherType.FROST, "/audio/music/snow.mp3",
        WeatherType.FOG, "/audio/music/fog.mp3",
        WeatherType.RAIN, "/audio/music/rain.mp3",
        WeatherType.THUNDER, "/audio/music/thunder.mp3",
        WeatherType.SNOW, "/audio/music/snow.mp3");

    private MediaPlayer backgroundPlayer;
    private MediaPlayer weatherPlayer;
    private WeatherType currentWeatherMusic;
    private final Set<MediaPlayer> activeEffects = new HashSet<>();

    /**
     * Creates an audio manager with no music playing.
     */
    public AudioManager() {
        this.currentWeatherMusic = null;
    }
    /**
     * Executes the operation represented by playMenuMusic.
     */
    public void playMenuMusic() {
        stopGameMusic();
        backgroundPlayer = loop(MENU_MUSIC, backgroundPlayer, 0.6);
    }
    /**
     * Executes the operation represented by playGameMusic.
     */
    public void playGameMusic() {
        stopMenuMusic();
        backgroundPlayer = loop(GAME_MUSIC, backgroundPlayer, 0.6);
    }
    /**
     * Executes the operation represented by stopMenuMusic.
     */
    public void stopMenuMusic() {
        stop(backgroundPlayer);
        backgroundPlayer = null;
    }
    /**
     * Executes the operation represented by stopGameMusic.
     */
    public void stopGameMusic() {
        stop(backgroundPlayer);
        backgroundPlayer = null;
        stopWeatherMusic();
    }
    /**
     * Executes the operation represented by updateWeatherMusic.
     * @param weather the value used by the method
     */
    public void updateWeatherMusic(WeatherType weather) {
        if (weather == null) {
            weather = WeatherType.CLEAR;
        }
        if (weather == currentWeatherMusic && weatherPlayer != null) {
            return;
        }
        stopWeatherMusic();
        String path = WEATHER_MUSIC.get(weather);
        if (path != null) {
            weatherPlayer = loop(path, null, 0.48);
            if (weatherPlayer != null) {
                currentWeatherMusic = weather;
            }
        }
    }
    /**
     * Executes the operation represented by stopWeatherMusic.
     */
    public void stopWeatherMusic() {
        stop(weatherPlayer);
        weatherPlayer = null;
        currentWeatherMusic = null;
    }
    /**
     * Executes the operation represented by playCardPlay.
     */
    public void playCardPlay() {
        playOneShot(CARD_PLAY);
    }
    /**
     * Executes the operation represented by playCardDraw.
     */
    public void playCardDraw() {
        playOneShot(CARD_DRAW);
    }
    /**
     * Executes the operation represented by playDestroy.
     */
    public void playDestroy() {
        playOneShot(DESTROY);
    }
    /**
     * Executes the operation represented by playRevive.
     */
    public void playRevive() {
        playOneShot(REVIVE);
    }
    /**
     * Executes the operation represented by playBuff.
     */
    public void playBuff() {
        playOneShot(BUFF);
    }
    /**
     * Executes the operation represented by playDuplicateLine.
     */
    public void playDuplicateLine() {
        playOneShot(DUPLICATE_LINE);
    }
    /**
     * Plays the sound of a player passing the round.
     */
    public void playPass() {
        playOneShot(PASS);
    }
    /**
     * Plays the sound of the end of a round.
     */
    public void playRoundEnd() {
        playOneShot(ROUND_END);
    }
    /**
     * Plays the victory jingle.
     */
    public void playVictory() {
        playOneShot(VICTORY);
    }
    /**
     * Plays the defeat jingle.
     */
    public void playDefeat() {
        playOneShot(DEFEAT);
    }
    /**
     * Executes the operation represented by loop.
     * @param path the value used by the method
     * @param previous the value used by the method
     * @param volume the value used by the method
     * @return the value produced by the method
     */
    private MediaPlayer loop(String path, MediaPlayer previous, double volume) {
        stop(previous);
        URL resource = AudioManager.class.getResource(path);
        if (resource == null) {
            reportMissing(path);
            return null;
        }
        try {
            MediaPlayer player = new MediaPlayer(new Media(resource.toExternalForm()));
            player.setCycleCount(MediaPlayer.INDEFINITE);
            player.setVolume(volume);
            player.setOnError(() -> System.err.println(
                "[AUDIO] Could not play resource: " + path + " - " + player.getError()));
            player.play();
            return player;
        } catch (RuntimeException e) {
            System.err.println("[AUDIO] Could not create player for: " + path + " - " + e.getMessage());
            return null;
        }
    }
    /**
     * Executes the operation represented by playOneShot.
     * @param path the value used by the method
     */
    private void playOneShot(String path) {
        URL resource = AudioManager.class.getResource(path);
        if (resource == null) {
            reportMissing(path);
            return;
        }
        try {
            MediaPlayer player = new MediaPlayer(new Media(resource.toExternalForm()));
            activeEffects.add(player);
            player.setOnEndOfMedia(() -> finishEffect(player));
            player.setOnError(() -> {
                System.err.println("[AUDIO] Could not play resource: " + path
                    + " - " + player.getError());
                finishEffect(player);
            });
            player.play();
        } catch (RuntimeException e) {
            System.err.println("[AUDIO] Could not create player for: " + path + " - " + e.getMessage());
        }
    }
    /**
     * Executes the operation represented by finishEffect.
     * @param player the value used by the method
     */
    private void finishEffect(MediaPlayer player) {
        activeEffects.remove(player);
        player.dispose();
    }
    /**
     * Executes the operation represented by stop.
     * @param player the value used by the method
     */
    private void stop(MediaPlayer player) {
        if (player != null) {
            player.stop();
            player.dispose();
        }
    }
    /**
     * Executes the operation represented by reportMissing.
     * @param path the value used by the method
    */
    private void reportMissing(String path) {
        System.err.println("[AUDIO] Missing resource: " + path);
    }
}
