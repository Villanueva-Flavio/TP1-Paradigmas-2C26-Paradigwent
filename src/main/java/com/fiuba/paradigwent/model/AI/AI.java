package com.fiuba.paradigwent.model.AI;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.player.Deck;
import com.fiuba.paradigwent.model.player.Hand;
import com.fiuba.paradigwent.model.player.Player;

import java.util.Random;
import java.util.random.RandomGenerator;

public class AI extends Player implements AIStrategy {

    Random random = new Random();

    public AI(String name, Deck deck) {
        super(name, deck);
    }

    public void decideActions(GameContext context) {

    }

    public Card selectCard(Hand hand, GameContext context){
        Card selectedCard = hand.getHand().get(random.nextInt(hand.getHand().size()));

        return selectedCard;
    }

}
