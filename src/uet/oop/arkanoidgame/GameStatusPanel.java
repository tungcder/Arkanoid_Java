package uet.oop.arkanoidgame;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Class quản lý khung STATUS hiển thị thông tin game
 */
public class GameStatusPanel {
    private final VBox hudBox;

    // Labels hiển thị thông tin
    private final Label livesLabel = new Label();
    private final Label livesValue = new Label();
    private final Label scoreLabel = new Label();
    private final Label scoreValue = new Label();
    private final Label timeLabel = new Label();
    private final Label timeValue = new Label();
    private final Label buffLabel = new Label();
    private final Label buffValue = new Label();
    private final Label debuffLabel = new Label();
    private final Label debuffValue = new Label();
    private final Button pauseButton;

    // Fonts
    private Font orbitronBold;
    private Font orbitronRegular;
    private Font orbitronMedium;
    private final DropShadow titleGlow = new DropShadow();
    private final DropShadow buttonGlow = new DropShadow();

    // Callback khi nhấn pause
    private Runnable onPauseToggle;

    public GameStatusPanel() {
        loadFonts();
        pauseButton = createPauseButton();
        hudBox = createHUD();
    }

    private void loadFonts() {
        try {
            orbitronBold = Font.loadFont(
                    getClass().getResourceAsStream("/fonts/Orbitron-Bold.ttf"), 32
            );
            orbitronMedium = Font.loadFont(
                    getClass().getResourceAsStream("/fonts/Orbitron-Bold.ttf"), 20
            );
            orbitronRegular = Font.loadFont(
                    getClass().getResourceAsStream("/fonts/Orbitron-Regular.ttf"), 16
            );

            if (orbitronBold == null) {
                orbitronBold = Font.font("System", FontWeight.BOLD, 32);
            }
            if (orbitronMedium == null) {
                orbitronMedium = Font.font("System", FontWeight.BOLD, 20);
            }
            if (orbitronRegular == null) {
                orbitronRegular = Font.font("System", 16);
            }
        } catch (Exception e) {
            orbitronBold = Font.font("System", FontWeight.BOLD, 32);
            orbitronMedium = Font.font("System", FontWeight.BOLD, 20);
            orbitronRegular = Font.font("System", 16);
            System.err.println("Warning: couldn't load Orbitron fonts, using system fonts.");
        }

        // Glow effect cho title
        titleGlow.setRadius(20);
        titleGlow.setColor(Color.web("#FFD700", 0.8));

        // Glow effect cho button
        buttonGlow.setRadius(12);
        buttonGlow.setColor(Color.web("#4CAF50", 0.6));
    }

    private Button createPauseButton() {
        Button btn = new Button("⏸  PAUSE");
        btn.setFont(Font.font("System", FontWeight.BOLD, 13));
        btn.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #5CDB5C, #4CAF50); " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 10 20; " +
                        "-fx-cursor: hand; " +
                        "-fx-border-color: #45a049; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10;"
        );
        btn.setPrefWidth(150);
        btn.setEffect(buttonGlow);

        // Hover effects
        btn.setOnMouseEntered(e -> {
            btn.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #6FEE6F, #5CDB5C); " +
                            "-fx-text-fill: white; " +
                            "-fx-background-radius: 10; " +
                            "-fx-padding: 10 20; " +
                            "-fx-cursor: hand; " +
                            "-fx-border-color: #4CAF50; " +
                            "-fx-border-width: 2; " +
                            "-fx-border-radius: 10; " +
                            "-fx-scale-x: 1.05; " +
                            "-fx-scale-y: 1.05;"
            );
        });

        btn.setOnMouseExited(e -> {
            if (btn.getText().contains("RESUME")) {
                btn.setStyle(
                        "-fx-background-color: linear-gradient(to bottom, #FFB84D, #FF9800); " +
                                "-fx-text-fill: white; " +
                                "-fx-background-radius: 10; " +
                                "-fx-padding: 10 20; " +
                                "-fx-cursor: hand; " +
                                "-fx-border-color: #F57C00; " +
                                "-fx-border-width: 2; " +
                                "-fx-border-radius: 10;"
                );
            } else {
                btn.setStyle(
                        "-fx-background-color: linear-gradient(to bottom, #5CDB5C, #4CAF50); " +
                                "-fx-text-fill: white; " +
                                "-fx-background-radius: 10; " +
                                "-fx-padding: 10 20; " +
                                "-fx-cursor: hand; " +
                                "-fx-border-color: #45a049; " +
                                "-fx-border-width: 2; " +
                                "-fx-border-radius: 10;"
                );
            }
        });

        btn.setOnAction(e -> {
            if (onPauseToggle != null) {
                onPauseToggle.run();
            }
        });

        return btn;
    }

    private VBox createHUD() {
        VBox box = new VBox(12);
        box.setPrefWidth(250);
        box.setPrefHeight(600);
        box.setMaxHeight(600);
        box.setAlignment(Pos.TOP_CENTER);
        box.setPadding(new Insets(18, 16, 16, 16));

        // Gradient background với viền sáng
        String bgStyle =
                "-fx-background-color: " +
                        "linear-gradient(to bottom, rgba(30,30,40,0.98), rgba(15,15,25,0.98)); " +
                        "-fx-background-radius: 12; " +
                        "-fx-border-color: linear-gradient(to bottom, rgba(100,100,120,0.5), rgba(60,60,80,0.5)); " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 12; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 15, 0, 0, 5);";
        box.setStyle(bgStyle);

        // STATUS title với animation effect
        Label title = new Label("STATUS");
        title.setFont(Font.font("System", FontWeight.BOLD, 26));
        title.setTextFill(Color.web("#FFD700"));
        title.setEffect(titleGlow);

        // Decorative line
        Rectangle topLine = new Rectangle(160, 2);
        topLine.setFill(Color.web("#FFD700", 0.6));
        topLine.setArcWidth(2);
        topLine.setArcHeight(2);

        Region spacer1 = new Region();
        spacer1.setPrefHeight(5);

        // Lives section
        VBox livesBox = createStatBox("LIVES", livesLabel, livesValue, "#00FFEA", "❤");

        // Score section
        VBox scoreBox = createStatBox("SCORE", scoreLabel, scoreValue, "#FFFFFF", "★");

        // Time section
        VBox timeBox = createStatBox("TIME", timeLabel, timeValue, "#FFA500", "⏱");

        // Separator
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: rgba(255,255,255,0.2);");
        sep.setPrefWidth(180);

        // Buff section
        VBox buffBox = createEffectBox("BUFF", buffLabel, buffValue, "#7CFC00", "↑");

        // Debuff section
        VBox debuffBox = createEffectBox("DEBUFF", debuffLabel, debuffValue, "#FF6B6B", "↓");

        // Thêm tất cả vào box
        box.getChildren().addAll(
                title, topLine, spacer1,
                pauseButton,
                createSpacer(8),
                livesBox,
                scoreBox,
                timeBox,
                sep,
                buffBox,
                debuffBox
        );

        return box;
    }

    private VBox createStatBox(String label, Label labelNode, Label valueNode, String color, String icon) {
        VBox container = new VBox(4);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(6, 8, 6, 8));
        container.setStyle(
                "-fx-background-color: rgba(255,255,255,0.05); " +
                        "-fx-background-radius: 8; " +
                        "-fx-border-color: rgba(255,255,255,0.1); " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 8;"
        );

        // Label header
        HBox headerBox = new HBox(6);
        headerBox.setAlignment(Pos.CENTER);

        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("System", 14));
        iconLabel.setTextFill(Color.web(color));

        labelNode.setText(label);
        labelNode.setFont(Font.font("System", FontWeight.BOLD, 12));
        labelNode.setTextFill(Color.web(color, 0.8));

        headerBox.getChildren().addAll(iconLabel, labelNode);

        // Value
        valueNode.setFont(Font.font("System", FontWeight.BOLD, 18));
        valueNode.setTextFill(Color.web(color));

        DropShadow glow = new DropShadow();
        glow.setRadius(6);
        glow.setColor(Color.web(color, 0.5));
        valueNode.setEffect(glow);

        container.getChildren().addAll(headerBox, valueNode);
        return container;
    }

    private VBox createEffectBox(String label, Label labelNode, Label valueNode, String color, String icon) {
        VBox container = new VBox(3);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(5, 10, 5, 10));

        HBox headerBox = new HBox(5);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        iconLabel.setTextFill(Color.web(color));

        labelNode.setText(label);
        labelNode.setFont(Font.font("System", FontWeight.BOLD, 11));
        labelNode.setTextFill(Color.web(color, 0.9));

        headerBox.getChildren().addAll(iconLabel, labelNode);

        valueNode.setFont(Font.font("System", 14));
        valueNode.setTextFill(Color.web(color, 0.95));

        container.getChildren().addAll(headerBox, valueNode);
        return container;
    }

    private Region createSpacer(double height) {
        Region spacer = new Region();
        spacer.setPrefHeight(height);
        return spacer;
    }

    /**
     * Cập nhật hiển thị Lives
     */
    public void updateLives(int lives) {
        livesValue.setText(String.valueOf(lives));
    }

    /**
     * Cập nhật hiển thị Score
     */
    public void updateScore(int score) {
        scoreValue.setText(String.valueOf(score));
    }

    /**
     * Cập nhật hiển thị Time
     */
    public void updateTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        timeValue.setText(String.format("%02d:%02d", minutes, secs));
    }

    /**
     * Cập nhật hiển thị Buff
     */
    public void updateBuff(String buffText, int buffTime) {
        buffValue.setText(buffText + (buffTime > 0 ? " (" + buffTime + "s)" : ""));
    }

    /**
     * Cập nhật hiển thị Debuff
     */
    public void updateDebuff(String debuffText, int debuffTime) {
        debuffValue.setText(debuffText + (debuffTime > 0 ? " (" + debuffTime + "s)" : ""));
    }

    /**
     * Cập nhật tất cả thông tin cùng lúc
     */
    public void updateAll(int lives, int score, int seconds,
                          String buffText, int buffTime,
                          String debuffText, int debuffTime) {
        updateLives(lives);
        updateScore(score);
        updateTime(seconds);
        updateBuff(buffText, buffTime);
        updateDebuff(debuffText, debuffTime);
    }

    /**
     * Đặt trạng thái nút Pause
     */
    public void setPauseButtonState(boolean isPaused) {
        if (isPaused) {
            pauseButton.setText("▶  RESUME");
            pauseButton.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #FFB84D, #FF9800); " +
                            "-fx-text-fill: white; " +
                            "-fx-background-radius: 10; " +
                            "-fx-padding: 10 20; " +
                            "-fx-cursor: hand; " +
                            "-fx-border-color: #F57C00; " +
                            "-fx-border-width: 2; " +
                            "-fx-border-radius: 10;"
            );
            DropShadow resumeGlow = new DropShadow();
            resumeGlow.setRadius(12);
            resumeGlow.setColor(Color.web("#FF9800", 0.6));
            pauseButton.setEffect(resumeGlow);
        } else {
            pauseButton.setText("⏸  PAUSE");
            pauseButton.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #5CDB5C, #4CAF50); " +
                            "-fx-text-fill: white; " +
                            "-fx-background-radius: 10; " +
                            "-fx-padding: 10 20; " +
                            "-fx-cursor: hand; " +
                            "-fx-border-color: #45a049; " +
                            "-fx-border-width: 2; " +
                            "-fx-border-radius: 10;"
            );
            pauseButton.setEffect(buttonGlow);
        }
    }

    /**
     * Đặt callback khi nhấn nút pause
     */
    public void setOnPauseToggle(Runnable callback) {
        this.onPauseToggle = callback;
    }

    /**
     * Lấy VBox chứa toàn bộ HUD
     */
    public VBox getHudBox() {
        return hudBox;
    }
}