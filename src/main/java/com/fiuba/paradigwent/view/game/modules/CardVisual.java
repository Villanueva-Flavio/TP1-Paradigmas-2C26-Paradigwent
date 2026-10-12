package com.fiuba.paradigwent.view.game.modules;

import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.card.CardAssetResolver;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.Node;
import java.util.HashMap;
import java.util.Map;

public class CardVisual extends StackPane {
    private static final double SOURCE_WIDTH = 637;
    private static final double SOURCE_HEIGHT = 1024;
    private static final double ART_X = 135;
    private static final double ART_Y = 215;
    private static final double ART_WIDTH = 365;
    private static final double ART_HEIGHT = 365;

    private final ImageView artwork;
    private final ImageView frame;
    private final Map<Node, double[]> placedNodes = new HashMap<>();
    private StackPane metadata;
    private VBox textContent;
    private Label nameLabel;
    private Label powerLabel;
    private Label descriptionLabel;
    private double scale;

    public CardVisual(Card card, double width, double height, String imageStyleClass) {
        artwork = new ImageView(new Image(resource(CardAssetResolver.resolve(card))));
        frame = new ImageView(new Image(resource(CardAssetResolver.resolveFactionFrame(card))));

        artwork.setPreserveRatio(true);
        artwork.getStyleClass().add(imageStyleClass);
        frame.setPreserveRatio(true);
        frame.getStyleClass().add("card-frame");
        artwork.setMouseTransparent(true);
        frame.setMouseTransparent(true);
        artwork.setManaged(false);
        frame.setManaged(false);

        getChildren().addAll(artwork, frame);
        updateSize(width, height);
    }

    public void updateSize(double width, double height) {
        scale = Math.min(width / SOURCE_WIDTH, height / SOURCE_HEIGHT);
        setMinSize(width, height);
        setPrefSize(width, height);
        frame.setFitWidth(width);
        frame.setFitHeight(height);
        frame.relocate(0, 0);
        artwork.setFitWidth(ART_WIDTH * scale);
        artwork.setFitHeight(ART_HEIGHT * scale);
        artwork.relocate(ART_X * scale, ART_Y * scale);
        if (metadata != null) {
            double textWidth = 365 * scale;
            double textHeight = 225 * scale;
            textContent.setPrefWidth(textWidth);
            textContent.setMaxWidth(textWidth);
            textContent.setSpacing(4 * scale);
            descriptionLabel.setPrefWidth(textWidth);
            descriptionLabel.setMaxWidth(textWidth);
            nameLabel.setStyle(fontStyle(38 * scale));
            powerLabel.setStyle(fontStyle(44 * scale));
            descriptionLabel.setStyle(fontStyle(22 * scale));
            StackPane.setMargin(powerLabel,
                new javafx.geometry.Insets(8 * scale, 0, 0, 10 * scale));
            metadata.setClip(new Rectangle(textWidth, textHeight));
        }
        placedNodes.forEach((node, bounds) -> applyPlacement(node, bounds));
    }

    public void place(Node node, double x, double y, double width, double height) {
        node.setManaged(false);
        placedNodes.put(node, new double[] {x, y, width, height});
        applyPlacement(node, placedNodes.get(node));
        if (!getChildren().contains(node)) {
            getChildren().add(node);
        }
    }

    private void applyPlacement(Node node, double[] bounds) {
        node.setScaleX(1);
        node.setScaleY(1);
        node.resize(bounds[2] * scale, bounds[3] * scale);
        node.relocate(bounds[0] * scale, bounds[1] * scale);
    }

    public ImageView getArtwork() {
        return artwork;
    }

    public void addMetadata(Card card) {
        nameLabel = metadataLabel(card.getName(), 38);
        powerLabel = metadataLabel(Integer.toString(card.getBasePower()), 44);
        descriptionLabel = metadataLabel(card.getDescription(), 22);
        descriptionLabel.setWrapText(true);

        textContent = new VBox(4, nameLabel, descriptionLabel);
        textContent.setAlignment(javafx.geometry.Pos.CENTER);
        textContent.setMouseTransparent(true);

        metadata = new StackPane(textContent, powerLabel);
        metadata.setMouseTransparent(true);
        StackPane.setAlignment(powerLabel, javafx.geometry.Pos.TOP_LEFT);
        place(metadata, 135, 653, 365, 225);
        updateSize(getPrefWidth(), getPrefHeight());
    }

    private Label metadataLabel(String text, int size) {
        Label label = new Label(text == null ? "" : text);
        label.setTextFill(Color.rgb(45, 25, 15));
        label.setStyle(fontStyle(size));
        return label;
    }

    private String fontStyle(double size) {
        return "-fx-font-size: " + Math.max(7, size) + "px; -fx-font-weight: bold;";
    }

    private static String resource(String path) {
        return CardVisual.class.getResource(path).toExternalForm();
    }
}
