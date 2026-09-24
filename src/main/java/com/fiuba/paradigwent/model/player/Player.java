package com.fiuba.paradigwent.model.player;

public class Player {

    private final String name;
    private int lives = 2;
    private final Deck deck;
    private final Hand hand;


    public Player(String name, Deck deck) {
        this.name = name;
        this.deck = deck;
        this.hand = new Hand();
        draw(10);
    }

    public void draw(int n) {
        for (int i = 0; i < n && !deck.isEmpty(); i++) hand.add(deck.draw());
    }

    public void play(Card c, Board board) {
        hand.remove(c);
        c.playOn(board, this);
    }


}
