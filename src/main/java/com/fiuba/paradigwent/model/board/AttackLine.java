package com.fiuba.paradigwent.model.board;
import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.enums.AttackLineType;

import java.util.ArrayList;
import java.util.List;

public class AttackLine {
    private List<Minion> minions;
    private boolean activateHorn;
    private final AttackLineType type;

    public AttackLine(AttackLineType type){
        this.minions = new ArrayList<>();
        this.activateHorn  = false;
        this.type = type;
    }

    public int calculateTotalPower(){
        int total = minions.stream().mapToInt(Minion::getCurrentPower).sum();
        return activateHorn ? 2 * total : total;
    }
    public void addMinion(Minion minion){
        this.minions.add(minion);
    }

    public void removeMinion(Minion minion){
        this.minions.remove(minion);
    }


    /**public void activateHorn(){
        this.activateHorn = true;
    }**/


    public Minion getStrongestMinion(){
        Minion strongestMinion = minions.get(0);
        for(Minion minion : this.minions){
            if(minion.getCurrentPower()>strongestMinion.getCurrentPower()){
                strongestMinion = minion;
            }
        }
        return strongestMinion;
    }

    public List<Minion> takeMinions() {
        List<Minion> copy = List.copyOf(minions);
        minions.clear();
        return copy;
    }

    public void deActivateHorn() {
        this.activateHorn = false;
    }
}
