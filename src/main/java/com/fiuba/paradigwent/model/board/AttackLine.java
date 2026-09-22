package com.fiuba.paradigwent.model.board;
import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.card.Minion;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AttackLine {
    private List<Minion> minions;
    private boolean activateHorn;

    public AttackLine(AttackLine type){
        this.minions = new ArrayList<>();
        this.activateHorn  = false;
    }

    public int calculateTotalPower(int power){
        int totalPower = 0;
        for(Minion minion : minions){
            totalPower += minion.getCurrentPower();
        }
        if (activateHorn) {
            return 2 * totalPower;
        }
        return totalPower;
    }
    public void addMinion(Minion minion){
        this.minions.add(minion);
    }

    public void clear(){
        for(Minion m : minions) {
            this.minions.remove(m);
        }
    }

    public void activateHorn(){
        this.activateHorn = true;
    }
}
