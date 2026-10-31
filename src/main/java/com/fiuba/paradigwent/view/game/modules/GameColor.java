package com.fiuba.paradigwent.view.game.modules;

import javafx.scene.paint.Color;

/**
 * Palette of the game board. An enum keeps the palette constant: unlike a
 * public map, nobody can add, remove or replace a color at runtime.
 */
public enum GameColor {
    SIEGE_LINE(Color.web("#d23737")),
    RANGED_LINE(Color.web("#00ff00")),
    MELEE_LINE(Color.web("#5337d2")),
    WEATHER(Color.web("#e1a037")),
    DECK(Color.web("#a51f7d")),
    DISCARD(Color.web("#6d0e0e")),
    BOARD(Color.web("#3a4b68")),
    BOARD_BORDER(Color.rgb(12, 15, 22, 0.88)),
    BOARD_BACKGROUND(Color.rgb(18, 22, 32, 0.9)),
    GEM(Color.web("#800f2f")),
    ROW(Color.rgb(20, 26, 38, 0.85)),
    ROW_TEXT(Color.web("#d0d6e0"));

    private final Color color;

    GameColor(Color color) {
        this.color = color;
    }

    /**
     * @return the JavaFX color (colors are immutable)
     */
    public Color color() {
        return color;
    }
}
