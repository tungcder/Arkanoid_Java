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
import uet.oop.arkanoidgame.entities.data.Score;
import uet.oop.arkanoidgame.entities.map.MapManager;
import uet.oop.arkanoidgame.entities.paddle.Paddle;
import uet.oop.arkanoidgame.entities.menu.MainMenu;
import uet.oop.arkanoidgame.entities.item.Item;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class quản lý game play chính
 */
public class GamePanel {
    // Constants - Dimensions
    private static final int CANVAS_WIDTH = 600;
    private static final int CANVAS_HEIGHT = 600;
    private static final int SCENE_WIDTH = 800;
    private static final int SCENE_HEIGHT = 600;

    // Constants - Game Settings
    private static final int INITIAL_LIVES = 3;
    private static final int MAX_LIVES = 5;
    private static final int ITEM_SCORE_BONUS = 100;
    private static final long NANOS_PER_SECOND = 1_000_000_000L;

    // Constants - Entity Positions
    private static final int PADDLE_X = 250;
    private static final int PADDLE_Y = 550;
    private static final int PADDLE_WIDTH = 140;
    private static final int PADDLE_HEIGHT = 40;
    private static final int BALL_X = 290;
    private static final int BALL_Y = 500;
    private static final int BALL_RADIUS = 15;

    // Constants - Paths
    private static final String BACKGROUND_PATH =
            "/uet/oop/arkanoidgame/entities/menu/menu_images/game_bg2.jpg";
    private static final String INITIAL_MAP_PATH = "src/main/resources/Levels/Map1.csv";

    // Constants - Colors
    private static final String GOLD_COLOR = "#FFD700";

    // Game State
    private final Stage stage;
    private final MapManager mapManager;
    private AnimationTimer timer;
    private boolean gameRunning = true;
    private boolean gamePaused = false;

    // Game Entities
    private Paddle paddle;
    private Ball ball;
    private BrickGrid bricks;
    private final List<Item> items = new ArrayList<>();

    // Game Stats
    public static int playerLives = INITIAL_LIVES;
    private int score = 0;
    private String buffText = "None";
    private String debuffText = "None";
    private int buffTime = 0;
    private int debuffTime = 0;

    // Time Tracking
    private long startTime = 0;
    private long pausedTime = 0;
    private long lastPauseTime = 0;
    private int elapsedSeconds = 0;

    // UI Components
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final Pane rootPane;
    private final GameStatusPanel statusPanel;
    private StackPane pauseOverlay;
    private final Image gameBackground;
    private final DropShadow titleGlow;

    // Managers
    private Score scoreManager;

    public GamePanel(Stage stage) {
        this.stage = stage;
        this.mapManager = new MapManager();

        // Initialize Graphics
        this.canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        this.gc = canvas.getGraphicsContext2D();
        this.gameBackground = loadBackgroundImage();
        this.titleGlow = createTitleGlow();

        // Initialize UI
        this.statusPanel = new GameStatusPanel();
        this.statusPanel.setOnPauseToggle(this::togglePause);

        // Setup Scene
        this.rootPane = createMainLayout();
        stage.setScene(new Scene(rootPane, SCENE_WIDTH, SCENE_HEIGHT));

        // Initialize Game
        initEntities();
        initInput();
    }

    // ========== Initialization Methods ==========

    private Image loadBackgroundImage() {
        return new Image(getClass().getResource(BACKGROUND_PATH).toExternalForm());
    }

    private DropShadow createTitleGlow() {
        DropShadow glow = new DropShadow();
        glow.setRadius(18);
        glow.setColor(Color.web(GOLD_COLOR, 0.9));
        return glow;
    }

    private Pane createMainLayout() {
        HBox mainLayout = new HBox();
        mainLayout.setSpacing(8);
        mainLayout.setPadding(new Insets(0));
        mainLayout.setStyle("-fx-background-color: black;");

        canvas.setWidth(CANVAS_WIDTH);
        canvas.setHeight(CANVAS_HEIGHT);
        HBox.setHgrow(canvas, Priority.NEVER);
        HBox.setHgrow(statusPanel.getHudBox(), Priority.NEVER);

        mainLayout.getChildren().addAll(canvas, statusPanel.getHudBox());
        mainLayout.setAlignment(Pos.CENTER_LEFT);

        return new StackPane(mainLayout);
    }

    private void initEntities() {
        paddle = createPaddle();
        ball = createBall();
        bricks = loadBricks();
        scoreManager = new Score();

        ball.attachToPaddle(paddle);
        mapManager.loadLevel(bricks);
    }

    private Paddle createPaddle() {
        return new Paddle(PADDLE_X, PADDLE_Y, PADDLE_WIDTH, PADDLE_HEIGHT);
    }

    private Ball createBall() {
        return new Ball(BALL_X, BALL_Y, BALL_RADIUS);
    }

    private BrickGrid loadBricks() {
        return new BrickGrid(INITIAL_MAP_PATH);
    }

    private void initInput() {
        canvas.setFocusTraversable(true);
        setupKeyboardInput();
        setupMouseInput();
    }

    private void setupKeyboardInput() {
        canvas.setOnKeyPressed(e -> handleKeyPress(e.getCode()));
        canvas.setOnKeyReleased(e -> paddle.removeKey(e.getCode()));
    }

    private void handleKeyPress(KeyCode code) {
        if (code == KeyCode.SPACE) {
            togglePause();
        } else {
            paddle.addKey(code);
        }
    }

    private void setupMouseInput() {
        canvas.setOnMouseMoved(paddle::handleMouseMove);
        canvas.setOnMouseDragged(paddle::handleMouseMove);
        canvas.setOnMouseClicked(e -> handleMouseClick());
    }

    private void handleMouseClick() {
        if (ball.isAttachedToPaddle() && !gamePaused) {
            ball.releaseFromPaddle();
        }
    }

    // ========== Game Loop Methods ==========

    public void startGame() {
        initializeGameTime();
        scoreManager.startNewGame();

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!gameRunning) {
                    stop();
                    showGameOverScreen();
                    return;
                }

                if (!gamePaused) {
                    updateGameTime(now);
                    update();
                    render();
                    checkLevelProgression();
                }
            }
        };

        timer.start();
    }

    private void initializeGameTime() {
        startTime = System.nanoTime();
        pausedTime = 0;
    }

    private void updateGameTime(long now) {
        long actualPlayTime = now - startTime - pausedTime;
        elapsedSeconds = (int) (actualPlayTime / NANOS_PER_SECOND);
    }

    private void update() {
        updateEntities();
        updateItems();
        checkBallStatus();
        updateHUD();
    }

    private void updateEntities() {
        bricks.update();
        ball.update(paddle, canvas.getWidth(), canvas.getHeight());
        paddle.update();
        ball.checkCollision(paddle);

        handleBrickCollisions();
    }

    private void handleBrickCollisions() {
        int bricksBefore = bricks.getActiveBrickCount();
        Item spawned = ball.checkCollision(bricks);
        int bricksAfter = bricks.getActiveBrickCount();

        if (bricksBefore > bricksAfter) {
            int bricksBroken = bricksBefore - bricksAfter;
            scoreBrokenBricks(bricksBroken);
        }

        if (spawned != null) {
            items.add(spawned);
        }
    }

    private void scoreBrokenBricks(int count) {
        for (int i = 0; i < count; i++) {
            scoreManager.brickBroken();
        }
    }

    private void updateItems() {
        Iterator<Item> iter = items.iterator();
        while (iter.hasNext()) {
            Item item = iter.next();
            item.update();

            if (isItemOutOfBounds(item)) {
                iter.remove();
            } else if (item.collidesWith(paddle)) {
                applyItem(item);
                iter.remove();
            }
        }
    }

    private boolean isItemOutOfBounds(Item item) {
        return item.getY() > canvas.getHeight();
    }

    private void applyItem(Item item) {
        item.apply(paddle, ball);
        score += ITEM_SCORE_BONUS;
    }

    private void checkBallStatus() {
        if (ball.isOutOfScreen(canvas.getHeight())) {
            handleBallLost();
        }
    }

    private void handleBallLost() {
        playerLives--;
        if (playerLives > 0) {
            ball.attachToPaddle(paddle);
        } else {
            gameRunning = false;
        }
    }

    private void checkLevelProgression() {
        if (bricks.isLevelComplete()) {
            if (mapManager.hasNextLevel()) {
                advanceToNextLevel();
            } else {
                completeGame();
            }
        }
    }

    private void advanceToNextLevel() {
        mapManager.nextLevel(bricks);
        resetForNextLevel();
    }

    private void completeGame() {
        gameRunning = false;
        timer.stop();
        scoreManager.recordGameEnd();
        showGameCompleteScreen();
    }

    private void render() {
        clearCanvas();
        drawBackground();
        drawEntities();
    }

    private void clearCanvas() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void drawBackground() {
        gc.drawImage(gameBackground, 0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void drawEntities() {
        bricks.render(gc);
        paddle.render(gc);
        ball.render(gc);

        for (Item item : items) {
            item.render(gc);
        }
    }

    private void updateHUD() {
        statusPanel.updateAll(
                playerLives, score, elapsedSeconds,
                buffText, buffTime,
                debuffText, debuffTime
        );
    }

    // ========== Pause/Resume Methods ==========

    private void togglePause() {
        gamePaused = !gamePaused;

        if (gamePaused) {
            pauseGame();
        } else {
            resumeGame();
        }
    }

    private void pauseGame() {
        lastPauseTime = System.nanoTime();
        statusPanel.setPauseButtonState(true);
        showPauseOverlay();
    }

    private void resumeGame() {
        pausedTime += (System.nanoTime() - lastPauseTime);
        statusPanel.setPauseButtonState(false);
        hidePauseOverlay();
    }

    private void showPauseOverlay() {
        pauseOverlay = createPauseOverlay();

        if (!rootPane.getChildren().contains(pauseOverlay)) {
            rootPane.getChildren().add(pauseOverlay);
        }
    }

    private StackPane createPauseOverlay() {
        StackPane overlay = new StackPane();
        overlay.setPrefSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        VBox pauseBox = createPauseBox();
        overlay.getChildren().add(pauseBox);

        return overlay;
    }

    private VBox createPauseBox() {
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);

        Label pauseLabel = createPauseLabel();
        Label infoLabel = createInfoLabel();

        box.getChildren().addAll(pauseLabel, infoLabel);
        return box;
    }

    private Label createPauseLabel() {
        Label label = new Label("GAME PAUSED");
        label.setFont(Font.font("System", FontWeight.BOLD, 40));
        label.setTextFill(Color.web(GOLD_COLOR));
        label.setEffect(titleGlow);
        return label;
    }

    private Label createInfoLabel() {
        Label label = new Label("Press SPACE to continue");
        label.setFont(Font.font("System", 16));
        label.setTextFill(Color.WHITE);
        return label;
    }

    private void hidePauseOverlay() {
        if (pauseOverlay != null) {
            rootPane.getChildren().remove(pauseOverlay);
        }
    }

    // ========== Level Management Methods ==========

    private void resetForNextLevel() {
        paddle = createPaddle();
        ball = createBall();
        ball.attachToPaddle(paddle);
        items.clear();
        initInput();
    }

    // ========== Screen Transition Methods ==========

    private void showGameOverScreen() {
        GameOverScreen gameOverScreen = new GameOverScreen(score, this::returnToMenu);
        rootPane.getChildren().add(gameOverScreen);
    }

    private void showGameCompleteScreen() {
        GameCompleteScreen completeScreen = new GameCompleteScreen(
                score,
                elapsedSeconds,
                this::returnToMenu
        );
        rootPane.getChildren().add(completeScreen);
    }

    private void returnToMenu() {
        resetGame();
        MainMenu menu = new MainMenu(stage);
        stage.setScene(new Scene(menu, SCENE_WIDTH, SCENE_HEIGHT));
    }

    private void resetGame() {
        mapManager.resetGame();
        playerLives = INITIAL_LIVES;
    }

    // ========== Static Methods ==========

    public static void addLives(int n) {
        playerLives += n;
        if (playerLives > MAX_LIVES) {
            playerLives = MAX_LIVES;
        }
        System.out.println("Player gained " + n + " life(s)! Current lives: " + playerLives);
    }
}