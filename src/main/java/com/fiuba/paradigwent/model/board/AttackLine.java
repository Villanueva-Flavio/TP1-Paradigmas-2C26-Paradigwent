package com.fiuba.paradigwent.model.board;
import com.fiuba.paradigwent.model.card.Card;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AttackLine {
    private List<Card> cards;
    private boolean activateHorn;

    public AttackLine(AttackLine type){
        this.cards = new ArrayList<>();
        this.activateHorn  = false;
    }

    public int calculateTotalPower(int power){
        int totalPower = 0;
        for(Card card : cards){
            totalPower += card.getCurrentPower();
        }
        if (activateHorn) {
            return 2 * totalPower;
        }
        return totalPower;
    }
    public void addCard(Card card){
        this.cards.add(card);
    }

    public void clear(Card card){
        this.cards.remove(card);
    }

    public void activateHorn(){
        this.activateHorn = true;
    }
}
