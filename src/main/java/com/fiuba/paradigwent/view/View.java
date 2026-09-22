package com.fiuba.paradigwent.view;

import javafx.application.Application;
import javafx.geometry.Insets;

import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class View extends Application {

    private static final String CSS_PATH = "/css/styles.css";
    private static final String FONT_PATH = "/fonts/Headstone.ttf";
    private static final String TITLE_IMAGE_PATH = "/images/title.png";
    private static final String BACKGROUND_VIDEO_PATH = "/videos/main_menu_background.mp4";
    private static final Integer TEXT_SIZE = 15;
    private static final List<Pair<Integer, Integer>> SUPPORTED_RESOLUTIONS = new ArrayList<>(Arrays.asList(
            new Pair<>(1920, 1080),
            new Pair<>(1600, 900),
            new Pair<>(1280, 720),
            new Pair<>(1366, 768)
    ));
    private StackPane root = new StackPane();
    private BorderPane uiOverlay = new BorderPane();
    private String fontFamilyName = "System";
    private Font customFont;
    private MainMenuView mainMenuView;
    private ConfigMenuView configMenuView;
    private FactionsMenuView factionsMenuView;
    private CollectionMenuView collectionMenuView;

    @Override
    public void start(Stage primaryStage) {
        setup(primaryStage);
        URL cssResource = getClass().getResource(CSS_PATH);

        this.mainMenuView = new MainMenuView(this, primaryStage);
        this.configMenuView = new ConfigMenuView(this, primaryStage, SUPPORTED_RESOLUTIONS);
        this.factionsMenuView = new FactionsMenuView(this);
        this.collectionMenuView = new CollectionMenuView(this);

        showMainMenu();
        root.getChildren().add(uiOverlay);
        
        Scene scene = new Scene(root, SUPPORTED_RESOLUTIONS.get(0).getKey(), SUPPORTED_RESOLUTIONS.get(0).getValue());
        scene.getStylesheets().add(cssResource.toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /** 
     * Sets up the initial configuration for the primary stage, including loading the custom font, setting up the background video, and configuring the stage properties.
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
     * Sets the content of the UI overlay to the specified node, positioning it on the right side of the overlay and adding a title image to the left side.
     * @param node The node to be displayed on the right side of the UI overlay.
    **/
    public void setContent(Node node) {
        VBox topLeftContainer = new VBox();
        ImageView titleView = createTitleView();
        topLeftContainer.setPadding(new Insets(40, 0, 0, 60));
        topLeftContainer.getChildren().add(titleView);
        uiOverlay.setLeft(topLeftContainer);
        uiOverlay.setRight(node);
    }

    // Series of methods to show the different menu views
    public void showMainMenu()       { setContent(mainMenuView);       }
    public void showConfigMenu()     { setContent(configMenuView);     }
    public void showFactionsMenu()   { setContent(factionsMenuView);   }
    public void showCollectionMenu() { setContent(collectionMenuView); }
    
    /**
    * Getter methods for root
    * @return The root StackPane
    **/
    public StackPane getRoot() { return root; }

    /** 
     * Getter methods for custom font
     * @return The custom font object
    **/
    public Font getCustomFont() { return customFont; }

    /** 
     * Getter methods for font family name
     * @return The font family name as a String
    **/
    public String getFontFamilyName() { return fontFamilyName; }

    /** 
     * Creates a menu button with the specified text and applies the "menu-button" style class to it.
     * @param text The text to be displayed on the button.
     * @return A Button object with the specified text and style.
    **/
    public Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("menu-button");
        return btn;
    }

    /** 
     * Loads a custom font from the specified FONT_PATH and sets it as the customFont and fontFamilyName properties. If the font cannot be loaded, an error message is printed to the console.
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
     * Sets up the background video for the main menu. If the video cannot be loaded, a fallback background color is applied to the root StackPane.
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
     * Creates an ImageView for the title image, applies a lighting effect to it, and returns the ImageView. If the title image cannot be loaded, null is returned.
     * @return An ImageView object containing the title image with lighting effect, or null if the image cannot be loaded.
    **/
    private ImageView createTitleView() {
        try {
            URL titleResource = getClass().getResource(TITLE_IMAGE_PATH);
            if (titleResource != null) {
                Image titleImg = new Image(titleResource.toExternalForm());
                ImageView titleView = new ImageView(titleImg);
                titleView.setPreserveRatio(true);
                titleView.setFitWidth(800);

                Lighting lighting = new Lighting();
                Light.Distant light = new Light.Distant();
                light.setAzimuth(45.0);
                light.setElevation(60.0);
                light.setColor(Color.WHITE);

                lighting.setLight(light);
                lighting.setSurfaceScale(4.5);
                titleView.setEffect(lighting);

                return titleView;
            }
        } catch (Exception e) {
            System.err.println("❌ Error loading asset: " + TITLE_IMAGE_PATH + " - " + e.getMessage());
        }
        return null;
    }

    public static void main(String[] args) {
        launch(args);
    }
}