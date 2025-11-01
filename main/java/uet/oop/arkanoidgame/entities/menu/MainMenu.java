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

public class MainMenu extends StackPane {

    // Màu sắc và style tùy chỉnh
    private static final String FONT_SIZE = "15px";
    private static final String BUTTON_RADIUS = "15";
    private static final String BUTTON_PADDING = "10 30 10 30";

    // Style cho nút bình thường: nền tối, chữ neon
    private static final String NORMAL_STYLE =
            "-fx-background-color: rgba(30, 30, 60, 0.8);" // Nền tím đậm trong suốt
                    + "-fx-text-fill: #00ffff;" // Chữ xanh Cyan Neon
                    + "-fx-font-size: " + FONT_SIZE + ";"
                    + "-fx-font-weight: bold;"
                    + "-fx-border-color: #00ffff;" // Viền neon
                    + "-fx-border-width: 2;"
                    + "-fx-border-radius: " + BUTTON_RADIUS + ";"
                    + "-fx-background-radius: " + BUTTON_RADIUS + ";"
                    + "-fx-padding: " + BUTTON_PADDING + ";";

    // Style khi di chuột qua: chuyển sang màu vàng/cam sáng
    private static final String HOVER_STYLE =
            "-fx-background-color: linear-gradient(to bottom, #ffc04c, #ff8c00);" // Gradient ấm áp
                    + "-fx-text-fill: #1a1a1a;" // Chữ đen/xám đậm
                    + "-fx-font-size: " + FONT_SIZE + ";"
                    + "-fx-font-weight: bold;"
                    + "-fx-border-color: #ffc04c;"
                    + "-fx-border-width: 2;"
                    + "-fx-border-radius: " + BUTTON_RADIUS + ";"
                    + "-fx-background-radius: " + BUTTON_RADIUS + ";"
                    + "-fx-padding: " + BUTTON_PADDING + ";";

    // Style cho nút bị vô hiệu hóa
    private static final String DISABLED_STYLE =
            "-fx-background-color: rgba(50, 50, 50, 0.5);" // Nền xám tối
                    + "-fx-text-fill: #666666;" // Chữ xám
                    + "-fx-font-size: " + FONT_SIZE + ";"
                    + "-fx-font-weight: bold;"
                    + "-fx-border-color: #666666;" // Viền xám
                    + "-fx-border-width: 2;"
                    + "-fx-border-radius: " + BUTTON_RADIUS + ";"
                    + "-fx-background-radius: " + BUTTON_RADIUS + ";"
                    + "-fx-padding: " + BUTTON_PADDING + ";"
                    + "-fx-opacity: 0.5;";

    // Hiệu ứng đổ bóng mạnh hơn, mô phỏng ánh sáng neon
    DropShadow neonShadow = new DropShadow(20, Color.web("#00ffff")); // Màu neon xanh

    private final Stage stage;
    private final SoundManager soundManager;

    public MainMenu(Stage stage, SoundManager soundManager) {

        this.stage = stage;
        this.soundManager = soundManager;

        // Bật nhạc Menu
        soundManager.playMusic("Menu", true);

        // --- 1. Ảnh nền ---
        java.net.URL bgUrl = getClass().getClassLoader().getResource("Images/Screen/Menu.jpg");
        if (bgUrl == null) {
            throw new RuntimeException("Không tìm thấy file Menu.jpg! Kiểm tra thư mục resources/Images/Screen/");
        }
        Image bgImage = new Image(bgUrl.toExternalForm());
        ImageView background = new ImageView(bgImage);
        background.setFitWidth(800);
        background.setFitHeight(600);

        // --- 2. Các nút bấm ---
        Button startBtn = createStyledButton("NEW GAME", neonShadow);
        Button continueBtn = createStyledButton("CONTINUE", neonShadow);
        Button highScoreBtn = createStyledButton("HIGH SCORE", neonShadow);
        Button settingBtn = createStyledButton("SETTING", neonShadow);
        Button exitBtn = createStyledButton("EXIT", neonShadow);

        // ✅ FIX: Kiểm tra save game MỖI LẦN tạo menu
        updateContinueButtonState(continueBtn);

        // Gắn sự kiện hover
        Button[] buttons = {startBtn, continueBtn, settingBtn, highScoreBtn, exitBtn};
        for (Button btn : buttons) {
            if (!btn.isDisabled()) {
                btn.setOnMouseEntered(e -> btn.setStyle(HOVER_STYLE));
                btn.setOnMouseExited(e -> btn.setStyle(NORMAL_STYLE));
            }
        }

        // --- 3. Hành động nút ---
        startBtn.setOnAction(e -> {
            // Dừng nhạc Menu trước khi vào game
            this.soundManager.stopMusic();
            // Xóa save cũ khi bắt đầu game mới
            GameSaveManager.deleteSave();
            GamePanel gamePanel = new GamePanel(stage, soundManager, false);
            gamePanel.startGame();
        });

        continueBtn.setOnAction(e -> {
            // ✅ FIX: Kiểm tra lại trước khi load
            if (GameSaveManager.hasSavedGame()) {
                // Dừng nhạc Menu trước khi vào game
                this.soundManager.stopMusic();
                GamePanel gamePanel = new GamePanel(stage, soundManager, true);
                gamePanel.startGame();
            } else {
                System.out.println("⚠ Không có save game để tiếp tục!");
            }
        });

        highScoreBtn.setOnAction(e -> showHighScoreScreen());

        settingBtn.setOnAction(e -> {
            // Tạo màn hình Settings mới
            SettingScreen settingScreen = new SettingScreen(this.stage, this.soundManager);
            // Tạo Scene mới cho settings (sử dụng kích thước 800x600)
            Scene settingScene = new Scene(settingScreen, 800, 600);
            this.stage.setScene(settingScene);
        });

        exitBtn.setOnAction(e -> this.stage.close());

        // --- 4. Bố cục ---
        // Layout dạng cột dọc với tất cả các nút
        VBox menuColumn = new VBox(30, startBtn, continueBtn, settingBtn, highScoreBtn, exitBtn);
        menuColumn.setAlignment(Pos.CENTER);
        menuColumn.setTranslateY(100); // Dịch chuyển nhóm nút xuống dưới

        // --- 5. Thêm vào StackPane ---
        getChildren().addAll(background, menuColumn);
        setAlignment(Pos.CENTER);
    }

    /**
     * ✅ NEW: Cập nhật trạng thái nút CONTINUE
     */
    private void updateContinueButtonState(Button continueBtn) {
        boolean hasSave = GameSaveManager.hasSavedGame();

        if (!hasSave) {
            continueBtn.setDisable(true);
            continueBtn.setStyle(DISABLED_STYLE);
            // Xóa sự kiện hover khi disabled
            continueBtn.setOnMouseEntered(null);
            continueBtn.setOnMouseExited(null);
        } else {
            continueBtn.setDisable(false);
            continueBtn.setStyle(NORMAL_STYLE);
            // Thêm lại sự kiện hover
            continueBtn.setOnMouseEntered(e -> continueBtn.setStyle(HOVER_STYLE));
            continueBtn.setOnMouseExited(e -> continueBtn.setStyle(NORMAL_STYLE));
        }

        System.out.println("📋 Continue button state: " + (hasSave ? "ENABLED" : "DISABLED"));
    }

    /**
     * Hàm tiện ích tạo nút với style và effect mặc định
     */
    private Button createStyledButton(String text, DropShadow effect) {
        Button btn = new Button(text);
        btn.setStyle(NORMAL_STYLE);
        btn.setEffect(effect);
        return btn;
    }

    private void showHighScoreScreen() {
        // Tạm thời điểm cao cố định
        int highScore = 12345;

        // Ảnh nền
        Image bgImage = new Image(
                getClass().getResource("/Images/Screen/Menu.jpg").toExternalForm()
        );
        ImageView background = new ImageView(bgImage);
        background.setFitWidth(800);
        background.setFitHeight(600);

        // Label điểm cao được style để trông phù hợp với theme neon
        Label scoreLabel = new Label("HIGHEST SCORE: " + highScore);
        scoreLabel.setTextFill(Color.web("#ffaa00")); // Màu cam/vàng sáng
        scoreLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold;");
        DropShadow scoreShadow = new DropShadow(30, Color.web("#ffaa00"));
        scoreLabel.setEffect(scoreShadow);

        // Nút quay lại
        Button backButton = new Button("BACK TO MENU");
        backButton.setStyle("-fx-font-size: 24px; -fx-background-color: #ff8c00; -fx-text-fill: black; -fx-background-radius: 10; -fx-padding: 10 30;");

        backButton.setOnAction(e -> {
            // Truyền soundManager khi tạo lại Menu
            Scene mainMenuScene = createScene(this.stage);
            this.stage.setScene(mainMenuScene);
        });

        VBox box = new VBox(40, scoreLabel, backButton);
        box.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(background, box);
        Scene highScoreScene = new Scene(root, 800, 600);
        this.stage.setScene(highScoreScene);
    }

    public Scene createScene(Stage stage) {
        return new Scene(new MainMenu(stage, this.soundManager), 800, 600);
    }
}