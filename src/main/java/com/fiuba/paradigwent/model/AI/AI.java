package com.fiuba.paradigwent.model.AI;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.player.Deck;
import com.fiuba.paradigwent.model.player.Hand;
import com.fiuba.paradigwent.model.player.Player;

import java.util.Random;

public class AI extends Player implements AIStrategy {

    private final Random random = new Random();

    public AI(String name, Deck deck) {
        super(name, deck);
    }

    public void decideActions(GameContext context) {

    }

    public Card selectCard(Hand hand, GameContext context){
        if (hand.getHand().isEmpty()) {
            return null;
        }
        return hand.getHand().get(random.nextInt(hand.getHand().size()));
    }

}
