package com.fiuba.paradigwent.view;

import java.net.URL;
import java.util.EnumMap;
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
    private static final String REVIVE = "/audio/effects/revive.mp3";
    private static final String BUFF = "/audio/effects/buff.mp3";
    private static final String DUPLICATE_LINE = "/audio/effects/duplicate-line.mp3";

    private static final Map<WeatherType, String> WEATHER_MUSIC = new EnumMap<>(WeatherType.class);

    static {
        WEATHER_MUSIC.put(WeatherType.CLEAR, "/audio/music/clear.mp3");
        WEATHER_MUSIC.put(WeatherType.FROST, "/audio/music/snow.mp3");
        WEATHER_MUSIC.put(WeatherType.FOG, "/audio/music/fog.mp3");
        WEATHER_MUSIC.put(WeatherType.RAIN, "/audio/music/rain.mp3");
        WEATHER_MUSIC.put(WeatherType.THUNDER, "/audio/music/thunder.mp3");
        WEATHER_MUSIC.put(WeatherType.SNOW, "/audio/music/snow.mp3");
    }

    private static MediaPlayer backgroundPlayer;
    private static MediaPlayer weatherPlayer;
    private static WeatherType currentWeatherMusic;
    private static final Set<MediaPlayer> activeEffects = new HashSet<>();

    /**      * Constructs a new AudioManager.      */
    private AudioManager() {
    }
    /**
     * Executes the operation represented by playMenuMusic.
     */
    public static void playMenuMusic() {
        stopGameMusic();
        backgroundPlayer = loop(MENU_MUSIC, backgroundPlayer, 0.6);
    }
    /**
     * Executes the operation represented by playGameMusic.
     */
    public static void playGameMusic() {
        stopMenuMusic();
        backgroundPlayer = loop(GAME_MUSIC, backgroundPlayer, 0.6);
    }
    /**
     * Executes the operation represented by stopMenuMusic.
     */
    public static void stopMenuMusic() {
        stop(backgroundPlayer);
        backgroundPlayer = null;
    }
    /**
     * Executes the operation represented by stopGameMusic.
     */
    public static void stopGameMusic() {
        stop(backgroundPlayer);
        backgroundPlayer = null;
        stopWeatherMusic();
    }
    /**
     * Executes the operation represented by updateWeatherMusic.
     * @param weather the value used by the method
     */
    public static void updateWeatherMusic(WeatherType weather) {
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
    public static void stopWeatherMusic() {
        stop(weatherPlayer);
        weatherPlayer = null;
        currentWeatherMusic = null;
    }
    /**
     * Executes the operation represented by playCardPlay.
     */
    public static void playCardPlay() {
        playOneShot(CARD_PLAY);
    }
    /**
     * Executes the operation represented by playCardDraw.
     */
    public static void playCardDraw() {
        playOneShot(CARD_DRAW);
    }
    /**
     * Executes the operation represented by playDestroy.
     */
    public static void playDestroy() {
        playOneShot(DESTROY);
    }
    /**
     * Executes the operation represented by playRevive.
     */
    public static void playRevive() {
        playOneShot(REVIVE);
    }
    /**
     * Executes the operation represented by playBuff.
     */
    public static void playBuff() {
        playOneShot(BUFF);
    }
    /**
     * Executes the operation represented by playDuplicateLine.
     */
    public static void playDuplicateLine() {
        playOneShot(DUPLICATE_LINE);
    }
    /**
     * Executes the operation represented by loop.
     * @param path the value used by the method
     * @param previous the value used by the method
     * @param volume the value used by the method
     * @return the value produced by the method
     */
    private static MediaPlayer loop(String path, MediaPlayer previous, double volume) {
        stop(previous);
        URL resource = AudioManager.class.getResource(path);
        if (resource == null) {
            reportMissing(path);
            return null;
        }
        MediaPlayer player = new MediaPlayer(new Media(resource.toExternalForm()));
        player.setCycleCount(MediaPlayer.INDEFINITE);
        player.setVolume(volume);
        player.setOnError(() -> System.err.println(
            "[AUDIO] Could not play resource: " + path + " - " + player.getError()));
        player.play();
        return player;
    }
    /**
     * Executes the operation represented by playOneShot.
     * @param path the value used by the method
     */
    private static void playOneShot(String path) {
        URL resource = AudioManager.class.getResource(path);
        if (resource == null) {
            reportMissing(path);
            return;
        }
        MediaPlayer player = new MediaPlayer(new Media(resource.toExternalForm()));
        activeEffects.add(player);
        player.setOnEndOfMedia(() -> finishEffect(player));
        player.setOnError(() -> {
            System.err.println("[AUDIO] Could not play resource: " + path
                + " - " + player.getError());
            finishEffect(player);
        });
        player.play();
    }
    /**
     * Executes the operation represented by finishEffect.
     * @param player the value used by the method
     */
    private static void finishEffect(MediaPlayer player) {
        activeEffects.remove(player);
        player.dispose();
    }
    /**
     * Executes the operation represented by stop.
     * @param player the value used by the method
     */
    private static void stop(MediaPlayer player) {
        if (player != null) {
            player.stop();
            player.dispose();
        }
    }
    /**
     * Executes the operation represented by reportMissing.
     * @param path the value used by the method
     */
    private static void reportMissing(String path) {
        System.err.println("[AUDIO] Missing resource: " + path);
    }
}
