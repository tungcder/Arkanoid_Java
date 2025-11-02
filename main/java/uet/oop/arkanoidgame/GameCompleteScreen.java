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

/**
 * Màn hình hiển thị khi hoàn thành game (Victory)
 * Cố định kích thước: 800x600
 */
public class GameCompleteScreen extends StackPane {

    private static final String BG_IMAGE_PATH = "/Images/Screen/GameComplete.jpg";

    private final int finalScore;
    private final int totalTime;
    private final Runnable onBackToMenu;

    public GameCompleteScreen(int score, int timeInSeconds, Runnable backToMenuAction) {
        this.finalScore = score;
        this.totalTime = timeInSeconds;
        this.onBackToMenu = backToMenuAction;

        setPrefSize(800, 600);
        setMaxSize(800, 600);
        setMinSize(800, 600);

        setupUI();
    }

    private void setupUI() {
        // Background gradient
        try {
            Image bgImg = new Image(getClass().getResource(BG_IMAGE_PATH).toExternalForm());
            ImageView background = new ImageView(bgImg);
            background.setFitWidth(800);
            background.setFitHeight(600);
            getChildren().add(background); // Thêm ảnh nền vào StackPane (lớp dưới cùng)
        } catch (Exception e) {
            //System.err.println("Không tải được ảnh nền GameComplete: " + VICTORY_ICON_PATH);
            setStyle("-fx-background-color: linear-gradient(to bottom, #0a0a1e, #1e0a32);");
        }

        // Overlay để dễ đọc chữ
        Rectangle overlay = new Rectangle(800, 600);
        overlay.setFill(Color.rgb(0, 0, 0, 0.4));

        // Content box (căn giữa, giới hạn chiều cao)
        VBox contentBox = new VBox(25);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(20, 0, 20, 0));
        contentBox.setPrefSize(760, 560);
        contentBox.setMaxSize(760, 560);

        Label victoryLabel = createVictoryTitle();
        Rectangle topLine = createDecorativeLine();

        VBox scoreBox = createScoreBox();
        VBox timeBox = createTimeBox();

        VBox statsContainer = new VBox(15, scoreBox, timeBox);
        statsContainer.setAlignment(Pos.CENTER);

        Rectangle bottomLine = createDecorativeLine();
        Button backButton = createBackButton();

        contentBox.getChildren().addAll(
                victoryLabel,
                topLine,
                statsContainer,
                bottomLine,
                backButton);

        getChildren().addAll(overlay, contentBox);
        playAnimations(victoryLabel, scoreBox, timeBox, backButton);
    }

    private Label createVictoryTitle() {
        Label label = new Label("🎉 VICTORY! 🎉");
        try {
            Font custom = Font.loadFont(getClass().getResourceAsStream("/fonts/Orbitron-Bold.ttf"), 50);
            label.setFont(custom != null ? custom : Font.font("System", FontWeight.BOLD, 50));
        } catch (Exception e) {
            label.setFont(Font.font("System", FontWeight.BOLD, 50));
        }
        label.setTextFill(Color.web("#FFD700"));
        DropShadow glow = new DropShadow(20, Color.web("#FFD700", 0.7));
        label.setEffect(glow);
        return label;
    }

    private Rectangle createDecorativeLine() {
        Rectangle line = new Rectangle(400, 2.5);
        line.setFill(Color.web("#FFD700", 0.7));
        DropShadow glow = new DropShadow(10, Color.web("#FFD700", 0.5));
        line.setEffect(glow);
        return line;
    }

    private VBox createScoreBox() {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(15));
        box.setStyle(
                "-fx-background-color: linear-gradient(to bottom, rgba(255,215,0,0.15), rgba(255,165,0,0.15)); " +
                        "-fx-background-radius: 10; " +
                        "-fx-border-color: rgba(255,215,0,0.4); " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10;"
        );

        Label title = new Label("YOUR SCORE");
        title.setFont(Font.font("System", FontWeight.BOLD, 18));
        title.setTextFill(Color.web("#FFD700"));

        Label value = new Label(String.valueOf(finalScore));
        value.setFont(Font.font("System", FontWeight.BOLD, 42));
        value.setTextFill(Color.web("#FFD700"));
        value.setEffect(new DropShadow(10, Color.web("#FFD700", 0.7)));

        box.getChildren().addAll(title, value);
        return box;
    }

    private VBox createTimeBox() {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(15));
        box.setStyle(
                "-fx-background-color: linear-gradient(to bottom, rgba(0,191,255,0.15), rgba(30,144,255,0.15)); " +
                        "-fx-background-radius: 10; " +
                        "-fx-border-color: rgba(0,191,255,0.4); " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10;"
        );

        Label title = new Label("TIME TAKEN");
        title.setFont(Font.font("System", FontWeight.BOLD, 18));
        title.setTextFill(Color.web("#00BFFF"));

        int minutes = totalTime / 60;
        int seconds = totalTime % 60;
        Label value = new Label(String.format("%02d:%02d", minutes, seconds));
        value.setFont(Font.font("System", FontWeight.BOLD, 42));
        value.setTextFill(Color.web("#00BFFF"));
        value.setEffect(new DropShadow(10, Color.web("#00BFFF", 0.7)));

        box.getChildren().addAll(title, value);
        return box;
    }

    private Button createBackButton() {
        Button button = new Button("⬅  BACK TO MENU");
        button.setFont(Font.font("System", FontWeight.BOLD, 18));
        button.setPrefSize(240, 50);
        button.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #FFD700, #FFA500); " +
                        "-fx-text-fill: #000; -fx-background-radius: 10; " +
                        "-fx-border-color: #FF8C00; -fx-border-width: 2; -fx-border-radius: 10;"
        );

        button.setOnMouseEntered(e -> button.setScaleX(1.05));
        button.setOnMouseExited(e -> button.setScaleX(1.0));
        button.setOnMouseExited(e -> button.setScaleY(1.0));
        button.setOnMouseEntered(e -> button.setScaleY(1.05));

        button.setOnAction(e -> {
            if (onBackToMenu != null) onBackToMenu.run();
        });

        return button;
    }

    private void playAnimations(Label title, VBox scoreBox, VBox timeBox, Button button) {
        FadeTransition titleFade = new FadeTransition(Duration.seconds(1), title);
        titleFade.setFromValue(0);
        titleFade.setToValue(1);

        ScaleTransition titleScale = new ScaleTransition(Duration.seconds(0.4), title);
        titleScale.setFromX(0.7);
        titleScale.setFromY(0.7);
        titleScale.setToX(1);
        titleScale.setToY(1);

        FadeTransition scoreFade = new FadeTransition(Duration.seconds(0.8), scoreBox);
        scoreFade.setFromValue(0);
        scoreFade.setToValue(1);
        scoreFade.setDelay(Duration.seconds(0.4));

        FadeTransition timeFade = new FadeTransition(Duration.seconds(0.8), timeBox);
        timeFade.setFromValue(0);
        timeFade.setToValue(1);
        timeFade.setDelay(Duration.seconds(0.7));

        FadeTransition buttonFade = new FadeTransition(Duration.seconds(0.8), button);
        buttonFade.setFromValue(0);
        buttonFade.setToValue(1);
        buttonFade.setDelay(Duration.seconds(1));

        titleFade.play();
        titleScale.play();
        scoreFade.play();
        timeFade.play();
        buttonFade.play();
    }
}
