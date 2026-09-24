package com.fiuba.paradigwent.model.board;

import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.player.Player;

import java.util.EnumMap;
import java.util.Map;

public class PlayerSide {

    private final Player owner;
    private final Map<AttackLineType, AttackLine> attackLines = new EnumMap<>(AttackLineType.class);

    public PlayerSide(Player owner) {
        this.owner = owner;
        for (AttackLineType type : AttackLineType.values()) attackLines.put(type, new AttackLine(type));
    }

    public AttackLine getAttackLine(AttackLineType type){return attackLines.get(type);}

    public int totalPower() {
        return attackLines.values().stream().mapToInt(AttackLine::calculateTotalPower).sum();
    }
    public void clear() { attackLines.values().forEach(AttackLine::clear); }
    public Player getOwner() { return owner; }

}
