package com.fiuba.paradigwent.model.board;

import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.player.Player;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
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

    public void discard() {
        List<Minion> minions = new ArrayList<>();
        for (AttackLine line : attackLines.values()) {
            minions.addAll(line.takeMinions());
            line.deActivateHorn();
        }
        owner.sendToDiscard(minions);

    }

    public Minion getStrongestMinion() {
        Minion strongest = null;
        for (AttackLine line : attackLines.values()) {
            Minion candidate = line.getStrongestMinion();
            if (candidate == null) continue;
            if (strongest == null
                    || candidate.getCurrentPower() > strongest.getCurrentPower()) {
                strongest = candidate;
            }
        }
        return strongest;
    }

    public Player getOwner() { return owner; }

}
