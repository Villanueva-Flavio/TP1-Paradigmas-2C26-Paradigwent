package com.fiuba.paradigwent.model.board;

import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.player.Player;

public class Board {

    private PlayerSide playerSide;
    private PlayerSide opponentSide;
    private AttackLineType currentWheater;

    public Board(Player player, Player opponent){
        this.playerSide = new PlayerSide(player);
        this.opponentSide = new PlayerSide(opponent);
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

    public void clear(){
        playerSide.clear();
        opponentSide.clear();

    }
}
