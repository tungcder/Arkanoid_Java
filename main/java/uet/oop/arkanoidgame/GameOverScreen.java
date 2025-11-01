package uet.oop.arkanoidgame;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import java.net.URL;

/**
 * Màn hình hiển thị khi thua cuộc
 */
public class GameOverScreen extends StackPane {
    // Constants
    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;
    private static final String RED_COLOR = "#FF4444";
    private static final String DARK_RED = "#FF0000";
    private static final String LIGHT_RED = "#FFB0B0";
    private static final String HOVER_RED_GRADIENT = "#FF8C8C, #FF6B6B";
    private static final String NORMAL_RED_GRADIENT = "#FF6B6B, #FF4444";

    // Background path
    private static final String BG_IMAGE_PATH =
            "/Images/Screen/GameOver.jpg";
    private static final String FONT_PATH = "/fonts/Orbitron-Bold.ttf";

    // Animation timings
    private static final double TITLE_FADE_DURATION = 1.0;
    private static final double TITLE_SCALE_DURATION = 0.5;
    private static final double SCORE_DELAY = 0.5;
    private static final double SCORE_DURATION = 0.8;
    private static final double MESSAGE_DELAY = 0.8;
    private static final double BUTTON_DELAY = 1.2;

    private final int finalScore;
    private final Runnable onBackToMenu;

    public GameOverScreen(int score, Runnable backToMenuAction) {
        this.finalScore = score;
        this.onBackToMenu = backToMenuAction;

        setPrefSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        setupUI();
    }

    private void setupUI() {
        setupBackground();
        VBox contentBox = createContentBox();
        getChildren().add(contentBox);
    }

    private void setupBackground() {
        addBackgroundImage();
        addOverlay();
    }

    private void addBackgroundImage() {
        URL bgUrl = getClass().getResource(BG_IMAGE_PATH);

        if (bgUrl != null) {
            ImageView bgImage = new ImageView(new Image(bgUrl.toExternalForm()));
            bgImage.setFitWidth(SCREEN_WIDTH);
            bgImage.setFitHeight(SCREEN_HEIGHT);
            bgImage.setPreserveRatio(false);
            getChildren().add(bgImage);
        } else {
            setFallbackBackground();
        }
    }

    private void setFallbackBackground() {
        setStyle("-fx-background-color: linear-gradient(to bottom, " +
                "rgba(20,10,10,0.95), rgba(50,10,10,0.95));");
    }

    private void addOverlay() {
        Rectangle overlay = new Rectangle(SCREEN_WIDTH, SCREEN_HEIGHT);
        overlay.setFill(Color.rgb(0, 0, 0, 0.5));
        getChildren().add(overlay);
    }

    private VBox createContentBox() {
        VBox contentBox = new VBox(30);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(50));

        Label gameOverLabel = createGameOverTitle();
        Rectangle topLine = createDecorativeLine();
        VBox scoreBox = createScoreBox();
        Label messageLabel = createMessage();
        Rectangle bottomLine = createDecorativeLine();
        Button backButton = createBackButton();

        contentBox.getChildren().addAll(
                gameOverLabel, topLine, scoreBox,
                messageLabel, bottomLine, backButton
        );

        playAnimations(gameOverLabel, scoreBox, messageLabel, backButton);
        return contentBox;
    }

    private Label createGameOverTitle() {
        Label label = new Label("PLAY AGAIN?");
        label.setFont(loadCustomFont(FONT_PATH, 56));
        label.setTextFill(Color.web(RED_COLOR));
        label.setEffect(createGlow(DARK_RED, 30, 0.8));
        return label;
    }

    private Rectangle createDecorativeLine() {
        Rectangle line = new Rectangle(400, 3);
        line.setFill(Color.web(RED_COLOR, 0.7));
        line.setArcWidth(3);
        line.setArcHeight(3);
        line.setEffect(createGlow(DARK_RED, 10, 0.6));
        return line;
    }

    private VBox createScoreBox() {
        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(25, 50, 25, 50));
        box.setStyle(createScoreBoxStyle());

        Label titleLabel = createScoreTitle();
        Label scoreValue = createScoreValue();

        box.getChildren().addAll(titleLabel, scoreValue);
        return box;
    }

    private String createScoreBoxStyle() {
        return "-fx-background-color: linear-gradient(to bottom, " +
                "rgba(255,68,68,0.15), rgba(255,0,0,0.15)); " +
                "-fx-background-radius: 15; " +
                "-fx-border-color: rgba(255,68,68,0.5); " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 15;";
    }

    private Label createScoreTitle() {
        Label label = new Label("FINAL SCORE");
        label.setFont(Font.font("System", FontWeight.BOLD, 18));
        label.setTextFill(Color.web(LIGHT_RED, 0.9));
        return label;
    }

    private Label createScoreValue() {
        Label label = new Label(String.valueOf(finalScore));
        label.setFont(Font.font("System", FontWeight.BOLD, 48));
        label.setTextFill(Color.web(RED_COLOR));
        label.setEffect(createGlow(DARK_RED, 15, 0.7));
        return label;
    }

    private Label createMessage() {
        Label label = new Label("💔 Better luck next time! 💔");
        label.setFont(Font.font("System", FontWeight.BOLD, 20));
        label.setTextFill(Color.web("#FFFFFF", 0.8));
        return label;
    }

    private Button createBackButton() {
        Button button = new Button("⬅  BACK TO MENU");
        button.setFont(Font.font("System", FontWeight.BOLD, 20));
        button.setPrefWidth(280);
        button.setPrefHeight(60);

        applyButtonStyle(button, false);
        button.setEffect(createGlow(DARK_RED, 15, 0.6));

        setupButtonInteractions(button);

        return button;
    }

    private void setupButtonInteractions(Button button) {
        button.setOnMouseEntered(e -> applyButtonStyle(button, true));
        button.setOnMouseExited(e -> applyButtonStyle(button, false));
        button.setOnAction(e -> {
            if (onBackToMenu != null) onBackToMenu.run();
        });
    }

    private void applyButtonStyle(Button button, boolean isHovered) {
        String gradient = isHovered ? HOVER_RED_GRADIENT : NORMAL_RED_GRADIENT;
        String borderColor = isHovered ? RED_COLOR : DARK_RED;
        String scale = isHovered ? "-fx-scale-x: 1.05; -fx-scale-y: 1.05;" : "";

        button.setStyle(String.format(
                "-fx-background-color: linear-gradient(to bottom, %s); " +
                        "-fx-text-fill: #FFFFFF; " +
                        "-fx-background-radius: 15; " +
                        "-fx-border-color: %s; " +
                        "-fx-border-width: 3; " +
                        "-fx-border-radius: 15; " +
                        "-fx-cursor: hand; %s",
                gradient, borderColor, scale
        ));
    }

    private void playAnimations(Label title, VBox scoreBox, Label message, Button button) {
        playTitleAnimation(title);
        playScoreAnimation(scoreBox);
        playFadeAnimation(message, MESSAGE_DELAY, SCORE_DURATION);
        playFadeAnimation(button, BUTTON_DELAY, SCORE_DURATION);
    }

    private void playTitleAnimation(Label title) {
        FadeTransition fade = createFadeTransition(title, 0, TITLE_FADE_DURATION);
        ScaleTransition scale = createScaleTransition(title, 0, TITLE_SCALE_DURATION, 0.5);

        fade.play();
        scale.play();
    }

    private void playScoreAnimation(VBox scoreBox) {
        FadeTransition fade = createFadeTransition(scoreBox, SCORE_DELAY, SCORE_DURATION);
        ScaleTransition scale = createScaleTransition(scoreBox, SCORE_DELAY,
                TITLE_SCALE_DURATION, 0.8);

        fade.play();
        scale.play();
    }

    private void playFadeAnimation(javafx.scene.Node node, double delay, double duration) {
        FadeTransition fade = createFadeTransition(node, delay, duration);
        fade.play();
    }

    private FadeTransition createFadeTransition(javafx.scene.Node node,
                                                double delay, double duration) {
        FadeTransition fade = new FadeTransition(Duration.seconds(duration), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setDelay(Duration.seconds(delay));
        return fade;
    }

    private ScaleTransition createScaleTransition(javafx.scene.Node node, double delay,
                                                  double duration, double fromScale) {
        ScaleTransition scale = new ScaleTransition(Duration.seconds(duration), node);
        scale.setFromX(fromScale);
        scale.setFromY(fromScale);
        scale.setToX(1);
        scale.setToY(1);
        scale.setDelay(Duration.seconds(delay));
        return scale;
    }

    private Font loadCustomFont(String path, int size) {
        try {
            Font custom = Font.loadFont(getClass().getResourceAsStream(path), size);
            return custom != null ? custom : createFallbackFont(size);
        } catch (Exception e) {
            return createFallbackFont(size);
        }
    }

    private Font createFallbackFont(int size) {
        return Font.font("System", FontWeight.BOLD, size);
    }

    private DropShadow createGlow(String color, double radius, double opacity) {
        return new DropShadow(radius, Color.web(color, opacity));
    }
}