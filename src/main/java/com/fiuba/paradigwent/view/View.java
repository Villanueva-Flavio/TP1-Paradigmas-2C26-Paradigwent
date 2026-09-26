package com.fiuba.paradigwent.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Button;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fiuba.paradigwent.view.menu.*;
import com.fiuba.paradigwent.view.game.GameView;

/**
 * @brief Application entry point and coordinator for the menu and game views.
 */
public class View extends Application {

    private static final String CSS_PATH = "/css/styles.css";
    private static final String FONT_PATH = "/fonts/Headstone.ttf";
    private static final String TITLE_IMAGE_PATH = "/images/title.png";
    private static final String BACKGROUND_VIDEO_PATH = "/videos/main_menu_background.mp4";
    private static final Integer TEXT_SIZE = 15;
    private static final double TITLE_PADDING_TOP = 40;          // Esto habrá que pasarlo a diccionarios de cada categoria. eg: CAMERA["Azimuth"], CAMERA["Elevation"], etc.
    private static final double TITLE_PADDING_LEFT = 60;
    private static final double TITLE_WIDTH = 800;
    private static final double LIGHT_AZIMUTH = 45.0;
    private static final double LIGHT_ELEVATION = 60.0;
    private static final double TITLE_SURFACE_SCALE = 4.5;
    private static final int DEFAULT_RESOLUTION_WIDTH = 1920;
    private static final int DEFAULT_RESOLUTION_HEIGHT = 1080;
    private static final int CONFIG_RESOLUTION_WIDTH = 1600;
    private static final int CONFIG_RESOLUTION_HEIGHT = 900;
    private static final int HD_RESOLUTION_WIDTH = 1280;
    private static final int HD_RESOLUTION_HEIGHT = 720;
    private static final int LAPTOP_RESOLUTION_WIDTH = 1366;
    private static final int LAPTOP_RESOLUTION_HEIGHT = 768;
    private static final List<Pair<Integer, Integer>> SUPPORTED_RESOLUTIONS = new ArrayList<>(Arrays.asList(
            new Pair<>(DEFAULT_RESOLUTION_WIDTH, DEFAULT_RESOLUTION_HEIGHT),
            new Pair<>(CONFIG_RESOLUTION_WIDTH, CONFIG_RESOLUTION_HEIGHT),
            new Pair<>(HD_RESOLUTION_WIDTH, HD_RESOLUTION_HEIGHT),
            new Pair<>(LAPTOP_RESOLUTION_WIDTH, LAPTOP_RESOLUTION_HEIGHT)
    ));
    private StackPane root = new StackPane();
    private BorderPane uiOverlay = new BorderPane();
    private Pane globalDimmer = new Pane();
    private String fontFamilyName = "System";
    private Font customFont;
    private MainMenuView mainMenuView;
    private ConfigMenuView configMenuView;
    private FactionsMenuView factionsMenuView;
    private CollectionMenuView collectionMenuView;
    private InGameConfigMenuView inGameConfigMenuView;
    private GameView gameView;

    /**
     * @brief Initializes the views, scene, stylesheet, and primary stage.
     * @param primaryStage Stage that hosts the application scene.
     */
    @Override
    public void start(Stage primaryStage) {
        setup(primaryStage);
        URL cssResource = getClass().getResource(CSS_PATH);

        this.mainMenuView = new MainMenuView(this, primaryStage);
        this.configMenuView = new ConfigMenuView(this, primaryStage, SUPPORTED_RESOLUTIONS);
        this.factionsMenuView = new FactionsMenuView(this);
        this.collectionMenuView = new CollectionMenuView(this);
        this.inGameConfigMenuView = new InGameConfigMenuView(this, primaryStage, SUPPORTED_RESOLUTIONS);
        this.gameView = new GameView(this);

        showMainMenu();
        root.getChildren().add(uiOverlay);
        globalDimmer.getStyleClass().add("global-dimmer");
        globalDimmer.setVisible(false);
        root.getChildren().add(globalDimmer);
        
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
        globalDimmer.setVisible(false);
        VBox topLeftContainer = new VBox();
        ImageView titleView = createTitleView();
        topLeftContainer.setPadding(new Insets(TITLE_PADDING_TOP, 0, 0, TITLE_PADDING_LEFT));
        topLeftContainer.getChildren().add(titleView);
        uiOverlay.setCenter(null);
        uiOverlay.setLeft(topLeftContainer);
        uiOverlay.setRight(node);
    }

    /**
     * @brief Shows the game board and removes menu regions from the overlay.
     */
    public void showGameView()       {
        root.getChildren().remove(inGameConfigMenuView);
        globalDimmer.setVisible(false);
        uiOverlay.setLeft(null);
        uiOverlay.setRight(null);
        uiOverlay.setCenter(gameView.getRootNode());
        gameView.setDimmed(false);
    }
    /** @brief Shows the main menu. */
    public void showMainMenu()       { setContent(mainMenuView);       }
    /** @brief Shows the configuration menu. */
    public void showConfigMenu()     { setContent(configMenuView);     }
    /** @brief Shows the factions menu. */
    public void showFactionsMenu()   { setContent(factionsMenuView);   }
    /** @brief Shows the collection menu. */
    public void showCollectionMenu() { setContent(collectionMenuView); }
    /** @brief Shows the in-game configuration menu. */
    public void showInGameConfigMenu() {
        uiOverlay.setLeft(null);
        uiOverlay.setCenter(gameView.getRootNode());
        uiOverlay.setRight(null);
        gameView.setDimmed(false);
        globalDimmer.setVisible(true);

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
        try (InputStream fontStream = getClass().getResourceAsStream(FONT_PATH)) {
            if (fontStream != null) {
                this.customFont = Font.loadFont(fontStream, TEXT_SIZE);
                this.fontFamilyName = this.customFont.getFamily();
            }
        } catch (Exception e) {
            System.err.println("❌ Error loading asset: " + FONT_PATH + " - " + e.getMessage());
        }
    }

    /** 
    * @brief Loads and loops the main menu background video.
    * If the video is unavailable, applies a solid fallback background.
    **/
    private void setupBackgroundVideo() {
        try {
            URL videoResource = getClass().getResource(BACKGROUND_VIDEO_PATH);
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
            System.err.println("❌ Error loading asset: " + BACKGROUND_VIDEO_PATH + " - " + e.getMessage());
            root.setStyle("-fx-background-color: #0f0f14;");
        }
    }

    /** 
    * @brief Creates the title image view and applies its lighting effect.
    * @return ImageView with the title asset, or null when the asset cannot be loaded.
    **/
    private ImageView createTitleView() {
        try {
            URL titleResource = getClass().getResource(TITLE_IMAGE_PATH);
            if (titleResource != null) {
                Image titleImg = new Image(titleResource.toExternalForm());
                ImageView titleView = new ImageView(titleImg);
                titleView.setPreserveRatio(true);
                titleView.setFitWidth(TITLE_WIDTH);

                Lighting lighting = new Lighting();
                Light.Distant light = new Light.Distant();
                light.setAzimuth(LIGHT_AZIMUTH);
                light.setElevation(LIGHT_ELEVATION);
                light.setColor(Color.WHITE);

                lighting.setLight(light);
                lighting.setSurfaceScale(TITLE_SURFACE_SCALE);
                titleView.setEffect(lighting);

                return titleView;
            }
        } catch (Exception e) {
            System.err.println("❌ Error loading asset: " + TITLE_IMAGE_PATH + " - " + e.getMessage());
        }
        return null;
    }

    /**
     * @brief Launches the JavaFX application.
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        launch(args);
    }
}