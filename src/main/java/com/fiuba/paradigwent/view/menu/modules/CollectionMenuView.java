package com.fiuba.paradigwent.view.menu.modules;

import com.fiuba.paradigwent.view.MainView;
import com.fiuba.paradigwent.model.card.Card;
import com.fiuba.paradigwent.model.card.JSONCardLoader;
import com.fiuba.paradigwent.view.game.modules.CardVisual;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.util.List;
import java.util.Locale;

public class CollectionMenuView extends VBox {
    private static final int CARDS_PER_PAGE = 10;
    private static final List<String> TYPE_ORDER = List.of("WEATHER", "SPELL", "MINION");
    private static final List<String> FACTION_ORDER = List.of(
        "DWARF", "HIGHELF", "HUMAN", "MURGOL", "ORC", "UNDEAD"
    );
    private List<Card> allCards = JSONCardLoader.loadCards();
    private final GridPane cardGrid = new GridPane();
    private final ScrollPane cardScroll = new ScrollPane(cardGrid);
    private final MainView mainView;
    private Button previousPageButton;
    private Button nextPageButton;
    private String activeCollection;
    private String searchTerm = "";
    private List<Card> activeCards = allCards;
    private int currentPage;
    private boolean pageAnimating;
    private StackPane previewOverlay;
    private boolean previewLocked;
    private Button activeFilterButton;
    private final PauseTransition previewHideDelay = new PauseTransition(Duration.millis(180));

    /**      * Constructs a new CollectionMenuView.      */
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
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            searchTerm = newValue == null ? "" : newValue.trim();
            refreshCards();
        });

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
    /**
     * Executes the operation represented by reloadCards.
     */
    public void reloadCards() {
        allCards = JSONCardLoader.loadCards();
        refreshCards();
    }
    /**
     * Executes the operation represented by createFactionButtons.
     * @return the value produced by the method
     */
    private HBox createFactionButtons() {
        HBox buttons = new HBox(8);
        buttons.setAlignment(Pos.CENTER);
        buttons.getStyleClass().add("collection-factions");
        FACTION_ORDER.stream()
            .map(this::createFactionButton)
            .forEach(buttons.getChildren()::add);
        return buttons;
    }
    /**
     * Executes the operation represented by createFactionButton.
     * @param faction the value used by the method
     * @return the value produced by the method
     */
    private Button createFactionButton(String faction) {
        Button button = new Button();
        Card factionCard = allCards.stream()
            .filter(card -> faction.equals(card.getFaction()))
            .findFirst()
            .orElse(null);
        if (factionCard != null) {
            //button.setGraphic(createCardImage(factionCard, 48, 68, false));
        }
        button.setFocusTraversable(false);
        button.getStyleClass().add("collection-faction-button");
        button.setAccessibleText(faction);
        button.setOnAction(event -> toggleCollectionFilter(faction, button));
        return button;
    }
    /**
     * Executes the operation represented by createTypeFilters.
     * @return the value produced by the method
     */
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
    /**
     * Executes the operation represented by createPagination.
     * @return the value produced by the method
     */
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
    /**
     * Executes the operation represented by toggleCollectionFilter.
     * @param collection the value used by the method
     * @param filter the value used by the method
     */
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
    /**
     * Executes the operation represented by selectCollection.
     * @param collection the value used by the method
     */
    private void selectCollection(String collection) {
        hidePreview();
        activeCollection = collection;
        refreshCards();
    }
    /**
     * Executes the operation represented by refreshCards.
     */
    private void refreshCards() {
        activeCards = allCards.stream()
            .filter(this::matchesActiveCollection)
            .filter(this::matchesSearch)
            .toList();
        currentPage = 0;
        hidePreview();
        animatePage(1);
    }
    /**
     * Executes the operation represented by matchesActiveCollection.
     * @param card the value used by the method
     * @return the value produced by the method
     */
    private boolean matchesActiveCollection(Card card) {
        return activeCollection == null
            || typeName(card).equals(activeCollection)
            || activeCollection.equals(card.getFaction());
    }
    /**
     * Executes the operation represented by matchesSearch.
     * @param card the value used by the method
     * @return the value produced by the method
     */
    private boolean matchesSearch(Card card) {
        if (searchTerm.isBlank()) {
            return true;
        }
        return card.getName() != null
            && card.getName().toLowerCase(Locale.ROOT)
                .contains(searchTerm.toLowerCase(Locale.ROOT));
    }
    /**
     * Executes the operation represented by changePage.
     * @param direction the value used by the method
     */
    private void changePage(int direction) {
        hidePreview();
        int pageCount = (int) Math.ceil((double) activeCards.size() / CARDS_PER_PAGE);
        if (pageAnimating || currentPage + direction < 0 || currentPage + direction >= pageCount) return;
        currentPage += direction;
        animatePage(direction);
    }
    /**
     * Displays the information handled by renderCards.
     * @param animate the value used by the method
     * @param direction the value used by the method
     */
    private void renderCards(boolean animate, int direction) {
        cardGrid.getChildren().clear();
        cardGrid.setAlignment(Pos.CENTER);
        cardGrid.setHgap(0);
        cardGrid.setVgap(0);
        cardGrid.getStyleClass().add("collection-card-grid");

        List<Card> cards = activeCards;
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
    /**
     * Executes the operation represented by calculateCardWidth.
     * @return the value produced by the method
     */
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
    /**
     * Executes the operation represented by calculateColumnCount.
     * @return the value produced by the method
     */
    private int calculateColumnCount() {
        return 5;
    }
    /**
     * Executes the operation represented by resizeCards.
     */
    private void resizeCards() {
        if (!cardGrid.getChildren().isEmpty()) resizeCardImages();
    }
    /**
     * Executes the operation represented by resizeCardImages.
     */
    private void resizeCardImages() {
        double cardWidth = calculateCardWidth();
        double cardHeight = cardWidth * 1.46;
        cardGrid.lookupAll(".collection-card-slot").stream()
            .map(StackPane.class::cast)
            .forEach(slot -> {
                slot.setMinSize(cardWidth + 12, cardHeight + 12);
                slot.setPrefSize(cardWidth + 12, cardHeight + 12);
                slot.lookupAll(".collection-card-visual").stream()
                    .map(CardVisual.class::cast)
                    .forEach(card -> card.updateSize(cardWidth, cardHeight));
            });
        cardGrid.requestLayout();
    }
    /**
     * Executes the operation represented by animatePage.
     * @param direction the value used by the method
     */
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
    /**
     * Executes the operation represented by animateGrid.
     * @param direction the value used by the method
     */
    private void animateGrid(int direction) {
        cardGrid.setTranslateX(direction * 70);
        cardGrid.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(220), cardGrid);
        fadeIn.setToValue(1);
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(220), cardGrid);
        slideIn.setToX(0);
        new ParallelTransition(fadeIn, slideIn).play();
    }
    /**
     * Executes the operation represented by updatePageButtons.
     */
    private void updatePageButtons() {
        if (previousPageButton == null || nextPageButton == null) return;
        int pageCount = (int) Math.ceil((double) activeCards.size() / CARDS_PER_PAGE);
        previousPageButton.setDisable(currentPage == 0);
        nextPageButton.setDisable(currentPage >= pageCount - 1);
    }
    /**
     * Executes the operation represented by typeName.
     * @param card the value used by the method
     * @return the value produced by the method
     */
    private String typeName(Card card) {
        return card.getClass().getSimpleName().toUpperCase();
    }
    /**
     * Executes the operation represented by createCardImage.
     * @param card the value used by the method
     * @param width the value used by the method
     * @param height the value used by the method
     * @return the value produced by the method
     */
    private CardVisual createCardImage(Card card, double width, double height) {
        return createCardImage(card, width, height, true);
    }
    /**
     * Executes the operation represented by createCardSlot.
     * @param card the value used by the method
     * @param width the value used by the method
     * @param height the value used by the method
     * @return the value produced by the method
     */
    private StackPane createCardSlot(Card card, double width, double height) {
        CardVisual image = createCardImage(card, width, height, false);
        StackPane slot = new StackPane(image);
        slot.getStyleClass().add("collection-card-slot");
        slot.setPadding(new Insets(6));
        slot.setMinSize(width + 12, height + 12);
        slot.setPrefSize(width + 12, height + 12);
        configureCardHover(slot, card);
        return slot;
    }

    private CardVisual createCardImage(Card card, double width, double height,
                                      boolean interactive) {
        CardVisual visual = new CardVisual(card, width, height, "collection-card-image");
        visual.getStyleClass().add("collection-card-visual");
        visual.addMetadata(card);
        if (interactive) configureCardHover(visual, card);
        return visual;
    }
    /**
     * Executes the operation represented by configureCardHover.
     * @param card the value used by the method
     * @param cardData the value used by the method
     */
    private void configureCardHover(Node card, Card cardData) {
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
            if (hovering[0]) showPreview(cardData, card);
        });
    }
    /**
     * Displays the information handled by showPreview.
     * @param card the value used by the method
     * @param reference the value used by the method
     */
    private void showPreview(Card card, Node reference) {
        hidePreview();
        previewLocked = false;
        previewOverlay = new StackPane();
        previewOverlay.getStyleClass().add("collection-preview-overlay");
        previewOverlay.setPickOnBounds(false);
        previewOverlay.setAlignment(Pos.TOP_LEFT);
        Region backdrop = new Region();
        backdrop.getStyleClass().add("collection-preview-backdrop");
        backdrop.setMouseTransparent(true);
        CardVisual preview = createCardImage(card, 600, 860, false);
        mainView.getRoot().heightProperty().addListener((observable, oldValue, newValue) -> {
            double previewHeight = Math.min(860, newValue.doubleValue() * 0.86);
            preview.updateSize(CardVisual.widthForHeight(previewHeight), previewHeight);
        });
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
        preview.setManaged(false);
        mainView.getRoot().getChildren().add(previewOverlay);
        positionPreview(reference, preview);
    }
    /**
     * Executes the operation represented by positionPreview.
     * @param reference the value used by the method
     * @param preview the value used by the method
     */
    private void positionPreview(Node reference, CardVisual preview) {
        javafx.geometry.Bounds sceneBounds = reference.localToScene(reference.getBoundsInLocal());
        javafx.geometry.Point2D rootPoint = mainView.getRoot().sceneToLocal(
            sceneBounds.getMaxX() + 16, sceneBounds.getMinY());
        double maxX = Math.max(0, mainView.getRoot().getWidth() - preview.getPrefWidth() - 16);
        double maxY = Math.max(0, mainView.getRoot().getHeight() - preview.getPrefHeight() - 16);
        preview.relocate(Math.min(rootPoint.getX(), maxX), Math.min(rootPoint.getY(), maxY));
    }
    /**
     * Executes the operation represented by hidePreview.
     */
    private void hidePreview() {
        previewHideDelay.stop();
        if (previewOverlay != null) {
            mainView.getRoot().getChildren().remove(previewOverlay);
            previewOverlay = null;
        }
        previewLocked = false;
    }
    /**
     * Executes the operation represented by schedulePreviewHide.
     */
    private void schedulePreviewHide() {
        if (previewLocked) return;
        previewHideDelay.setOnFinished(event -> hidePreview());
        previewHideDelay.playFromStart();
    }
}
