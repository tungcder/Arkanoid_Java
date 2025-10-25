package uet.oop.arkanoidgame;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import uet.oop.arkanoidgame.entities.ball.Ball;
import uet.oop.arkanoidgame.entities.brick.BrickGrid;
import uet.oop.arkanoidgame.entities.item.Item;
import uet.oop.arkanoidgame.entities.map.MapManager;
import uet.oop.arkanoidgame.entities.menu.MainMenu;
import uet.oop.arkanoidgame.entities.paddle.Paddle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class quản lý game play chính
 */
public class GamePanel {
    private final Stage stage;
    private final MapManager mapManager;

    private final Canvas canvas;
    private final GraphicsContext gc;

    private Paddle paddle;
    private Ball ball;
    private BrickGrid bricks;
    private boolean gameRunning = true;
    private boolean gamePaused = false;
    private AnimationTimer timer;

    private final List<Item> items = new ArrayList<>();
    public static int playerLives = 3;
    private int score = 0;
    private String buffText = "None";
    private String debuffText = "None";
    private int buffTime = 0;
    private int debuffTime = 0;

    // Thời gian chơi
    private long startTime = 0;
    private long pausedTime = 0;
    private long lastPauseTime = 0;
    private int elapsedSeconds = 0;

    // UI Components
    private final Pane rootPane;
    private final GameStatusPanel statusPanel;
    private StackPane pauseOverlay;

    private final DropShadow titleGlow = new DropShadow();

    private final Image gameBackground = new Image(getClass().getResource(
            "/uet/oop/arkanoidgame/entities/menu/menu_images/game_bg2.jpg"
    ).toExternalForm());

    public GamePanel(Stage stage) {
        this.stage = stage;
        this.mapManager = new MapManager();

        // Khởi tạo canvas
        canvas = new Canvas(600, 600);
        gc = canvas.getGraphicsContext2D();

        // Khởi tạo Status Panel
        statusPanel = new GameStatusPanel();
        statusPanel.setOnPauseToggle(this::togglePause);

        // Setup title glow effect cho pause overlay
        titleGlow.setRadius(18);
        titleGlow.setColor(Color.web("#FFD700", 0.9));

        // Layout chính
        HBox mainLayout = new HBox();
        mainLayout.setSpacing(8);
        mainLayout.setPadding(new Insets(0));
        mainLayout.setStyle("-fx-background-color: black;");

        canvas.setWidth(600);
        canvas.setHeight(600);
        HBox.setHgrow(canvas, Priority.NEVER);
        HBox.setHgrow(statusPanel.getHudBox(), Priority.NEVER);

        mainLayout.getChildren().addAll(canvas, statusPanel.getHudBox());
        mainLayout.setAlignment(Pos.CENTER_LEFT);

        rootPane = new StackPane(mainLayout);
        stage.setScene(new Scene(rootPane, 800, 600));

        initEntities();
        initInput();
    }

    /**
     * Khởi tạo các entities (paddle, ball, bricks)
     */
    private void initEntities() {
        paddle = new Paddle(250, 550, 140, 40);
        ball = new Ball(290, 500, 15);
        bricks = new BrickGrid("src/main/resources/Levels/Map1.csv");
        mapManager.loadLevel(bricks);
        ball.attachToPaddle(paddle);
    }

    /**
     * Khởi tạo input handling (keyboard, mouse)
     */
    private void initInput() {
        canvas.setFocusTraversable(true);
        canvas.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                togglePause();
            } else {
                paddle.addKey(e.getCode());
            }
        });
        canvas.setOnKeyReleased(e -> paddle.removeKey(e.getCode()));
        canvas.setOnMouseMoved(paddle::handleMouseMove);
        canvas.setOnMouseDragged(paddle::handleMouseMove);
        canvas.setOnMouseClicked(e -> {
            if (ball.isAttachedToPaddle() && !gamePaused) {
                ball.releaseFromPaddle();
            }
        });
    }

    /**
     * Toggle pause/resume game
     */
    private void togglePause() {
        gamePaused = !gamePaused;

        if (gamePaused) {
            lastPauseTime = System.nanoTime();
            statusPanel.setPauseButtonState(true);
            showPauseOverlay();
        } else {
            pausedTime += (System.nanoTime() - lastPauseTime);
            statusPanel.setPauseButtonState(false);
            hidePauseOverlay();
        }
    }

    /**
     * Hiển thị overlay khi pause
     */
    private void showPauseOverlay() {
        pauseOverlay = new StackPane();
        pauseOverlay.setPrefSize(600, 600);
        pauseOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        VBox pauseBox = new VBox(20);
        pauseBox.setAlignment(Pos.CENTER);

        Label pauseLabel = new Label("GAME PAUSED");
        pauseLabel.setFont(Font.font("System", FontWeight.BOLD, 40));
        pauseLabel.setTextFill(Color.web("#FFD700"));
        pauseLabel.setEffect(titleGlow);

        Label infoLabel = new Label("Press SPACE to continue");
        infoLabel.setFont(Font.font("System", 16));
        infoLabel.setTextFill(Color.WHITE);

        pauseBox.getChildren().addAll(pauseLabel, infoLabel);
        pauseOverlay.getChildren().add(pauseBox);

        if (!rootPane.getChildren().contains(pauseOverlay)) {
            rootPane.getChildren().add(pauseOverlay);
        }
    }

    /**
     * Ẩn overlay pause
     */
    private void hidePauseOverlay() {
        if (pauseOverlay != null) {
            rootPane.getChildren().remove(pauseOverlay);
        }
    }

    /**
     * Cập nhật HUD (Status Panel)
     */
    private void updateHUD() {
        statusPanel.updateAll(
                playerLives,
                score,
                elapsedSeconds,
                buffText,
                buffTime,
                debuffText,
                debuffTime
        );
    }

    /**
     * Bắt đầu game loop
     */
    public void startGame() {
        startTime = System.nanoTime();
        pausedTime = 0;

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (gameRunning) {
                    if (!gamePaused) {
                        long actualPlayTime = now - startTime - pausedTime;
                        elapsedSeconds = (int) (actualPlayTime / 1_000_000_000);

                        update();
                        render();
                    }
                } else {
                    stop();
                    showGameOverScreen();
                }
            }
        };
        timer.start();
    }

    /**
     * Update game logic
     */
    private void update() {
        ball.update(paddle, canvas.getWidth(), canvas.getHeight());
        paddle.update();
        ball.checkCollision(paddle);
        Item spawned = ball.checkCollision(bricks);
        if (spawned != null) items.add(spawned);

        Iterator<Item> iter = items.iterator();
        while (iter.hasNext()) {
            Item item = iter.next();
            item.update();
            if (item.getY() > canvas.getHeight()) {
                iter.remove();
            } else if (item.collidesWith(paddle)) {
                item.apply(paddle, ball);
                iter.remove();
                score += 100;
            }
        }

        if (ball.isOutOfScreen(canvas.getHeight())) {
            playerLives--;
            if (playerLives > 0) {
                ball.attachToPaddle(paddle);
            } else {
                gameRunning = false;
            }
        }

        if (bricks.isLevelComplete()) {
            if (mapManager.hasNextLevel()) {
                mapManager.nextLevel(bricks);
                resetForNextLevel();
            } else {
                gameRunning = false;
                timer.stop();
                showGameCompleteScreen();
            }
        }

        updateHUD();
    }

    /**
     * Render tất cả game objects
     */
    private void render() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.drawImage(gameBackground, 0, 0, canvas.getWidth(), canvas.getHeight());
        bricks.render(gc);
        paddle.render(gc);
        ball.render(gc);
        for (Item item : items) {
            item.render(gc);
        }
    }

    /**
     * Reset game cho level tiếp theo
     */
    private void resetForNextLevel() {
        paddle = new Paddle(250, 550, 140, 40);
        ball = new Ball(290, 500, 15);
        ball.attachToPaddle(paddle);
        items.clear();
        initInput();
    }

    /**
     * Hiển thị màn hình Game Over
     */
    private void showGameOverScreen() {
        GameOverScreen gameOverScreen = new GameOverScreen(score, () -> {
            mapManager.resetGame();
            playerLives = 3;
            MainMenu menu = new MainMenu(stage);
            stage.setScene(new Scene(menu, 800, 600));
        });

        rootPane.getChildren().add(gameOverScreen);
    }

    /**
     * Hiển thị màn hình chiến thắng
     */
    private void showGameCompleteScreen() {
        GameCompleteScreen completeScreen = new GameCompleteScreen(
                score,
                elapsedSeconds,
                () -> {
                    mapManager.resetGame();
                    playerLives = 3;
                    MainMenu menu = new MainMenu(stage);
                    stage.setScene(new Scene(menu, 800, 600));
                }
        );

        rootPane.getChildren().add(completeScreen);
    }

    /**
     * Thêm mạng cho player
     */
    public static void addLives(int n) {
        playerLives += n;
        if (playerLives > 5) {
            playerLives = 5;
        }
        System.out.println("Player gained " + n + " life(s)! Current lives: " + playerLives);
    }
}