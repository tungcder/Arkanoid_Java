package uet.oop.arkanoidgame;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.effect.DropShadow;
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
    // Constants - Dimensions
    private static final int PANEL_WIDTH = 250;
    private static final int PANEL_HEIGHT = 600;
    private static final int BUTTON_WIDTH = 150;

    // Constants - Colors
    private static final String GOLD_COLOR = "#FFD700";
    private static final String CYAN_COLOR = "#00FFEA";
    private static final String ORANGE_COLOR = "#FFA500";
    private static final String GREEN_COLOR = "#7CFC00";
    private static final String RED_COLOR = "#FF6B6B";
    private static final String WHITE_COLOR = "#FFFFFF";

    // Constants - Button Colors
    private static final String PAUSE_GRADIENT = "#5CDB5C, #4CAF50";
    private static final String PAUSE_HOVER_GRADIENT = "#6FEE6F, #5CDB5C";
    private static final String RESUME_GRADIENT = "#FFB84D, #FF9800";
    private static final String PAUSE_BORDER = "#45a049";
    private static final String RESUME_BORDER = "#F57C00";

    // Constants - Paths & Icons
    private static final String FONT_BOLD_PATH = "/fonts/Orbitron-Bold.ttf";
    private static final String FONT_REGULAR_PATH = "/fonts/Orbitron-Regular.ttf";
    private static final String ICON_LIVES = "❤";
    private static final String ICON_SCORE = "★";
    private static final String ICON_TIME = "⏱";
    private static final String ICON_BUFF = "↑";
    private static final String ICON_DEBUFF = "↓";

    // UI Components
    private final VBox hudBox;
    private final Button pauseButton;

    // Status Labels
    private final Label livesValue = new Label();
    private final Label scoreValue = new Label();
    private final Label timeValue = new Label();
    private final Label buffValue = new Label();
    private final Label debuffValue = new Label();

    // Effects
    private final DropShadow titleGlow;
    private final DropShadow buttonGlow;

    // Callback
    private Runnable onPauseToggle;

    public GameStatusPanel() {
        this.titleGlow = createTitleGlow();
        this.buttonGlow = createButtonGlow();
        this.pauseButton = createPauseButton();
        this.hudBox = createHUD();
    }

    private DropShadow createTitleGlow() {
        DropShadow glow = new DropShadow();
        glow.setRadius(20);
        glow.setColor(Color.web(GOLD_COLOR, 0.8));
        return glow;
    }

    private DropShadow createButtonGlow() {
        DropShadow glow = new DropShadow();
        glow.setRadius(12);
        glow.setColor(Color.web("#4CAF50", 0.6));
        return glow;
    }

    private Button createPauseButton() {
        Button btn = new Button("⏸  PAUSE");
        btn.setFont(Font.font("System", FontWeight.BOLD, 13));
        btn.setPrefWidth(BUTTON_WIDTH);
        btn.setEffect(buttonGlow);

        applyPauseButtonStyle(btn, false, false);
        setupButtonInteractions(btn);

        return btn;
    }

    private void setupButtonInteractions(Button btn) {
        btn.setOnMouseEntered(e -> handleButtonHover(btn, true));
        btn.setOnMouseExited(e -> handleButtonHover(btn, false));
        btn.setOnAction(e -> {
            if (onPauseToggle != null) onPauseToggle.run();
        });
    }

    private void handleButtonHover(Button btn, boolean isHovered) {
        boolean isResumeMode = btn.getText().contains("RESUME");
        applyPauseButtonStyle(btn, isResumeMode, isHovered);
    }

    private void applyPauseButtonStyle(Button btn, boolean isResumeMode, boolean isHovered) {
        String gradient, borderColor;

        if (isResumeMode) {
            gradient = RESUME_GRADIENT;
            borderColor = RESUME_BORDER;
        } else {
            gradient = isHovered ? PAUSE_HOVER_GRADIENT : PAUSE_GRADIENT;
            borderColor = PAUSE_BORDER;
        }

        String scale = isHovered ? "-fx-scale-x: 1.05; -fx-scale-y: 1.05;" : "";

        btn.setStyle(String.format(
                "-fx-background-color: linear-gradient(to bottom, %s); " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 10 20; " +
                        "-fx-cursor: hand; " +
                        "-fx-border-color: %s; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10; %s",
                gradient, borderColor, scale
        ));
    }

    private VBox createHUD() {
        VBox box = new VBox(12);
        box.setPrefWidth(PANEL_WIDTH);
        box.setPrefHeight(PANEL_HEIGHT);
        box.setMaxHeight(PANEL_HEIGHT);
        box.setAlignment(Pos.TOP_CENTER);
        box.setPadding(new Insets(18, 16, 16, 16));
        box.setStyle(createHUDStyle());

        Label title = createTitle();
        Rectangle topLine = createDecorativeLine();
        Region spacer = createSpacer(5);

        VBox livesBox = createStatBox("LIVES", livesValue, CYAN_COLOR, ICON_LIVES);
        VBox scoreBox = createStatBox("SCORE", scoreValue, WHITE_COLOR, ICON_SCORE);
        VBox timeBox = createStatBox("TIME", timeValue, ORANGE_COLOR, ICON_TIME);

        Separator separator = createSeparator();

        VBox buffBox = createEffectBox("BUFF", buffValue, GREEN_COLOR, ICON_BUFF);
        VBox debuffBox = createEffectBox("DEBUFF", debuffValue, RED_COLOR, ICON_DEBUFF);

        box.getChildren().addAll(
                title, topLine, spacer, pauseButton,
                createSpacer(8), livesBox, scoreBox, timeBox,
                separator, buffBox, debuffBox
        );

        return box;
    }

    private String createHUDStyle() {
        return "-fx-background-color: " +
                "linear-gradient(to bottom, rgba(30,30,40,0.98), rgba(15,15,25,0.98)); " +
                "-fx-background-radius: 12; " +
                "-fx-border-color: linear-gradient(to bottom, " +
                "rgba(100,100,120,0.5), rgba(60,60,80,0.5)); " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 15, 0, 0, 5);";
    }

    private Label createTitle() {
        Label title = new Label("STATUS");
        title.setFont(Font.font("System", FontWeight.BOLD, 26));
        title.setTextFill(Color.web(GOLD_COLOR));
        title.setEffect(titleGlow);
        return title;
    }

    private Rectangle createDecorativeLine() {
        Rectangle line = new Rectangle(160, 2);
        line.setFill(Color.web(GOLD_COLOR, 0.6));
        line.setArcWidth(2);
        line.setArcHeight(2);
        return line;
    }

    private Separator createSeparator() {
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: rgba(255,255,255,0.2);");
        sep.setPrefWidth(180);
        return sep;
    }

    private VBox createStatBox(String labelText, Label valueLabel,
                               String color, String icon) {
        VBox container = new VBox(4);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(6, 8, 6, 8));
        container.setStyle(createStatBoxStyle());

        HBox headerBox = createStatHeader(labelText, color, icon);
        configureValueLabel(valueLabel, color, 18);

        container.getChildren().addAll(headerBox, valueLabel);
        return container;
    }

    private String createStatBoxStyle() {
        return "-fx-background-color: rgba(255,255,255,0.05); " +
                "-fx-background-radius: 8; " +
                "-fx-border-color: rgba(255,255,255,0.1); " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 8;";
    }

    private HBox createStatHeader(String labelText, String color, String icon) {
        HBox headerBox = new HBox(6);
        headerBox.setAlignment(Pos.CENTER);

        Label iconLabel = createIconLabel(icon, color, 14);
        Label textLabel = createTextLabel(labelText, color, 12, 0.8);

        headerBox.getChildren().addAll(iconLabel, textLabel);
        return headerBox;
    }

    private VBox createEffectBox(String labelText, Label valueLabel,
                                 String color, String icon) {
        VBox container = new VBox(3);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(5, 10, 5, 10));

        HBox headerBox = createEffectHeader(labelText, color, icon);
        configureValueLabel(valueLabel, color, 14, 0.95);

        container.getChildren().addAll(headerBox, valueLabel);
        return container;
    }

    private HBox createEffectHeader(String labelText, String color, String icon) {
        HBox headerBox = new HBox(5);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label iconLabel = createIconLabel(icon, color, 12);
        Label textLabel = createTextLabel(labelText, color, 11, 0.9);

        headerBox.getChildren().addAll(iconLabel, textLabel);
        return headerBox;
    }

    private Label createIconLabel(String icon, String color, int size) {
        Label label = new Label(icon);
        label.setFont(Font.font("System", size));
        label.setTextFill(Color.web(color));
        return label;
    }

    private Label createTextLabel(String text, String color, int size, double opacity) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.BOLD, size));
        label.setTextFill(Color.web(color, opacity));
        return label;
    }

    private void configureValueLabel(Label label, String color, int size) {
        configureValueLabel(label, color, size, 1.0);
    }

    private void configureValueLabel(Label label, String color, int size, double opacity) {
        label.setFont(Font.font("System", FontWeight.BOLD, size));
        label.setTextFill(Color.web(color, opacity));

        DropShadow glow = new DropShadow();
        glow.setRadius(6);
        glow.setColor(Color.web(color, 0.5));
        label.setEffect(glow);
    }

    private Region createSpacer(double height) {
        Region spacer = new Region();
        spacer.setPrefHeight(height);
        return spacer;
    }

    // Public Update Methods

    public void updateLives(int lives) {
        livesValue.setText(String.valueOf(lives));
    }

    public void updateScore(int score) {
        scoreValue.setText(String.valueOf(score));
    }

    public void updateTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        timeValue.setText(String.format("%02d:%02d", minutes, secs));
    }

    public void updateBuff(String buffText, int buffTime) {
        buffValue.setText(formatEffectText(buffText, buffTime));
    }

    public void updateDebuff(String debuffText, int debuffTime) {
        debuffValue.setText(formatEffectText(debuffText, debuffTime));
    }

    private String formatEffectText(String text, int time) {
        return text + (time > 0 ? " (" + time + "s)" : "");
    }

    public void updateAll(int lives, int score, int seconds,
                          String buffText, int buffTime,
                          String debuffText, int debuffTime) {
        updateLives(lives);
        updateScore(score);
        updateTime(seconds);
        updateBuff(buffText, buffTime);
        updateDebuff(debuffText, debuffTime);
    }

    public void setPauseButtonState(boolean isPaused) {
        if (isPaused) {
            pauseButton.setText("▶  RESUME");
            applyResumeButtonStyle();
        } else {
            pauseButton.setText("⏸  PAUSE");
            applyPauseButtonStyle(pauseButton, false, false);
        }
    }

    private void applyResumeButtonStyle() {
        pauseButton.setStyle(
                "-fx-background-color: linear-gradient(to bottom, " + RESUME_GRADIENT + "); " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 10 20; " +
                        "-fx-cursor: hand; " +
                        "-fx-border-color: " + RESUME_BORDER + "; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10;"
        );

        DropShadow resumeGlow = new DropShadow();
        resumeGlow.setRadius(12);
        resumeGlow.setColor(Color.web("#FF9800", 0.6));
        pauseButton.setEffect(resumeGlow);
    }

    public void setOnPauseToggle(Runnable callback) {
        this.onPauseToggle = callback;
    }

    public VBox getHudBox() {
        return hudBox;
    }
}