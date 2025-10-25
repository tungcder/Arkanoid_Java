package uet.oop.arkanoidgame.entities.menu;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import uet.oop.arkanoidgame.GamePanel;

public class MainMenu extends StackPane {

    // Màu sắc và style tùy chỉnh
    private static final String FONT_SIZE = "26px";
    private static final String BUTTON_RADIUS = "15";
    private static final String BUTTON_PADDING = "10 60 10 60";

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

    // Hiệu ứng đổ bóng mạnh hơn, mô phỏng ánh sáng neon
    DropShadow neonShadow = new DropShadow(20, Color.web("#00ffff")); // Màu neon xanh

    public MainMenu(Stage stage) {
        // --- 1. Ảnh nền ---
        Image bgImage = new Image(
                getClass().getResource("/uet/oop/arkanoidgame/entities/menu/menu_images/menu_bg1.jpg").toExternalForm()
        );
        ImageView background = new ImageView(bgImage);
        background.setFitWidth(800);
        background.setFitHeight(600);

        // --- 2. Các nút bấm ---
        Button startBtn = createStyledButton("START GAME", neonShadow);
        Button highScoreBtn = createStyledButton("HIGH SCORE", neonShadow);
        Button optionsBtn = createStyledButton("OPTIONS", neonShadow);
        Button exitBtn = createStyledButton("EXIT", neonShadow);

        // Gắn sự kiện hover
        Button[] buttons = {startBtn, highScoreBtn, optionsBtn, exitBtn};
        for (Button btn : buttons) {
            btn.setOnMouseEntered(e -> btn.setStyle(HOVER_STYLE));
            btn.setOnMouseExited(e -> btn.setStyle(NORMAL_STYLE));
        }

        // --- 3. Hành động nút ---
        startBtn.setOnAction(e -> {
            GamePanel gamePanel = new GamePanel(stage);
            gamePanel.startGame();
        });

        highScoreBtn.setOnAction(e -> showHighScoreScreen(stage));
        optionsBtn.setOnAction(e -> System.out.println("Open Options!"));
        exitBtn.setOnAction(e -> stage.close());

        // --- 4. Bố cục 2 cột (Giống code ban đầu) ---

        // Cột bên trái (Start + Exit)
        VBox leftBox = new VBox(30, startBtn, exitBtn); // Tăng khoảng cách VBox lên 30
        leftBox.setAlignment(Pos.CENTER);

        // Cột bên phải (High Score + Options)
        VBox rightBox = new VBox(30, highScoreBtn, optionsBtn); // Tăng khoảng cách VBox lên 30
        rightBox.setAlignment(Pos.CENTER);

        // Gộp hai cột lại trong HBox
        HBox menuBox = new HBox(120, leftBox, rightBox); // Tăng khoảng cách giữa 2 cột lên 120
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setTranslateY(100); // Dịch chuyển nhóm nút xuống dưới để lấp đầy khoảng trống của Title

        // --- 5. Thêm vào StackPane ---
        // Chỉ thêm nền và menu box (đã bỏ Title Label)
        getChildren().addAll(background, menuBox);

        setAlignment(Pos.CENTER);
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

    private void showHighScoreScreen(Stage stage) {
        // Tạm thời điểm cao cố định
        int highScore = 12345;

        // Ảnh nền
        Image bgImage = new Image(
                getClass().getResource("/uet/oop/arkanoidgame/entities/menu/menu_images/menu_bg1.jpg").toExternalForm()
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
            Scene mainMenuScene = createScene(stage);
            stage.setScene(mainMenuScene);
        });

        VBox box = new VBox(40, scoreLabel, backButton);
        box.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(background, box);
        Scene highScoreScene = new Scene(root, 800, 600);
        stage.setScene(highScoreScene);
    }

    public Scene createScene(Stage stage) {
        return new Scene(new MainMenu(stage), 800, 600);
    }
}