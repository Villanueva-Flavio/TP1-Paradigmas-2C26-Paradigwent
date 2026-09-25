package com.fiuba.paradigwent.view.game.modules;

import com.fiuba.paradigwent.view.game.GameView;

import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class RowView extends StackPane {

    private static final double ROW_WIDTH = 700;
    private static final double ROW_HEIGHT = 120;

    public RowView(String labelText, Color strokeColor) {
        setPrefSize(ROW_WIDTH, ROW_HEIGHT);
        setMaxSize(ROW_WIDTH, ROW_HEIGHT);

        Rectangle rectFila = new Rectangle(ROW_WIDTH, ROW_HEIGHT, GameView.COLOR.get("Row"));
        rectFila.setStroke(strokeColor);
        rectFila.setStrokeWidth(1.5);
        rectFila.setArcWidth(8);
        rectFila.setArcHeight(8);

        Text text = new Text(labelText);
        text.setFont(Font.font("System", FontWeight.BOLD, 12));
        text.setFill(GameView.COLOR.get("RowText"));
        StackPane.setAlignment(text, Pos.CENTER_LEFT);
        text.setTranslateX(15);

        HBox slotsCartas = new HBox(10);
        slotsCartas.setAlignment(Pos.CENTER);

        slotsCartas.setTranslateX(20);

        for (int i = 0; i < 9; i++) {
            Rectangle cartaDummy = new Rectangle(60, 100, GameView.COLOR.get("DummyCard"));
            cartaDummy.setStroke(GameView.COLOR.get("DummyCardStroke"));
            cartaDummy.setStrokeWidth(1);
            cartaDummy.setArcWidth(4);
            cartaDummy.setArcHeight(4);
            cartaDummy.setEffect(new DropShadow(4, GameView.COLOR.get("DummyCardShadow")));
            slotsCartas.getChildren().add(cartaDummy);
        }

        getChildren().addAll(rectFila, text, slotsCartas);
    }
}