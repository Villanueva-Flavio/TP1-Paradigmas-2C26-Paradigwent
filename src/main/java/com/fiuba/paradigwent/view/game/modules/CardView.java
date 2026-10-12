package com.fiuba.paradigwent.view.game.modules;

import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.card.CardAssetResolver;

import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.input.MouseEvent;
import java.util.function.Consumer;

public class CardView extends StackPane {
    public static final double HEIGHT = 126;
    public static final double WIDTH = HEIGHT / 1.46;
    public static final double HIT_AREA_WIDTH = 100;
    public static final double HIT_AREA_HEIGHT = 150;

    private final Card card;
    private final CardVisual visual;

    public CardView(Card card) {
        this.card = card;
        visual = new CardVisual(card, WIDTH, HEIGHT, "hand-card");

        setPrefSize(HIT_AREA_WIDTH, HIT_AREA_HEIGHT);
        setMinSize(HIT_AREA_WIDTH, HIT_AREA_HEIGHT);
        setPickOnBounds(true);

        visual.addMetadata(card);
        visual.setMouseTransparent(true);
        getChildren().add(visual);
        addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                onCardClicked.accept(card);
                event.consume();
            }
        });
    }

    private Consumer<Card> onCardClicked = ignored -> { };

    public void setOnCardClicked(Consumer<Card> callback) {
        onCardClicked = callback == null ? ignored -> { } : callback;
    }

    public Card getCard() {
        return card;
    }

    public ImageView getImageView() {
        return visual.getArtwork();
    }
}
