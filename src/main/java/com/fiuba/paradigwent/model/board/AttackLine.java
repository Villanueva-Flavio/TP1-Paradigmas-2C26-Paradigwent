package com.fiuba.paradigwent.model.board;
import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.enums.AttackLineType;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class AttackLine {
    private List<Minion> minions;
    private boolean activateHorn;
    private final AttackLineType type;

    /**
     * Constructor for the AttackLine class.
     * @param type The type of the attack line (MELEE, RANGED, SIEGE).
    **/
    public AttackLine(AttackLineType type){
        this.minions = new ArrayList<>();
        this.activateHorn  = false;
        this.type = type;
    }

    /**
     * Calculates the total power of the attack line, considering the horn effect if activated.
     * @return The total power of the attack line.
    **/
    public int calculateTotalPower(){
        int total = minions.stream().mapToInt(Minion::getCurrentPower).sum();
        return activateHorn ? 2 * total : total;
    }

    /**
     * Adds a minion to the attack line.
     * @param minion
    **/
    public void addMinion(Minion minion){
        this.minions.add(minion);
    }

    /**
     * Removes a minion from the attack line.
     * @param minion
    **/
    public void removeMinion(Minion minion){
        this.minions.remove(minion);
    }

    /**
     * Activates the horn effect for the attack line, doubling its total power.
    **/
    public void activateHorn(){
        this.activateHorn = true;
    }

    /**
     * Checks if the horn effect is activated for the attack line.
     * @return True if the horn effect is activated, false otherwise.
    **/
    public boolean hasHorn() {
        return activateHorn;
    }

    /**
     * Returns the type of the attack line.
     * @return The type of the attack line (MELEE, RANGED, SIEGE).
    **/
    public AttackLineType getType() {
        return type;
    }

    public List<Minion> getMinions() {
        return Collections.unmodifiableList(minions);
    }

    /**
     * Returns the strongest minion in the attack line based on its current power.
     * @return The strongest minion in the attack line, or null if there are no minions.
    **/
    public Minion getStrongestMinion(){
        if (minions.isEmpty()) return null;
        Minion strongestMinion = minions.get(0);
        for(Minion minion : this.minions){
            if(minion.getCurrentPower()>strongestMinion.getCurrentPower()){
                strongestMinion = minion;
            }
        }
        return strongestMinion;
    }

    /**
     * Removes all minions from the attack line and returns a copy of the removed minions.
     * @return A list of the removed minions from the attack line.
    **/
    public List<Minion> takeMinions() {
        List<Minion> copy = List.copyOf(minions);
        minions.clear();
        return copy;
    }

    /**
     *  Deactivates the horn effect for the attack line, returning its total power to normal.
    **/
    public void deActivateHorn() {
        this.activateHorn = false;
    }
}
