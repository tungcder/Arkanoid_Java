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
 * Class hiển thị màn hình thua cuộc
 */
public class GameOverScreen extends StackPane {

    private final int finalScore;
    private final Runnable onBackToMenu;

    public GameOverScreen(int score, Runnable backToMenuAction) {
        this.finalScore = score;
        this.onBackToMenu = backToMenuAction;

        setPrefSize(800, 600);
        setupUI();
    }

    private void setupUI() {
        // Background
        setupBackground();

        // Main content
        VBox contentBox = new VBox(30);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(50));

        // Game Over title
        Label gameOverLabel = createGameOverTitle();

        // Decorative line
        Rectangle topLine = createDecorativeLine();

        // Score display
        VBox scoreBox = createScoreBox();

        // Message
        Label messageLabel = createMessage();

        // Decorative line
        Rectangle bottomLine = createDecorativeLine();

        // Buttons
        Button backButton = createBackButton();

        // Add all
        contentBox.getChildren().addAll(
                gameOverLabel,
                topLine,
                scoreBox,
                messageLabel,
                bottomLine,
                backButton
        );

        getChildren().add(contentBox);

        // Animations
        playAnimations(gameOverLabel, scoreBox, messageLabel, backButton);
    }

    private void setupBackground() {
        URL bgUrl = getClass().getResource(
                "/uet/oop/arkanoidgame/entities/menu/menu_images/game_over.png"
        );

        if (bgUrl != null) {
            ImageView bgImage = new ImageView(new Image(bgUrl.toExternalForm()));
            bgImage.setFitWidth(800);
            bgImage.setFitHeight(600);
            bgImage.setPreserveRatio(false);
            getChildren().add(bgImage);
        } else {
            setStyle(
                    "-fx-background-color: linear-gradient(to bottom, " +
                            "rgba(20,10,10,0.95), rgba(50,10,10,0.95));"
            );
        }

        Rectangle overlay = new Rectangle(800, 600);
        overlay.setFill(Color.rgb(0, 0, 0, 0.5));
        getChildren().add(overlay);
    }

    private Label createGameOverTitle() {
        Label label = new Label("GAME OVER");

        Font customFont;
        try {
            customFont = Font.loadFont(
                    getClass().getResourceAsStream("/fonts/Orbitron-Bold.ttf"),
                    56
            );
            if (customFont == null) {
                customFont = Font.font("System", FontWeight.BOLD, 56);
            }
        } catch (Exception e) {
            customFont = Font.font("System", FontWeight.BOLD, 56);
        }

        label.setFont(customFont);
        label.setTextFill(Color.web("#FF4444"));

        DropShadow glow = new DropShadow();
        glow.setRadius(30);
        glow.setColor(Color.web("#FF0000", 0.8));
        label.setEffect(glow);

        return label;
    }

    private Rectangle createDecorativeLine() {
        Rectangle line = new Rectangle(400, 3);
        line.setFill(Color.web("#FF4444", 0.7));
        line.setArcWidth(3);
        line.setArcHeight(3);

        DropShadow glow = new DropShadow();
        glow.setRadius(10);
        glow.setColor(Color.web("#FF0000", 0.6));
        line.setEffect(glow);

        return line;
    }

    private VBox createScoreBox() {
        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(25, 50, 25, 50));
        box.setStyle(
                "-fx-background-color: linear-gradient(to bottom, " +
                        "rgba(255,68,68,0.15), rgba(255,0,0,0.15)); " +
                        "-fx-background-radius: 15; " +
                        "-fx-border-color: rgba(255,68,68,0.5); " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 15;"
        );

        Label titleLabel = new Label("FINAL SCORE");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.web("#FFB0B0", 0.9));

        Label scoreValue = new Label(String.valueOf(finalScore));
        scoreValue.setFont(Font.font("System", FontWeight.BOLD, 48));
        scoreValue.setTextFill(Color.web("#FF4444"));

        DropShadow scoreGlow = new DropShadow();
        scoreGlow.setRadius(15);
        scoreGlow.setColor(Color.web("#FF0000", 0.7));
        scoreValue.setEffect(scoreGlow);

        box.getChildren().addAll(titleLabel, scoreValue);
        return box;
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
        button.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #FF6B6B, #FF4444); " +
                        "-fx-text-fill: #FFFFFF; " +
                        "-fx-background-radius: 15; " +
                        "-fx-border-color: #FF0000; " +
                        "-fx-border-width: 3; " +
                        "-fx-border-radius: 15; " +
                        "-fx-cursor: hand;"
        );

        DropShadow buttonGlow = new DropShadow();
        buttonGlow.setRadius(15);
        buttonGlow.setColor(Color.web("#FF0000", 0.6));
        button.setEffect(buttonGlow);

        button.setOnMouseEntered(e -> {
            button.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #FF8C8C, #FF6B6B); " +
                            "-fx-text-fill: #FFFFFF; " +
                            "-fx-background-radius: 15; " +
                            "-fx-border-color: #FF4444; " +
                            "-fx-border-width: 3; " +
                            "-fx-border-radius: 15; " +
                            "-fx-cursor: hand; " +
                            "-fx-scale-x: 1.05; " +
                            "-fx-scale-y: 1.05;"
            );
        });

        button.setOnMouseExited(e -> {
            button.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #FF6B6B, #FF4444); " +
                            "-fx-text-fill: #FFFFFF; " +
                            "-fx-background-radius: 15; " +
                            "-fx-border-color: #FF0000; " +
                            "-fx-border-width: 3; " +
                            "-fx-border-radius: 15; " +
                            "-fx-cursor: hand;"
            );
        });

        button.setOnAction(e -> {
            if (onBackToMenu != null) {
                onBackToMenu.run();
            }
        });

        return button;
    }

    private void playAnimations(Label title, VBox scoreBox, Label message, Button button) {
        FadeTransition titleFade = new FadeTransition(Duration.seconds(1), title);
        titleFade.setFromValue(0);
        titleFade.setToValue(1);

        ScaleTransition titleScale = new ScaleTransition(Duration.seconds(0.5), title);
        titleScale.setFromX(0.5);
        titleScale.setFromY(0.5);
        titleScale.setToX(1);
        titleScale.setToY(1);

        FadeTransition scoreFade = new FadeTransition(Duration.seconds(0.8), scoreBox);
        scoreFade.setFromValue(0);
        scoreFade.setToValue(1);
        scoreFade.setDelay(Duration.seconds(0.5));

        ScaleTransition scoreScale = new ScaleTransition(Duration.seconds(0.5), scoreBox);
        scoreScale.setFromX(0.8);
        scoreScale.setFromY(0.8);
        scoreScale.setToX(1);
        scoreScale.setToY(1);
        scoreScale.setDelay(Duration.seconds(0.5));

        FadeTransition messageFade = new FadeTransition(Duration.seconds(0.8), message);
        messageFade.setFromValue(0);
        messageFade.setToValue(1);
        messageFade.setDelay(Duration.seconds(0.8));

        FadeTransition buttonFade = new FadeTransition(Duration.seconds(0.8), button);
        buttonFade.setFromValue(0);
        buttonFade.setToValue(1);
        buttonFade.setDelay(Duration.seconds(1.2));

        titleFade.play();
        titleScale.play();
        scoreFade.play();
        scoreScale.play();
        messageFade.play();
        buttonFade.play();
    }
}