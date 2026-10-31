package com.fiuba.paradigwent.model.board;

import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.enums.WeatherType;
import com.fiuba.paradigwent.model.player.Player;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Half of the board that belongs to one player. It owns one
 * {@link AttackLine} per {@link AttackLineType}.
 */
public class PlayerSide {

    private final Player owner;
    private final Map<AttackLineType, AttackLine> attackLines = new EnumMap<>(AttackLineType.class);

    /**
     * @param owner player that owns this side
     */
    public PlayerSide(Player owner) {
        this.owner = owner;
        for (AttackLineType type : AttackLineType.values()) attackLines.put(type, new AttackLine(type));
    }

    /**
     * @param type an attack line type
     * @return the line of that type
     */
    public AttackLine getAttackLine(AttackLineType type) {
        return attackLines.get(type);
    }

    /**
     * Places a minion on the line it belongs to.
     *
     * @param minion minion to deploy
     * @throws IllegalArgumentException if the minion has no playable line
     */
    public void deploy(Minion minion) {
        if (minion.getAttackLineType() == null || minion.getAttackLineType() == AttackLineType.NONE) {
            throw new IllegalArgumentException("A minion must belong to a playable line: " + minion);
        }
        getAttackLine(minion.getAttackLineType()).addMinion(minion);
    }

    /**
     * @return the sum of the power of every line
     */
    public int totalPower() {
        return attackLines.values().stream().mapToInt(AttackLine::calculateTotalPower).sum();
    }

    /**
     * Sends every minion to the owner's discard pile and removes the horns.
     */
    public void discard() {
        List<Minion> minions = new ArrayList<>();
        for (AttackLine line : attackLines.values()) {
            minions.addAll(line.takeMinions());
            line.deActivateHorn();
        }
        minions.forEach(Minion::resetPower);
        owner.sendToDiscard(minions);
    }

    /**
     * Marks the lines affected by the weather.
     *
     * @param weather the weather of the board
     */
    public void applyWeather(WeatherType weather) {
        for (AttackLineType type : AttackLineType.values()) {
            boolean affected = switch (weather) {
                case FROST -> type == AttackLineType.MELEE;
                case FOG -> type == AttackLineType.RANGED;
                case RAIN -> type == AttackLineType.SIEGE;
                case THUNDER -> type == AttackLineType.MELEE || type == AttackLineType.SIEGE;
                case SNOW -> type == AttackLineType.MELEE || type == AttackLineType.RANGED;
                case CLEAR -> false;
            };
            attackLines.get(type).setWeathered(affected);
        }
    }

    /**
     * @return the strongest minion of this side, or {@code null}
     */
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

    /**
     * @return every minion of this side
     */
    public List<Minion> allMinions() {
        List<Minion> result = new ArrayList<>();
        for (AttackLine line : attackLines.values()) {
            result.addAll(line.getMinions());
        }
        return result;
    }

    /**
     * @param minion a minion
     * @return {@code true} if the minion is on this side
     */
    public boolean contains(Minion minion) {
        return attackLines.values().stream()
            .anyMatch(line -> line.getMinions().contains(minion));
    }

    /**
     * @param minion minion to remove
     * @return {@code true} if the minion was on this side
     */
    public boolean removeMinion(Minion minion) {
        return attackLines.values().stream().anyMatch(line -> line.removeIfPresent(minion));
    }

    /**
     * Removes a minion and sends it to the owner's discard pile.
     *
     * @param minion minion to destroy
     * @return {@code true} if the minion was on this side
     */
    public boolean destroy(Minion minion) {
        if (!removeMinion(minion)) {
            return false;
        }
        minion.resetPower();
        owner.sendToDiscard(List.of(minion));
        return true;
    }

    /**
     * @param player a player
     * @return {@code true} if the player owns this side
     */
    public boolean isOwnedBy(Player player) {
        return owner == player;
    }

    /**
     * @return the player that owns this side
     */
    public Player getOwner() {
        return owner;
    }
}
