package com.fiuba.paradigwent.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.fiuba.paradigwent.view.menu.*;
import com.fiuba.paradigwent.view.menu.modules.CollectionMenuView;
import com.fiuba.paradigwent.view.menu.modules.ConfigMenuView;
import com.fiuba.paradigwent.view.menu.modules.FactionsMenuView;
import com.fiuba.paradigwent.view.menu.modules.InGameConfigMenuView;
import com.fiuba.paradigwent.view.game.GameView;

/**
 * @brief Application entry point and coordinator for the menu and game views.
 */
public class MainView extends Application {

    private static final Map<String, String> ASSETS = Map.of(
        "CSS", "/css/styles.css",
        "FONT", "/fonts/Headstone.ttf",
        "TITLE_IMAGE", "/images/title.png",
        "BACKGROUND_VIDEO", "/videos/main_menu_background.mp4"
    );
    private static final Map<String, Double> UI_DIMENSIONS = Map.of(
        "TEXT_SIZE", 15.0,
        "TITLE_PADDING_TOP", 40.0,
        "TITLE_PADDING_LEFT", 60.0,
        "TITLE_WIDTH", 800.0
    );
    private static final Map<String, Double> LIGHTING = Map.of(
        "AZIMUTH", 45.0,
        "ELEVATION", 60.0,
        "SURFACE_SCALE", 4.5
    );
    private static final List<Pair<Integer, Integer>> SUPPORTED_RESOLUTIONS = List.of(
        new Pair<>(1920, 1080),
        new Pair<>(1600, 900),
        new Pair<>(1280, 720),
        new Pair<>(1366, 768)
    );
    private StackPane root = new StackPane();
    private BorderPane uiOverlay = new BorderPane();
    private String fontFamilyName = "System";
    private Font customFont;
    private MainMenuView mainMenuView;
    private ConfigMenuView configMenuView;
    private FactionsMenuView factionsMenuView;
    private CollectionMenuView collectionMenuView;
    private InGameConfigMenuView inGameConfigMenuView;
    private GameView gameView;
    private Pair<Integer, Integer> currentResolution = SUPPORTED_RESOLUTIONS.get(0);

    /**
     * @brief Initializes the views, scene, stylesheet, and primary stage.
     * @param primaryStage Stage that hosts the application scene.
     */
    @Override
    public void start(Stage primaryStage) {
        setup(primaryStage);
        URL cssResource = getClass().getResource(ASSETS.get("CSS"));

        this.mainMenuView = new MainMenuView(this, primaryStage);
        this.configMenuView = new ConfigMenuView(this, primaryStage, SUPPORTED_RESOLUTIONS);
        this.factionsMenuView = new FactionsMenuView(this);
        this.collectionMenuView = new CollectionMenuView(this);
        this.inGameConfigMenuView = new InGameConfigMenuView(this, primaryStage, SUPPORTED_RESOLUTIONS);
        this.gameView = new GameView(this);

        showMainMenu();
        root.getChildren().add(uiOverlay);
        root.widthProperty().addListener((observable, oldValue, newValue) -> adaptMenuLayout());
        root.heightProperty().addListener((observable, oldValue, newValue) -> adaptMenuLayout());
        Scene scene = new Scene(root, SUPPORTED_RESOLUTIONS.get(0).getKey(), SUPPORTED_RESOLUTIONS.get(0).getValue());
        scene.getStylesheets().add(cssResource.toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
        Platform.runLater(() -> {
            gameView.centerBoardInViewport();
        });
    }

    /** 
    * @brief Loads shared assets and configures the primary stage.
     * @param primaryStage The primary stage of the application.
    **/
    private void setup(Stage primaryStage){
        loadCustomFont();
        setupBackgroundVideo();
        primaryStage.setTitle("Paradigwent");
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
    }

    /** 
    * @brief Replaces the current menu content and restores the title area.
    * @param node Menu node displayed on the right side of the overlay.
    **/
    public void setContent(Node node) {
        root.getChildren().remove(inGameConfigMenuView);
        VBox topLeftContainer = new VBox();
        ImageView titleView = createTitleView();
        titleView.fitWidthProperty().bind(Bindings.min(500, root.widthProperty().multiply(0.32)));
        topLeftContainer.setPadding(new Insets(
            UI_DIMENSIONS.get("TITLE_PADDING_TOP"), 0, 0,
            UI_DIMENSIONS.get("TITLE_PADDING_LEFT")));
        topLeftContainer.getChildren().add(titleView);
        uiOverlay.setCenter(null);
        uiOverlay.setLeft(topLeftContainer);
        uiOverlay.setRight(node);
        adaptMenuLayout();
    }

    /**
     * @brief Shows the game board and removes menu regions from the overlay.
     */
    public void showGameView()       {
        root.getChildren().remove(inGameConfigMenuView);
        uiOverlay.setLeft(null);
        uiOverlay.setRight(null);
        uiOverlay.setCenter(gameView.getRootNode());
    }
    /** @brief Shows the main menu. */
    public void showMainMenu()       { setContent(mainMenuView);       }
    /** @brief Shows the configuration menu. */
    public void showConfigMenu()     {
        configMenuView.refreshResolution();
        setContent(configMenuView);
    }
    /** @brief Shows the factions menu. */
    public void showFactionsMenu()   { setContent(factionsMenuView);   }
    /** @brief Shows the collection menu. */
    public void showCollectionMenu() {
        root.getChildren().remove(inGameConfigMenuView);
        uiOverlay.setLeft(null);
        uiOverlay.setRight(null);
        uiOverlay.setCenter(collectionMenuView);
    }
    /** @brief Shows the in-game configuration menu. */
    public void showInGameConfigMenu() {
        inGameConfigMenuView.refreshResolution();
        uiOverlay.setLeft(null);
        uiOverlay.setCenter(gameView.getRootNode());
        uiOverlay.setRight(null);
        if (!root.getChildren().contains(inGameConfigMenuView)) {
            root.getChildren().add(inGameConfigMenuView);
        }
        StackPane.setAlignment(inGameConfigMenuView, Pos.CENTER_RIGHT);
    }
    
    /**
    * @brief Returns the root node that contains the background and overlay.
    * @return Root StackPane of the application.
    **/
    public StackPane getRoot() { return root; }

    /** 
    * @brief Returns the loaded custom font.
    * @return Custom font, or null when the asset could not be loaded.
    **/
    public Font getCustomFont() { return customFont; }

    public void applyResolution(Stage stage, int width, int height) {
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        currentResolution = new Pair<>(width, height);

        stage.setFullScreen(false);
        if (width > visualBounds.getWidth() || height > visualBounds.getHeight()) {
            stage.setMaximized(true);
            return;
        }

        stage.setMaximized(false);
        stage.setWidth(width);
        stage.setHeight(height);
        stage.centerOnScreen();
    }

    public Pair<Integer, Integer> getCurrentResolution() {
        return currentResolution;
    }

    /** 
    * @brief Returns the active font family name.
    * @return Font family name used by the application.
    **/
    public String getFontFamilyName() { return fontFamilyName; }

    /** 
    * @brief Creates a button styled for the application menus.
    * @param text Text displayed by the button.
    * @return New button with the menu style class applied.
    **/
    public Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("menu-button");
        return btn;
    }

    /** 
    * @brief Loads the configured custom font and updates the active family name.
    **/
    private void loadCustomFont() {
        try (InputStream fontStream = getClass().getResourceAsStream(ASSETS.get("FONT"))) {
            if (fontStream != null) {
                this.customFont = Font.loadFont(fontStream, UI_DIMENSIONS.get("TEXT_SIZE"));
                this.fontFamilyName = this.customFont.getFamily();
            }
        } catch (Exception e) {
            System.err.println("❌ Error loading asset: " + ASSETS.get("FONT") + " - " + e.getMessage());
        }
    }

    /** 
    * @brief Loads and loops the main menu background video.
    * If the video is unavailable, applies a solid fallback background.
    **/
    private void setupBackgroundVideo() {
        try {
            URL videoResource = getClass().getResource(ASSETS.get("BACKGROUND_VIDEO"));
            if (videoResource != null) {
                Media media = new Media(videoResource.toExternalForm());
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                mediaPlayer.setAutoPlay(true);

                MediaView mediaView = new MediaView(mediaPlayer);
                mediaView.setPreserveRatio(false);
                mediaView.fitWidthProperty().bind(root.widthProperty());
                mediaView.fitHeightProperty().bind(root.heightProperty());

                root.getChildren().add(mediaView);
            }
        } catch (Exception e) {
            System.err.println("❌ Error loading asset: " + ASSETS.get("BACKGROUND_VIDEO") + " - " + e.getMessage());
            root.setStyle("-fx-background-color: #0f0f14;");
        }
    }

    /** 
    * @brief Creates the title image view and applies its lighting effect.
    * @return ImageView with the title asset, or null when the asset cannot be loaded.
    **/
    private ImageView createTitleView() {
        try {
            URL titleResource = getClass().getResource(ASSETS.get("TITLE_IMAGE"));
            if (titleResource != null) {
                Image titleImg = new Image(titleResource.toExternalForm());
                ImageView titleView = new ImageView(titleImg);
                titleView.setPreserveRatio(true);
                titleView.setFitWidth(UI_DIMENSIONS.get("TITLE_WIDTH"));

                Lighting lighting = new Lighting();
                Light.Distant light = new Light.Distant();
                light.setAzimuth(LIGHTING.get("AZIMUTH"));
                light.setElevation(LIGHTING.get("ELEVATION"));
                light.setColor(Color.WHITE);

                lighting.setLight(light);
                lighting.setSurfaceScale(LIGHTING.get("SURFACE_SCALE"));
                titleView.setEffect(lighting);

                return titleView;
            }
        } catch (Exception e) {
            System.err.println("❌ Error loading asset: " + ASSETS.get("TITLE_IMAGE") + " - " + e.getMessage());
        }
        return null;
    }

    private void adaptMenuLayout() {
        double width = uiOverlay.getWidth();
        if (width <= 0) return;

        double componentWidth = Math.min(360, Math.max(180, width * 0.22));
        adaptComponentWidths(uiOverlay, componentWidth);
    }

    private void adaptComponentWidths(Node node, double width) {
        node.lookupAll(".menu-button").stream()
            .filter(control -> !control.getStyleClass().contains("menu-button-dialog"))
            .map(Button.class::cast)
            .forEach(button -> {
                button.setMinWidth(140);
                button.setPrefWidth(width);
            });

        node.lookupAll(".menu-combo-box").stream()
            .map(ComboBox.class::cast)
            .forEach(comboBox -> {
                comboBox.setMinWidth(140);
                comboBox.setPrefWidth(width);
            });
    }

    /**
     * @brief Launches the JavaFX application.
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        launch(args);
    }
}