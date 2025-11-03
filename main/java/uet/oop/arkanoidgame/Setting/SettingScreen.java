package uet.oop.arkanoidgame.Setting;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import uet.oop.arkanoidgame.SoundManager;
import uet.oop.arkanoidgame.ThemeManager;
import uet.oop.arkanoidgame.entities.menu.MainMenu;

import java.util.function.Consumer;

/**
 * Lớp quản lý màn hình Cài Đặt (Settings).
 * Cho phép điều chỉnh âm lượng và chủ đề.
 */
public class SettingScreen extends StackPane {

    // --- Hằng số Quản lý Bố cục (Layout) ---
    private static final String BACKGROUND_IMAGE_PATH = "/Images/Screen/Setting.jpg";
    private static final double PANEL_WIDTH = 350;
    private static final double CONTENT_PADDING = 0;
    private static final double CONTENT_SPACING = 10;

    // Margins (Lề)
    private static final double TITLE_MARGIN_TOP = 100;
    private static final double SOUND_PANEL_MARGIN_TOP = 260;
    private static final double THEME_PANEL_MARGIN_TOP = 250;
    private static final double PANEL_MARGIN_LEFT = 50;  // Cách lề trái 50px
    private static final double PANEL_MARGIN_RIGHT = 30; // Cách lề phải 50px
    private static final double BACK_BTN_MARGIN_BOTTOM = 15;

    // --- Hằng số Component (Thành phần) ---
    private static final double TITLE_FONT_SIZE = 40;
    private static final double TITLE_GLOW_RADIUS = 25;

    private static final double SLIDER_LABEL_FONT_SIZE = 18;
    private static final double PERCENT_LABEL_FONT_SIZE = 14;
    private static final double PERCENT_LABEL_WIDTH = 55;
    private static final double PERCENT_LABEL_TRANSLATE_Y = -15;
    private static final double SLIDER_ROW_SPACING = 0;
    private static final double SLIDER_BOX_SPACING = 5;

    // --- Hằng số Style Nút "BACK" ---
    private static final String BACK_NORMAL_STYLE = "-fx-background-color: rgba(30, 30, 60, 0.8);"
            + "-fx-text-fill: #00ffff;"
            + "-fx-font-size: 15px;"
            + "-fx-font-weight: bold;"
            + "-fx-border-color: #00ffff;"
            + "-fx-border-width: 2;"
            + "-fx-border-radius: 15;"
            + "-fx-background-radius: 15;"
            + "-fx-padding: 10 30 10 30;";

    private static final String BACK_HOVER_STYLE = "-fx-background-color: linear-gradient(to bottom, #ffc04c, #ff8c00);"
            + "-fx-text-fill: #1a1a1a;"
            + "-fx-font-size: 15px;"
            + "-fx-font-weight: bold;"
            + "-fx-border-color: #ffc04c;"
            + "-fx-border-width: 2;"
            + "-fx-border-radius: 15;"
            + "-fx-background-radius: 15;"
            + "-fx-padding: 10 30 10 30;";

    // --- Hằng số Style Nút "THEME" ---
    private static final String THEME_NORMAL_STYLE = "-fx-background-color: rgba(60, 30, 60, 0.8);"
            + "-fx-text-fill: #00ffff;"
            + "-fx-font-size: 18px;"
            + "-fx-font-weight: bold;"
            + "-fx-border-color: #00ffff;"
            + "-fx-border-width: 2;"
            + "-fx-border-radius: 10;"
            + "-fx-background-radius: 10;"
            + "-fx-padding: 12 40 12 40;"
            + "-fx-pref-width: 250px;";

    private static final String THEME_HOVER_STYLE = "-fx-background-color: linear-gradient(to bottom, #ffc04c, #ff8c00);"
            + "-fx-text-fill: #1a1a1a;"
            + "-fx-font-size: 18px;"
            + "-fx-font-weight: bold;"
            + "-fx-border-color: #ffc04c;"
            + "-fx-border-width: 2;"
            + "-fx-border-radius: 10;"
            + "-fx-background-radius: 10;"
            + "-fx-padding: 12 40 12 40;"
            + "-fx-pref-width: 250px;";

    private static final String ARROW_STYLE = "-fx-background-color: rgba(30, 30, 60, 0.8);"
            + "-fx-text-fill: #00ffff; -fx-font-size: 24px; -fx-font-weight: bold;"
            + "-fx-border-color: #00ffff; -fx-border-width: 2; -fx-background-radius: 5;"
            + "-fx-border-radius: 5; -fx-padding: 5 15;";

    private static final String ARROW_HOVER_STYLE = "-fx-background-color: linear-gradient(to bottom, #ffc04c, #ff8c00);"
            + "-fx-text-fill: #1a1a1a; -fx-font-size: 24px; -fx-font-weight: bold;"
            + "-fx-border-color: #ffc04c; -fx-border-width: 2; -fx-background-radius: 5;"
            + "-fx-border-radius: 5; -fx-padding: 5 15;";


    private final Stage stage;
    private final SoundManager soundManager;

    private final java.util.List<String> themeNames = java.util.List.of("Theme1", "Theme2", "Theme3");
    private final java.util.List<String> themeDisplayNames = java.util.List.of("SPACE", "PYRAMID", "OCEAN");
    private int currentThemeIndex;
    private Label currentThemeLabel;

    public SettingScreen(Stage stage, SoundManager soundManager) {

        String initialTheme = ThemeManager.getCurrentTheme();
        this.currentThemeIndex = themeNames.indexOf(initialTheme);
        if (this.currentThemeIndex == -1) {
            this.currentThemeIndex = 0;
        }

        this.stage = stage;
        this.soundManager = soundManager;

        // 1. Ảnh nền
        Image bgImage = new Image(
                getClass().getResource(BACKGROUND_IMAGE_PATH).toExternalForm()
        );
        ImageView background = new ImageView(bgImage);
        background.setFitWidth(800);
        background.setFitHeight(600);

        // 2. Tiêu đề
        Label title = createTitle("SETTINGS");

        // 3. Hộp chứa nội dung ÂM THANH
        VBox soundBox = new VBox(CONTENT_SPACING);
        soundBox.setAlignment(Pos.TOP_CENTER); // Căn giữa các thanh trượt
        soundBox.setPadding(new Insets(CONTENT_PADDING));
        soundBox.setMaxWidth(PANEL_WIDTH);

        // Các thanh trượt âm lượng
        VBox totalVolumeBox = createVolumeSlider(
                "Total Volume",
                soundManager.getMasterVolume(),
                soundManager::setMasterVolume
        );

        VBox musicVolumeBox = createVolumeSlider(
                "Background Music Volume",
                soundManager.getMusicVolume(),
                soundManager::setMusicVolume
        );

        VBox sfxVolumeBox = createVolumeSlider(
                "Sound Effect Volume",
                soundManager.getSfxVolume(),
                soundManager::setSfxVolume
        );

        // Thêm mọi thứ vào Hộp ÂM THANH
        soundBox.getChildren().addAll(
                totalVolumeBox,
                musicVolumeBox,
                sfxVolumeBox
        );

        // 4. Hộp chứa nội dung CHỦ ĐỀ
        VBox themeBox = new VBox(CONTENT_SPACING);
        themeBox.setAlignment(Pos.TOP_CENTER);
        themeBox.setPadding(new Insets(CONTENT_PADDING));
        themeBox.setMaxWidth(PANEL_WIDTH);

        // Tiêu đề cho chủ đề
        Label themeTitle = createTitle("THEME");
        themeTitle.setFont(Font.font("System", FontWeight.BOLD, 32));
        themeTitle.setTextFill(Color.web("#00ffff"));
        themeTitle.setEffect(new DropShadow(20, Color.web("#00ffff")));

        // Tạo các nút mũi tên
        Button leftArrow = new Button("<");
        Button rightArrow = new Button(">");

        // Style cho nút
        leftArrow.setStyle(ARROW_STYLE);
        rightArrow.setStyle(ARROW_STYLE);
        leftArrow.setOnMouseEntered(e -> leftArrow.setStyle(ARROW_HOVER_STYLE));
        leftArrow.setOnMouseExited(e -> leftArrow.setStyle(ARROW_STYLE));
        rightArrow.setOnMouseEntered(e -> rightArrow.setStyle(ARROW_HOVER_STYLE));
        rightArrow.setOnMouseExited(e -> rightArrow.setStyle(ARROW_STYLE));

        // Label hiển thị tên theme
        currentThemeLabel = new Label(themeDisplayNames.get(currentThemeIndex));
        currentThemeLabel.setFont(Font.font("System", FontWeight.BOLD, 26)); // Cỡ chữ lớn
        currentThemeLabel.setTextFill(Color.web("#00ffff"));
        currentThemeLabel.setPrefWidth(200); // Đặt chiều rộng cố định để HBox ổn định
        currentThemeLabel.setAlignment(Pos.CENTER);

        // Góm thành HBox
        HBox selectorBox = new HBox(0, leftArrow, currentThemeLabel, rightArrow);
        selectorBox.setAlignment(Pos.TOP_CENTER);

        // Gán hành động
        leftArrow.setOnAction(e -> selectPreviousTheme());
        rightArrow.setOnAction(e -> selectNextTheme());

        // Thêm mọi thứ vào Hộp CHỦ ĐỀ
        themeBox.getChildren().addAll(
                themeTitle,
                selectorBox
        );

        // 5. Nút quay lại
        Button backButton = createBackButton();
        backButton.setOnAction(e -> {
            // Quay trở lại Main Menu
            MainMenu mainMenu = new MainMenu(stage, soundManager);
            stage.setScene(new Scene(mainMenu, 800, 600));
        });

        // 6. Thêm vào StackPane ---
        getChildren().addAll(background, title, soundBox, themeBox, backButton);

        // Căn lề TIÊU ĐỀ
        StackPane.setAlignment(title, Pos.TOP_CENTER);
        StackPane.setMargin(title, new Insets(TITLE_MARGIN_TOP, 0, 0, 0));

        // Căn lề Panel ÂM THANH (Trái)
        StackPane.setAlignment(soundBox, Pos.TOP_LEFT);
        StackPane.setMargin(soundBox, new Insets(SOUND_PANEL_MARGIN_TOP, 0, 0, PANEL_MARGIN_LEFT));

        // Căn lề Panel CHỦ ĐỀ (Phải)
        StackPane.setAlignment(themeBox, Pos.TOP_RIGHT);
        StackPane.setMargin(themeBox, new Insets(THEME_PANEL_MARGIN_TOP, PANEL_MARGIN_RIGHT, 0, 0));

        // Căn lề Nút BACK (Giữa bên dưới)
        StackPane.setAlignment(backButton, Pos.BOTTOM_CENTER);
        StackPane.setMargin(backButton, new Insets(0, 0, BACK_BTN_MARGIN_BOTTOM, 0));
    }

    /**
     * Hàm tiện ích tạo thanh trượt âm lượng.
     */
    private VBox createVolumeSlider(String label, double initialValue, Consumer<Double> onVolumeChange) {
        // Tiêu đề của thanh trượt
        Label sliderLabel = new Label(label);
        sliderLabel.setFont(Font.font("System", FontWeight.BOLD, SLIDER_LABEL_FONT_SIZE));
        sliderLabel.setTextFill(Color.web("#00ffff"));
        sliderLabel.setEffect(new DropShadow(10, Color.web("#00ffff", 0.7)));

        // Thanh trượt
        Slider slider = new Slider(0, 1, initialValue); // Min=0, Max=1, Giá trị hiện tại
        slider.setMajorTickUnit(0.1);
        slider.setMinorTickCount(1);
        slider.setBlockIncrement(0.1); // Bước nhảy khi nhấp
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setStyle("-fx-text-fill: white; -fx-control-inner-background: rgba(100, 100, 100, 0.5);");

        // Label hiển thị %
        Label percentageLabel = new Label(String.format("%.0f%%", initialValue * 100));
        percentageLabel.setFont(Font.font("System", FontWeight.BOLD, PERCENT_LABEL_FONT_SIZE));
        percentageLabel.setTextFill(Color.WHITE);

        // Liên kết sự kiện
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            onVolumeChange.accept(newValue.doubleValue());
            percentageLabel.setText(String.format("%.0f%%", newValue.doubleValue() * 100));
            SettingManager.saveSettings(soundManager);
        });

        // Hộp chứa thanh trượt và %
        percentageLabel.setPrefWidth(PERCENT_LABEL_WIDTH);
        percentageLabel.setAlignment(Pos.CENTER_RIGHT);
        percentageLabel.setTranslateY(PERCENT_LABEL_TRANSLATE_Y);

        HBox sliderRow = new HBox(SLIDER_ROW_SPACING, slider, percentageLabel);
        sliderRow.setAlignment(Pos.CENTER_LEFT);

        HBox.setHgrow(slider, Priority.ALWAYS);

        VBox container = new VBox(SLIDER_BOX_SPACING, sliderLabel, sliderRow);
        container.setAlignment(Pos.CENTER_LEFT);
        return container;
    }

    /**
     * Hàm tiện ích tạo Tiêu đề.
     */
    private Label createTitle(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.BOLD, TITLE_FONT_SIZE));
        label.setTextFill(Color.web("#00ffff"));
        label.setEffect(new DropShadow(TITLE_GLOW_RADIUS, Color.web("#00ffff")));
        return label;
    }

    /**
     * Hàm mới tạo nút BACK.
     */
    private Button createBackButton() {
        Button backButton = new Button("BACK");
        backButton.setStyle(BACK_NORMAL_STYLE);
        backButton.setEffect(new DropShadow(20, Color.web("#00ffff")));
        backButton.setOnMouseEntered(e -> backButton.setStyle(BACK_HOVER_STYLE));
        backButton.setOnMouseExited(e -> backButton.setStyle(BACK_NORMAL_STYLE));
        return backButton;
    }

    /**
     * Hàm tiện ích tạo nút CHỦ ĐỀ.
     */
    private Button createThemeButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(THEME_NORMAL_STYLE);
        btn.setEffect(new DropShadow(20, Color.web("#00ffff")));
        btn.setOnMouseEntered(e -> btn.setStyle(THEME_HOVER_STYLE));
        btn.setOnMouseExited(e -> btn.setStyle(THEME_NORMAL_STYLE));
        return btn;
    }

    /**
     * Chuyển sang chủ đề phía trước.
     */
    private void selectPreviousTheme() {
        currentThemeIndex--;
        if (currentThemeIndex < 0) {
            currentThemeIndex = themeNames.size() - 1; // Xoay vòng về cuối
        }
        updateTheme();
    }

    /**
     * Chuyển sang chủ đề tiếp theo.
     */
    private void selectNextTheme() {
        currentThemeIndex++;
        if (currentThemeIndex >= themeNames.size()) {
            currentThemeIndex = 0; // Xoay vòng về đầu
        }
        updateTheme();
    }

    /**
     * Cập nhật UI và ThemeManager toàn cục.
     */
    private void updateTheme() {
        String themeName = themeNames.get(currentThemeIndex);
        String themeDisplayName = themeDisplayNames.get(currentThemeIndex);

        // 1. Cập nhật Label trên màn hình
        currentThemeLabel.setText(themeDisplayName);

        // 2. Cập nhật ThemeManager để toàn bộ game sử dụng
        ThemeManager.setCurrentTheme(themeName);

        SettingManager.saveSettings(soundManager);
    }
}