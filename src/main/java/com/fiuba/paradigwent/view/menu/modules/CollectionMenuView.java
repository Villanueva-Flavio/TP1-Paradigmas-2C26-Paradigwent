package com.fiuba.paradigwent.view.menu.modules;

import com.fiuba.paradigwent.view.MainView;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.util.List;
import java.util.Map;

public class CollectionMenuView extends VBox {
    private static final int CARDS_PER_PAGE = 10;
    private static final List<String> TYPE_ORDER = List.of("WEATHER", "SPELL", "MINION");
    private static final List<String> FACTION_ORDER = List.of(
        "DWARF", "HIGHELF", "HUMAN", "MURGOL", "ORC", "UNDEAD"
    );
    private static final Map<String, List<String>> TYPE_ASSETS = Map.of(
        "WEATHER", repeatedPattern(List.of("card-highelf.png", "card-undead.png"), 12),
        "SPELL", repeatedPattern(List.of("card-dwarf.png", "card-murgol.png"), 12),
        "MINION", repeatedPattern(List.of("card-human.png", "card-orc.png"), 12)
    );
    private static final Map<String, List<String>> FACTION_ASSETS = Map.of(
        "DWARF", repeatedAsset("card-dwarf.png", 12),
        "HIGHELF", repeatedAsset("card-highelf.png", 12),
        "HUMAN", repeatedAsset("card-human.png", 12),
        "MURGOL", repeatedAsset("card-murgol.png", 12),
        "ORC", repeatedAsset("card-orc.png", 12),
        "UNDEAD", repeatedAsset("card-undead.png", 12)
    );
    private static final List<String> ALL_CARDS = FACTION_ORDER.stream()
        .flatMap(faction -> FACTION_ASSETS.get(faction).stream())
        .toList();
    private final GridPane cardGrid = new GridPane();
    private final ScrollPane cardScroll = new ScrollPane(cardGrid);
    private final MainView mainView;
    private Button previousPageButton;
    private Button nextPageButton;
    private String activeCollection;
    private List<String> activeCards = ALL_CARDS;
    private int currentPage;
    private boolean pageAnimating;
    private StackPane previewOverlay;
    private boolean previewLocked;
    private Button activeFilterButton;
    private final PauseTransition previewHideDelay = new PauseTransition(Duration.millis(180));

    public CollectionMenuView(MainView mainView) {
        this.mainView = mainView;
        setSpacing(12);
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(24, 32, 24, 24));
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        getStyleClass().add("collection-menu");

        Label lblTitle = new Label("COLLECTION");
        lblTitle.getStyleClass().add("menu-label");
        lblTitle.setFont(Font.font(mainView.getFontFamilyName(), 20));

        TextField searchBar = new TextField();
        searchBar.setPromptText("SEARCH BY NAME");
        searchBar.getStyleClass().add("collection-search");

        HBox factionButtons = createFactionButtons();
        HBox typeFilters = createTypeFilters();
        renderCards(false, 0);

        cardScroll.setFitToWidth(true);
        cardScroll.setFitToHeight(true);
        cardScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        cardScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        cardScroll.getStyleClass().add("collection-scroll");
        VBox.setVgrow(cardScroll, javafx.scene.layout.Priority.ALWAYS);
        cardScroll.widthProperty().addListener((observable, oldValue, newValue) -> resizeCards());
        cardScroll.heightProperty().addListener((observable, oldValue, newValue) -> resizeCards());
        cardScroll.viewportBoundsProperty().addListener((observable, oldValue, newValue) -> resizeCards());
        Platform.runLater(() -> {
            resizeCards();
            Platform.runLater(this::resizeCards);
        });

        HBox pagination = createPagination();

        Button btnBack = mainView.createMenuButton("BACK");
        btnBack.setOnAction(e -> mainView.showMainMenu());

        getChildren().addAll(lblTitle, searchBar, factionButtons, typeFilters, cardScroll, pagination, btnBack);
    }

    private static List<String> repeatedAsset(String asset, int amount) {
        return java.util.stream.IntStream.range(0, amount)
            .mapToObj(index -> asset)
            .toList();
    }

    private static List<String> repeatedPattern(List<String> assets, int amount) {
        return java.util.stream.IntStream.range(0, amount)
            .mapToObj(index -> assets.get(index % assets.size()))
            .toList();
    }

    private HBox createFactionButtons() {
        HBox buttons = new HBox(8);
        buttons.setAlignment(Pos.CENTER);
        buttons.getStyleClass().add("collection-factions");
        FACTION_ORDER.stream()
            .map(this::createFactionButton)
            .forEach(buttons.getChildren()::add);
        return buttons;
    }

    private Button createFactionButton(String faction) {
        Button button = new Button();
        button.setGraphic(createCardImage(
            FACTION_ASSETS.get(faction).get(0), 48, 68, false));
        button.setFocusTraversable(false);
        button.getStyleClass().add("collection-faction-button");
        button.setAccessibleText(faction);
        button.setOnAction(event -> toggleCollectionFilter(faction, button));
        return button;
    }

    private HBox createTypeFilters() {
        HBox filters = new HBox(8);
        filters.setAlignment(Pos.CENTER);
        filters.getStyleClass().add("collection-filters");
        for (String type : TYPE_ORDER) {
            Button filter = new Button();
            filter.setUserData(type);
            filter.setAccessibleText(type);
            filter.getStyleClass().add("collection-filter");
            filter.setOnAction(event -> toggleCollectionFilter(type, filter));
            filters.getChildren().add(filter);
        }
        return filters;
    }

    private HBox createPagination() {
        previousPageButton = new Button("<");
        nextPageButton = new Button(">");
        previousPageButton.getStyleClass().add("collection-page-button");
        nextPageButton.getStyleClass().add("collection-page-button");
        previousPageButton.setOnAction(event -> changePage(-1));
        nextPageButton.setOnAction(event -> changePage(1));

        HBox pagination = new HBox(16, previousPageButton, nextPageButton);
        pagination.setAlignment(Pos.CENTER);
        pagination.getStyleClass().add("collection-pagination");
        updatePageButtons();
        return pagination;
    }

    private void toggleCollectionFilter(String collection, Button filter) {
        if (activeFilterButton == filter) {
            filter.getStyleClass().remove("active");
            activeFilterButton = null;
            selectCollection(null);
            return;
        }

        if (activeFilterButton != null) activeFilterButton.getStyleClass().remove("active");
        filter.getStyleClass().add("active");
        activeFilterButton = filter;
        selectCollection(collection);
    }

    private void selectCollection(String collection) {
        hidePreview();
        activeCollection = collection;
        activeCards = collection == null
            ? ALL_CARDS
            : TYPE_ASSETS.containsKey(collection)
                ? TYPE_ASSETS.get(collection)
                : FACTION_ASSETS.get(collection);
        currentPage = 0;
        animatePage(1);
    }

    private void changePage(int direction) {
        hidePreview();
        int pageCount = (int) Math.ceil((double) activeCards.size() / CARDS_PER_PAGE);
        if (pageAnimating || currentPage + direction < 0 || currentPage + direction >= pageCount) return;
        currentPage += direction;
        animatePage(direction);
    }

    private void renderCards(boolean animate, int direction) {
        cardGrid.getChildren().clear();
        cardGrid.setAlignment(Pos.CENTER);
        cardGrid.setHgap(0);
        cardGrid.setVgap(0);
        cardGrid.getStyleClass().add("collection-card-grid");

        List<String> cards = activeCards;
        int start = currentPage * CARDS_PER_PAGE;
        int end = Math.min(start + CARDS_PER_PAGE, cards.size());
        double cardWidth = calculateCardWidth();
        for (int index = start; index < end; index++) {
            Node card = createCardSlot(cards.get(index), cardWidth, cardWidth * 1.46);
            int pageIndex = index - start;
            int columns = calculateColumnCount();
            cardGrid.add(card, pageIndex % columns, pageIndex / columns);
        }
        updatePageButtons();
        if (animate) animateGrid(direction);
    }

    private double calculateCardWidth() {
        if (cardScroll.getWidth() <= 0 || cardScroll.getHeight() <= 0) return 112;
        double availableWidth = cardScroll.getWidth() - 32;
        double availableHeight = cardScroll.getHeight() - 32;
        int columns = calculateColumnCount();
        int rows = (int) Math.ceil((double) CARDS_PER_PAGE / columns);
        double widthBased = (availableWidth - columns * 12) / columns;
        double heightBased = (availableHeight - rows * 16) / (rows * 1.46);
        return Math.max(80, Math.min(widthBased, heightBased));
    }

    private int calculateColumnCount() {
        return 5;
    }

    private void resizeCards() {
        if (!cardGrid.getChildren().isEmpty()) resizeCardImages();
    }

    private void resizeCardImages() {
        double cardWidth = calculateCardWidth();
        double cardHeight = cardWidth * 1.46;
        cardGrid.lookupAll(".collection-card-slot").stream()
            .map(StackPane.class::cast)
            .forEach(slot -> {
                slot.setMinSize(cardWidth + 12, cardHeight + 12);
                slot.setPrefSize(cardWidth + 12, cardHeight + 12);
                slot.lookupAll(".collection-card-image").stream()
                    .map(ImageView.class::cast)
                    .forEach(card -> {
                        card.setFitWidth(cardWidth);
                        card.setFitHeight(cardHeight);
                    });
            });
        cardGrid.requestLayout();
    }

    private void animatePage(int direction) {
        if (cardGrid.getChildren().isEmpty()) {
            renderCards(false, 0);
            return;
        }

        pageAnimating = true;
        FadeTransition fadeOut = new FadeTransition(Duration.millis(140), cardGrid);
        fadeOut.setToValue(0);
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(140), cardGrid);
        slideOut.setToX(-direction * 70);
        ParallelTransition exit = new ParallelTransition(fadeOut, slideOut);
        exit.setOnFinished(event -> {
            renderCards(false, 0);
            cardGrid.setOpacity(0);
            cardGrid.setTranslateX(direction * 70);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(220), cardGrid);
            fadeIn.setToValue(1);
            TranslateTransition slideIn = new TranslateTransition(Duration.millis(220), cardGrid);
            slideIn.setToX(0);
            ParallelTransition entrance = new ParallelTransition(fadeIn, slideIn);
            entrance.setOnFinished(done -> pageAnimating = false);
            entrance.play();
        });
        exit.play();
    }

    private void animateGrid(int direction) {
        cardGrid.setTranslateX(direction * 70);
        cardGrid.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(220), cardGrid);
        fadeIn.setToValue(1);
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(220), cardGrid);
        slideIn.setToX(0);
        new ParallelTransition(fadeIn, slideIn).play();
    }

    private void updatePageButtons() {
        if (previousPageButton == null || nextPageButton == null) return;
        int pageCount = (int) Math.ceil((double) activeCards.size() / CARDS_PER_PAGE);
        previousPageButton.setDisable(currentPage == 0);
        nextPageButton.setDisable(currentPage >= pageCount - 1);
    }

    private ImageView createCardImage(String assetName, double width, double height) {
        return createCardImage(assetName, width, height, true);
    }

    private StackPane createCardSlot(String assetName, double width, double height) {
        ImageView image = createCardImage(assetName, width, height, false);
        StackPane slot = new StackPane(image);
        slot.getStyleClass().add("collection-card-slot");
        slot.setPadding(new Insets(6));
        slot.setMinSize(width + 12, height + 12);
        slot.setPrefSize(width + 12, height + 12);
        configureCardHover(slot, assetName);
        return slot;
    }

    private ImageView createCardImage(String assetName, double width, double height,
                                      boolean interactive) {
        ImageView imageView = new ImageView(new Image(
            getClass().getResource("/images/cards/" + assetName).toExternalForm()));
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.getStyleClass().add("collection-card-image");
        if (interactive) configureCardHover(imageView, assetName);
        return imageView;
    }

    private void configureCardHover(Node card, String assetName) {
        Rotate rotateX = new Rotate(0, 0, 0, 0, Rotate.X_AXIS);
        Rotate rotateY = new Rotate(0, 0, 0, 0, Rotate.Y_AXIS);
        card.getTransforms().addAll(rotateX, rotateY);
        PauseTransition previewDelay = new PauseTransition(Duration.millis(500));
        final Timeline[] returnTransition = new Timeline[1];
        final boolean[] hovering = {false};

        card.setOnMouseEntered(event -> {
            hovering[0] = true;
            if (previewOverlay != null) hidePreview();
            previewHideDelay.stop();
            if (returnTransition[0] != null) returnTransition[0].stop();
            previewDelay.playFromStart();
        });
        card.setOnMouseMoved(event -> {
            double centerX = card.getBoundsInLocal().getWidth() / 2;
            double centerY = card.getBoundsInLocal().getHeight() / 2;
            double normalizedX = (event.getX() - centerX) / centerX;
            double normalizedY = (event.getY() - centerY) / centerY;
            rotateY.setAngle(-normalizedX * 16);
            rotateX.setAngle(normalizedY * 16);
            card.setTranslateX(normalizedX * 8);
            card.setTranslateY(normalizedY * 8);
            card.setScaleX(1.08);
            card.setScaleY(1.08);
        });
        card.setOnMouseExited(event -> {
            hovering[0] = false;
            previewDelay.stop();
            returnTransition[0] = new Timeline(
                new KeyFrame(Duration.millis(200),
                    new KeyValue(rotateX.angleProperty(), 0),
                    new KeyValue(rotateY.angleProperty(), 0),
                    new KeyValue(card.translateXProperty(), 0),
                    new KeyValue(card.translateYProperty(), 0),
                    new KeyValue(card.scaleXProperty(), 1),
                    new KeyValue(card.scaleYProperty(), 1)));
            returnTransition[0].play();
            if (!previewLocked) schedulePreviewHide();
        });
        previewDelay.setOnFinished(event -> {
            if (hovering[0]) showPreview(assetName);
        });
    }

    private void showPreview(String assetName) {
        hidePreview();
        previewLocked = false;
        previewOverlay = new StackPane();
        previewOverlay.getStyleClass().add("collection-preview-overlay");
        previewOverlay.setPickOnBounds(false);
        Region backdrop = new Region();
        backdrop.getStyleClass().add("collection-preview-backdrop");
        backdrop.setMouseTransparent(true);
        ImageView preview = createCardImage(assetName, 600, 860, false);
        preview.fitHeightProperty().bind(Bindings.min(
            860, mainView.getRoot().heightProperty().multiply(0.86)));
        preview.setOnMouseEntered(event -> previewHideDelay.stop());
        preview.setOnMouseExited(event -> {
            if (!previewLocked) schedulePreviewHide();
        });
        preview.setOnMouseClicked(event -> {
            event.consume();
            if (previewLocked) {
                hidePreview();
            } else {
                previewLocked = true;
            }
        });
        preview.setScaleX(1.02);
        preview.setScaleY(1.02);
        previewOverlay.getChildren().add(backdrop);
        previewOverlay.getChildren().add(preview);
        StackPane.setAlignment(preview, Pos.CENTER_RIGHT);
        StackPane.setMargin(preview, new Insets(0, 80, 0, 0));
        mainView.getRoot().getChildren().add(previewOverlay);
    }

    private void hidePreview() {
        previewHideDelay.stop();
        if (previewOverlay != null) {
            mainView.getRoot().getChildren().remove(previewOverlay);
            previewOverlay = null;
        }
        previewLocked = false;
    }

    private void schedulePreviewHide() {
        if (previewLocked) return;
        previewHideDelay.setOnFinished(event -> hidePreview());
        previewHideDelay.playFromStart();
    }
}