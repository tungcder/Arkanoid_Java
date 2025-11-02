package uet.oop.arkanoidgame.entities.menu;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import uet.oop.arkanoidgame.GamePanel;
import uet.oop.arkanoidgame.entities.data.GameSaveManager;
import uet.oop.arkanoidgame.SoundManager;
import uet.oop.arkanoidgame.Setting.SettingScreen;
import uet.oop.arkanoidgame.HighScoreScreen; // ✅ THÊM DÒNG NÀY

public class MainMenu extends StackPane {

    // Các constants giữ nguyên...
    private static final String FONT_SIZE = "15px";
    private static final String BUTTON_RADIUS = "15";
    private static final String BUTTON_PADDING = "10 30 10 30";

    private static final String NORMAL_STYLE =
            "-fx-background-color: rgba(30, 30, 60, 0.8);"
                    + "-fx-text-fill: #00ffff;"
                    + "-fx-font-size: " + FONT_SIZE + ";"
                    + "-fx-font-weight: bold;"
                    + "-fx-border-color: #00ffff;"
                    + "-fx-border-width: 2;"
                    + "-fx-border-radius: " + BUTTON_RADIUS + ";"
                    + "-fx-background-radius: " + BUTTON_RADIUS + ";"
                    + "-fx-padding: " + BUTTON_PADDING + ";";

    private static final String HOVER_STYLE =
            "-fx-background-color: linear-gradient(to bottom, #ffc04c, #ff8c00);"
                    + "-fx-text-fill: #1a1a1a;"
                    + "-fx-font-size: " + FONT_SIZE + ";"
                    + "-fx-font-weight: bold;"
                    + "-fx-border-color: #ffc04c;"
                    + "-fx-border-width: 2;"
                    + "-fx-border-radius: " + BUTTON_RADIUS + ";"
                    + "-fx-background-radius: " + BUTTON_RADIUS + ";"
                    + "-fx-padding: " + BUTTON_PADDING + ";";

    private static final String DISABLED_STYLE =
            "-fx-background-color: rgba(50, 50, 50, 0.5);"
                    + "-fx-text-fill: #666666;"
                    + "-fx-font-size: " + FONT_SIZE + ";"
                    + "-fx-font-weight: bold;"
                    + "-fx-border-color: #666666;"
                    + "-fx-border-width: 2;"
                    + "-fx-border-radius: " + BUTTON_RADIUS + ";"
                    + "-fx-background-radius: " + BUTTON_RADIUS + ";"
                    + "-fx-padding: " + BUTTON_PADDING + ";"
                    + "-fx-opacity: 0.5;";

    DropShadow neonShadow = new DropShadow(20, Color.web("#00ffff"));

    private final Stage stage;
    private final SoundManager soundManager;

    public MainMenu(Stage stage, SoundManager soundManager) {

        this.stage = stage;
        this.soundManager = soundManager;

        soundManager.playMusic("Menu", true);

        java.net.URL bgUrl = getClass().getClassLoader().getResource("Images/Screen/Menu.jpg");
        if (bgUrl == null) {
            throw new RuntimeException("Không tìm thấy file Menu.jpg! Kiểm tra thư mục resources/Images/Screen/");
        }
        Image bgImage = new Image(bgUrl.toExternalForm());
        ImageView background = new ImageView(bgImage);
        background.setFitWidth(800);
        background.setFitHeight(600);

        Button startBtn = createStyledButton("NEW GAME", neonShadow);
        Button continueBtn = createStyledButton("CONTINUE", neonShadow);
        Button highScoreBtn = createStyledButton("HIGH SCORE", neonShadow);
        Button settingBtn = createStyledButton("SETTING", neonShadow);
        Button exitBtn = createStyledButton("EXIT", neonShadow);

        updateContinueButtonState(continueBtn);

        Button[] buttons = {startBtn, continueBtn, settingBtn, highScoreBtn, exitBtn};
        for (Button btn : buttons) {
            if (!btn.isDisabled()) {
                btn.setOnMouseEntered(e -> btn.setStyle(HOVER_STYLE));
                btn.setOnMouseExited(e -> btn.setStyle(NORMAL_STYLE));
            }
        }

        startBtn.setOnAction(e -> {
            this.soundManager.stopMusic();
            GameSaveManager.deleteSave();
            GamePanel gamePanel = new GamePanel(stage, soundManager, false);
            gamePanel.startGame();
        });

        continueBtn.setOnAction(e -> {
            if (GameSaveManager.hasSavedGame()) {
                this.soundManager.stopMusic();
                GamePanel gamePanel = new GamePanel(stage, soundManager, true);
                gamePanel.startGame();
            } else {
                System.out.println("⚠ Không có save game để tiếp tục!");
            }
        });

        settingBtn.setOnAction(e -> {
            SettingScreen settingScreen = new SettingScreen(this.stage, this.soundManager);
            Scene settingScene = new Scene(settingScreen, 800, 600);
            this.stage.setScene(settingScene);
        });

        highScoreBtn.setOnAction(e -> showHighScoreScreen());

        exitBtn.setOnAction(e -> this.stage.close());

        VBox menuColumn = new VBox(20, startBtn, continueBtn, settingBtn, highScoreBtn, exitBtn);
        menuColumn.setAlignment(Pos.CENTER);
        menuColumn.setTranslateY(100);

        getChildren().addAll(background, menuColumn);
        setAlignment(Pos.CENTER);
    }

    private void updateContinueButtonState(Button continueBtn) {
        boolean hasSave = GameSaveManager.hasSavedGame();

        if (!hasSave) {
            continueBtn.setDisable(true);
            continueBtn.setStyle(DISABLED_STYLE);
            continueBtn.setOnMouseEntered(null);
            continueBtn.setOnMouseExited(null);
        } else {
            continueBtn.setDisable(false);
            continueBtn.setStyle(NORMAL_STYLE);
            continueBtn.setOnMouseEntered(e -> continueBtn.setStyle(HOVER_STYLE));
            continueBtn.setOnMouseExited(e -> continueBtn.setStyle(NORMAL_STYLE));
        }

        System.out.println("📋 Continue button state: " + (hasSave ? "ENABLED" : "DISABLED"));
    }

    private Button createStyledButton(String text, DropShadow effect) {
        Button btn = new Button(text);
        btn.setStyle(NORMAL_STYLE);
        btn.setEffect(effect);
        return btn;
    }

    private void showHighScoreScreen() {
        HighScoreScreen highScoreScreen = new HighScoreScreen(() -> {
            // Quay lại menu
            Scene mainMenuScene = createScene(this.stage);
            this.stage.setScene(mainMenuScene);
        });

        Scene highScoreScene = new Scene(highScoreScreen, 800, 600);
        this.stage.setScene(highScoreScene);
    }

    public Scene createScene(Stage stage) {
        return new Scene(new MainMenu(stage, this.soundManager), 800, 600);
    }
}