package com.fiuba.paradigwent.view.game;

import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.enums.Result;
import com.fiuba.paradigwent.model.enums.WeatherType;
import com.fiuba.paradigwent.model.event.GameListener;
import com.fiuba.paradigwent.model.player.Player;
import com.fiuba.paradigwent.view.AudioManager;

/**
 * Plays a sound for every event of the match. The model tells what happened;
 * nobody has to ask an effect which class it is to choose the sound.
 */
public class GameSoundEffects implements GameListener {
    private final AudioManager audioManager;

    /**
     * @param audioManager player of music and sound effects
     */
    public GameSoundEffects(AudioManager audioManager) {
        this.audioManager = audioManager;
    }

    @Override
    public void cardPlayed(Player player, Card card) {
        audioManager.playCardPlay();
    }

    @Override
    public void minionDestroyed(Minion minion) {
        audioManager.playDestroy();
    }

    @Override
    public void minionRevived(Minion minion) {
        audioManager.playRevive();
    }

    @Override
    public void powerModified(Minion minion, int amount) {
        audioManager.playBuff();
    }

    @Override
    public void lineDuplicated(AttackLineType line) {
        audioManager.playDuplicateLine();
    }

    @Override
    public void weatherChanged(WeatherType weather) {
        audioManager.updateWeatherMusic(weather);
    }

    @Override
    public void cardsStolen(int amount) {
        audioManager.playCardDraw();
    }

    @Override
    public void turnPassed(Player player) {
        audioManager.playPass();
    }

    @Override
    public void roundStarted(Player startingPlayer) {
        audioManager.updateWeatherMusic(WeatherType.CLEAR);
    }

    @Override
    public void roundFinished(Result result) {
        audioManager.playRoundEnd();
    }

    /**
     * The human is always player 1, so only a player 1 win is a victory.
     */
    @Override
    public void matchFinished(Result result) {
        if (result == Result.PLAYER1_WIN) {
            audioManager.playVictory();
        } else {
            audioManager.playDefeat();
        }
    }
}
