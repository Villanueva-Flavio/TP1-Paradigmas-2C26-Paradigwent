package com.fiuba.paradigwent.view.game.modules;

import com.fiuba.paradigwent.view.game.GameView;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class BoardZoneView extends StackPane {

    public BoardZoneView(String labelText, double x, double y, double width, double height, Color strokeColor) {
        setLayoutX(x);
        setLayoutY(y);
        setPrefSize(width, height);
        setMaxSize(width, height);

        Rectangle fondo = new Rectangle(width, height, GameView.COLOR.get("BoardBackground"));
        fondo.setStroke(strokeColor);
        fondo.setStrokeWidth(width * (1.5 / 60.0));
        fondo.setArcWidth(width * 0.1);
        fondo.setArcHeight(width * 0.1);

        Text etiqueta = new Text(labelText);
        etiqueta.setFont(Font.font("System", FontWeight.SEMI_BOLD, width * (11.0 / 60.0)));
        etiqueta.setTextAlignment(TextAlignment.CENTER);
        etiqueta.setFill(Color.LIGHTGRAY);

        getChildren().addAll(fondo, etiqueta);
    }
}