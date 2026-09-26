package com.fiuba.paradigwent.model.board;

import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.player.Player;

public class Board {

    private PlayerSide playerSide;
    private PlayerSide opponentSide;
    private AttackLineType currentWeather;

    public Board(Player player, Player opponent){
        this.playerSide = new PlayerSide(player);
        this.opponentSide = new PlayerSide(opponent);
        this.currentWeather = AttackLineType.valueOf("NONE");
    }

    public PlayerSide getPlayerSide() {
        return playerSide;
    }

    public PlayerSide getOpponentSide() {
        return opponentSide;
    }

    public Card getTargetCard() {
        return null;
    }

    public void setCurrentWeather(AttackLineType currentWeather) {
        this.currentWeather = currentWeather;
    }

    public void discard() {
        playerSide.discard();
        opponentSide.discard();
        this.currentWeather = AttackLineType.valueOf("NONE");

    }

    public Minion getStrongestMiniondOf(PlayerSide opponent) {

        Minion strongestMinion =

        return strongestMinion;
    }


