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

    /**
     * Constructor for the PlayerSide class.
     * @param owner The Player object representing the owner of this side of the board.
    **/
    public PlayerSide(Player owner) {
        this.owner = owner;
        for (AttackLineType type : AttackLineType.values()) attackLines.put(type, new AttackLine(type));
    }

    /**
     * Returns the AttackLine object corresponding to the specified AttackLineType.
     * @param type
     * @return The AttackLine object for the specified type.
    **/
    public AttackLine getAttackLine(AttackLineType type){return attackLines.get(type);}

    /**
     * Calculates the total power of all attack lines on this side of the board.
     * @return The total power of all attack lines.
    **/
    public int totalPower() {
        return attackLines.values().stream().mapToInt(AttackLine::calculateTotalPower).sum();
    }

    /**
     * Discards all minions from the attack lines and sends them to the owner's discard pile. Also deactivates any horn effects on the attack lines.
    **/
    public void discard() {
        List<Minion> minions = new ArrayList<>();
        for (AttackLine line : attackLines.values()) {
            minions.addAll(line.takeMinions());
            line.deActivateHorn();
        }
        owner.sendToDiscard(minions);
    }

    /**
     * Returns the strongest minion on this side of the board based on its current power.
     * @return The strongest minion on this side, or null if there are no minions.
    **/
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

    public List<Minion> allMinions() {
        List<Minion> result = new ArrayList<>();
        for (AttackLine line : attackLines.values()) {
            result.addAll(line.getMinions());
        }
        return result;
    }

    public boolean contains(Minion minion) {
        return attackLines.values().stream()
            .anyMatch(line -> line.getMinions().contains(minion));
    }

    public boolean removeMinion(Minion minion) {
        return attackLines.values().stream().anyMatch(line -> line.removeIfPresent(minion));
    }

    /**
     * @return The Player object representing the owner of this side of the board.
    **/
    public Player getOwner() { return owner; }
}
