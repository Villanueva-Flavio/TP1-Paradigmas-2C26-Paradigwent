package com.fiuba.paradigwent.view.game.modules;

import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.card.CardAssetResolver;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.Node;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import java.util.HashMap;
import java.util.Map;

public class CardVisual extends StackPane {
    private static final double SOURCE_WIDTH = 637;
    private static final double SOURCE_HEIGHT = 1024;
    private static final double ART_X = 135;
    private static final double ART_Y = 215;
    private static final double ART_WIDTH = 365;
    private static final double ART_HEIGHT = 365;
    private static final double CARD_ASPECT_RATIO = SOURCE_WIDTH / SOURCE_HEIGHT;

    private final ImageView artwork;
    private final ImageView frame;
    private final Map<Node, double[]> placedNodes = new HashMap<>();
    private StackPane metadata;
    private VBox textContent;
    private HBox cardHeader;
    private Label nameLabel;
    private Label powerLabel;
    private Label descriptionLabel;
    private ImageView typeIcon;
    private Label effectLabel;
    private double scale;
    private boolean compactMetadata;

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
        double cardWidth = Math.min(width, height * CARD_ASPECT_RATIO);
        double cardHeight = cardWidth / CARD_ASPECT_RATIO;
        scale = cardWidth / SOURCE_WIDTH;
        double offsetX = (width - cardWidth) / 2;
        double offsetY = (height - cardHeight) / 2;
        setMinSize(cardWidth, cardHeight);
        setPrefSize(cardWidth, cardHeight);
        frame.setFitWidth(cardWidth);
        frame.setFitHeight(cardHeight);
        frame.relocate(offsetX, offsetY);
        artwork.setFitWidth(ART_WIDTH * scale * 1.1);
        artwork.setFitHeight(ART_HEIGHT * scale * 1.1);
        artwork.relocate(offsetX + ART_X * scale, offsetY + ART_Y * scale * 0.9);
        if (metadata != null) {
            double textWidth = 365 * scale;
            double textHeight = 225 * scale;
            textContent.setPrefWidth(textWidth);
            textContent.setMinWidth(textWidth);
            textContent.setMaxWidth(textWidth);
            textContent.setSpacing(4 * scale);
            cardHeader.setPrefWidth(textWidth);
            cardHeader.setMinWidth(textWidth);
            cardHeader.setMaxWidth(textWidth);
            double reservedHeaderWidth = (compactMetadata ? 40 : 40) * scale;
            double powerWidth = powerLabel.isManaged() ? 52 * scale : 0;
            double nameWidth = Math.max(0, textWidth - reservedHeaderWidth - powerWidth);
            nameLabel.setMaxWidth(nameWidth);
            nameLabel.setTextOverrun(OverrunStyle.ELLIPSIS);
            descriptionLabel.setPrefWidth(textWidth);
            descriptionLabel.setMaxWidth(textWidth);
            nameLabel.setStyle(fontStyle(fitNameFont(nameLabel.getText(),
                nameWidth, 38 * scale)));
            powerLabel.setStyle(fontStyle(compactMetadata ? 38 * scale : 32 * scale));
            descriptionLabel.setStyle(fontStyle(22 * scale));
            metadata.setClip(new Rectangle(textWidth, textHeight));
            typeIcon.setFitWidth(34 * scale);
            typeIcon.setFitHeight(34 * scale);
            effectLabel.setStyle(fontStyle(20 * scale));
            if (compactMetadata) {
                effectLabel.setVisible(false);
                effectLabel.setManaged(false);
                descriptionLabel.setVisible(false);
                descriptionLabel.setManaged(false);
            }
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

    public static double widthForHeight(double height) {
        return height * CARD_ASPECT_RATIO;
    }

    private double fitNameFont(String text, double availableWidth, double preferredSize) {
        double size = preferredSize;
        while (size > 8 && textWidth(text, size) > availableWidth) {
            size -= 1;
        }
        return Math.max(8, size);
    }

    private double textWidth(String text, double size) {
        Text measured = new Text(text == null ? "" : text);
        measured.setFont(Font.font("System", FontWeight.BOLD, size));
        return measured.getLayoutBounds().getWidth();
    }

    private ImageView cardTypeIcon(String cardType) {
        String fileName = switch (cardType) {
            case "MINION" -> "minion.png";
            case "SPELL" -> "spell.png";
            case "WEATHER" -> "weather.png";
            default -> "noimage.png";
        };
        ImageView icon = new ImageView(new Image(resource(
            "/images/cards/" + fileName), true));
        icon.setPreserveRatio(true);
        icon.setFitWidth(42 * scale);
        icon.setFitHeight(42 * scale);
        icon.setMouseTransparent(true);
        return icon;
    }

    public void addMetadata(Card card) {
        addMetadata(card, false);
    }

    public void addMetadata(Card card, boolean compact) {
        compactMetadata = compact;
        nameLabel = metadataLabel(card.getName(), 38);
        nameLabel.setTextOverrun(OverrunStyle.ELLIPSIS);
        powerLabel = metadataLabel(Integer.toString(card.getBasePower()), 44);
        powerLabel.setVisible(card.getBasePower() > 0);
        powerLabel.setManaged(card.getBasePower() > 0);
        descriptionLabel = metadataLabel(card.getDescription(), 22);
        descriptionLabel.setWrapText(true);
        typeIcon = cardTypeIcon(card.getCardType());
        String effect = card.getEffectDescription();
        String target = card.getTargetDescription();
        String effectText = effect.isBlank() ? "" : effect;
        effectLabel = metadataLabel(
            target.isBlank() ? effectText : effectText + "\n" + target, 20);

        cardHeader = new HBox(6, typeIcon, powerLabel, nameLabel);
        cardHeader.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        cardHeader.setMinWidth(365);
        cardHeader.setPrefWidth(365);
        cardHeader.setMaxWidth(365);
        textContent = new VBox(4, cardHeader, effectLabel, descriptionLabel);
        textContent.setAlignment(javafx.geometry.Pos.TOP_LEFT);
        textContent.setMinWidth(365);
        textContent.setPrefWidth(365);
        textContent.setMaxWidth(365);
        textContent.setMouseTransparent(true);

        metadata = new StackPane(textContent);
        metadata.setMouseTransparent(true);
        if (compactMetadata) {
            effectLabel.setVisible(false);
            effectLabel.setManaged(false);
            descriptionLabel.setVisible(false);
            descriptionLabel.setManaged(false);
        }
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
