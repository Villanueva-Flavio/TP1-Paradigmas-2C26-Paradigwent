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

    public RowView(String labelText, Color strokeColor, double rowWidth, double rowHeight) {
        setPrefSize(rowWidth, rowHeight);
        setMaxSize(rowWidth, rowHeight);

        Rectangle rectFila = new Rectangle(rowWidth, rowHeight, GameView.COLOR.get("Row"));
        rectFila.setStroke(strokeColor);
        rectFila.setStrokeWidth(rowWidth * (1.5 / 700.0));
        rectFila.setArcWidth(rowWidth * (8.0 / 700.0));
        rectFila.setArcHeight(rowWidth * (8.0 / 700.0));

        Text text = new Text(labelText);
        text.setFont(Font.font("System", FontWeight.BOLD, rowHeight * 0.1));
        text.setFill(GameView.COLOR.get("RowText"));
        StackPane.setAlignment(text, Pos.CENTER_LEFT);
        text.setTranslateX(rowWidth * (15.0 / 700.0));

        HBox slotsCartas = new HBox(rowWidth * (10.0 / 700.0));
        slotsCartas.setAlignment(Pos.CENTER);

        slotsCartas.setTranslateX(rowWidth * (20.0 / 700.0));

        for (int i = 0; i < 9; i++) {
            Rectangle cartaDummy = new Rectangle(rowWidth * (60.0 / 700.0), rowHeight * (100.0 / 120.0), GameView.COLOR.get("DummyCard"));
            cartaDummy.setStroke(GameView.COLOR.get("DummyCardStroke"));
            cartaDummy.setStrokeWidth(rowWidth * (1.0 / 700.0));
            cartaDummy.setArcWidth(rowWidth * (4.0 / 700.0));
            cartaDummy.setArcHeight(rowWidth * (4.0 / 700.0));
            cartaDummy.setEffect(new DropShadow(rowWidth * (4.0 / 700.0), GameView.COLOR.get("DummyCardShadow")));
            slotsCartas.getChildren().add(cartaDummy);
        }

        getChildren().addAll(rectFila, text, slotsCartas);
    }
}