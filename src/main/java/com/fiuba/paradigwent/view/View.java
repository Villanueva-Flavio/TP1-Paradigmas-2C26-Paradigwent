package com.fiuba.paradigwent.view;

import javafx.application.Application;
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
    public void start(Stage primaryStage) {}

    /**
     * @brief Launches the JavaFX application.
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        launch(args);
    }
}