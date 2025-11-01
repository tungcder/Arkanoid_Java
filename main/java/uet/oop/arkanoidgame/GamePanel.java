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
import uet.oop.arkanoidgame.entities.data.GameSaveManager;
import uet.oop.arkanoidgame.entities.data.GameState;
import uet.oop.arkanoidgame.entities.data.Score;
import uet.oop.arkanoidgame.entities.map.MapManager;
import uet.oop.arkanoidgame.entities.paddle.Paddle;
import uet.oop.arkanoidgame.entities.menu.MainMenu;
import uet.oop.arkanoidgame.entities.item.Item;
import uet.oop.arkanoidgame.ThemeManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GamePanel {
    private static final int CANVAS_WIDTH = 600;
    private static final int CANVAS_HEIGHT = 600;
    private static final int SCENE_WIDTH = 800;
    private static final int SCENE_HEIGHT = 600;

    private static final int INITIAL_LIVES = 3;
    private static final int MAX_LIVES = 5;
    private static final int ITEM_SCORE_BONUS = 100;
    private static final long NANOS_PER_SECOND = 1_000_000_000L;

    private static final int PADDLE_X = 250;
    private static final int PADDLE_Y = 550;
    private static final int PADDLE_WIDTH = 140;
    private static final int PADDLE_HEIGHT = 40;
    private static final int BALL_X = 290;
    private static final int BALL_Y = 500;
    private static final int BALL_RADIUS = 15;

    // Constants - Paths
    private static final String BACKGROUND_ASSET_PATH = "background/Background.jpg";
    private static final String INITIAL_MAP_PATH = "/Levels/Map1.csv";

    private static final String GOLD_COLOR = "#FFD700";

    private final Stage stage;
    private final MapManager mapManager;
    private AnimationTimer timer;
    private boolean gameRunning = true;
    private boolean gamePaused = false;

    private Paddle paddle;
    private Ball ball;
    private BrickGrid bricks;
    private final List<Item> items = new ArrayList<>();

    public static int playerLives = INITIAL_LIVES;
    private int score = 0;
    private String buffText = "None";
    private String debuffText = "None";
    private int buffTime = 0;
    private int debuffTime = 0;

    private long startTime = 0;
    private long pausedTime = 0;
    private long lastPauseTime = 0;
    private int elapsedSeconds = 0;

    // Biến lưu tổng thời gian đã chơi thực tế (tính bằng giây)
    private int totalPlayedSeconds = 0;

    private final Canvas canvas;
    private final GraphicsContext gc;
    private final Pane rootPane;
    private final GameStatusPanel statusPanel;
    private StackPane pauseOverlay;
    private final Image gameBackground;
    private final DropShadow titleGlow;

    private Score scoreManager;
    private final SoundManager soundManager;

    // Biến để theo dõi xem game đã kết thúc chưa (game over hoặc complete)
    private boolean gameEnded = false;

    // Constructor mặc định (không load save)
    public GamePanel(Stage stage, SoundManager soundManager) {
        this(stage, soundManager, false);
    }

    // Constructor đầy đủ với tùy chọn load save
    public GamePanel(Stage stage, SoundManager soundManager, boolean loadSavedGame) {
        this.stage = stage;
        this.mapManager = new MapManager();
        this.soundManager = soundManager;

        this.canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        this.gc = canvas.getGraphicsContext2D();
        this.gameBackground = loadBackgroundImage();
        this.titleGlow = createTitleGlow();

        this.statusPanel = new GameStatusPanel();
        this.statusPanel.setOnPauseToggle(this::togglePause);
        this.statusPanel.setOnExitGame(this::exitToMenu);

        this.rootPane = createMainLayout();
        Scene gameScene = new Scene(rootPane, SCENE_WIDTH, SCENE_HEIGHT);
        stage.setScene(gameScene);

        // Thiết lập xử lý khi đóng cửa sổ (nút X)
        setupWindowCloseHandler();

        if (loadSavedGame) {
            loadGameState();
        } else {
            initEntities();
        }

        initInput();
    }

    /**
     * Thiết lập xử lý khi người dùng ấn nút X để đóng cửa sổ
     */
    private void setupWindowCloseHandler() {
        stage.setOnCloseRequest(event -> {
            // Nếu game đang chạy và chưa kết thúc, lưu trạng thái
            if (gameRunning && !gameEnded) {
                // Tính toán thời gian đã chơi trước khi lưu
                calculateTotalPlayedTime();
                saveCurrentGameState();
                System.out.println("✓ Game đã được lưu tự động khi đóng cửa sổ");
            } else if (gameEnded) {
                // Nếu game đã kết thúc (game over hoặc complete), xóa save
                GameSaveManager.deleteSave();
                System.out.println("✓ Game đã kết thúc, file save đã bị xóa");
            }

            // Dừng timer nếu đang chạy
            if (timer != null) {
                timer.stop();
            }

            // Cho phép đóng cửa sổ hoàn toàn (không dùng event.consume())
        });
    }

    private Image loadBackgroundImage() {
        String themedPath = ThemeManager.getImagePath(BACKGROUND_ASSET_PATH);
        return new Image(getClass().getResource(themedPath).toExternalForm());
    }

    private DropShadow createTitleGlow() {
        DropShadow glow = new DropShadow();
        glow.setRadius(18);
        glow.setColor(Color.web(GOLD_COLOR, 0.9));
        return glow;
    }

    private Pane createMainLayout() {
        VBox gameArea = new VBox();
        gameArea.setPrefSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        gameArea.setMaxSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        gameArea.setStyle(
                "-fx-border-color: #00FFFF, #00FFFF;" +
                        "-fx-border-width: 3px, 1px;" +
                        "-fx-border-insets: 0, 6px;" +
                        "-fx-border-style: solid, solid;" +
                        "-fx-effect: dropshadow(gaussian, #00FFFF, 20, 0.9, 0, 0);" +
                        "-fx-background-color: transparent;"
        );

        canvas.setWidth(CANVAS_WIDTH);
        canvas.setHeight(CANVAS_HEIGHT);
        gameArea.getChildren().add(canvas);

        HBox mainLayout = new HBox();
        mainLayout.setSpacing(8);
        mainLayout.setPadding(new Insets(0));
        mainLayout.setStyle("-fx-background-color: black;");

        HBox.setHgrow(gameArea, Priority.NEVER);
        HBox.setHgrow(statusPanel.getHudBox(), Priority.NEVER);

        mainLayout.getChildren().addAll(gameArea, statusPanel.getHudBox());
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
        return new Ball(BALL_X, BALL_Y, BALL_RADIUS, this.soundManager);
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
            paddle.handleHit();
            soundManager.playSfx("PaddleHit");
            ball.releaseFromPaddle();
        }
    }

    public void startGame() {
        initializeGameTime();
        if (scoreManager == null) {
            scoreManager = new Score();
        }
        scoreManager.startNewGame();
        soundManager.playMusic("GameRun", true);

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
        // Nếu load game, startTime được điều chỉnh để elapsedSeconds khớp với totalPlayedSeconds
        if (totalPlayedSeconds > 0) {
            startTime = System.nanoTime() - (totalPlayedSeconds * NANOS_PER_SECOND);
        } else {
            startTime = System.nanoTime();
        }
        pausedTime = 0;
    }

    private void updateGameTime(long now) {
        long actualPlayTime = now - startTime - pausedTime;
        elapsedSeconds = (int) (actualPlayTime / NANOS_PER_SECOND);
    }

    /**
     * Tính toán tổng thời gian đã chơi thực tế
     */
    private void calculateTotalPlayedTime() {
        long now = System.nanoTime();
        long actualPlayTime = now - startTime - pausedTime;
        totalPlayedSeconds = (int) (actualPlayTime / NANOS_PER_SECOND);
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
        soundManager.playSfx("LevelClear");
        mapManager.nextLevel(bricks);
        resetForNextLevel();
    }

    private void completeGame() {
        gameRunning = false;
        gameEnded = true;
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

    private void resetForNextLevel() {
        paddle = createPaddle();
        ball = createBall();
        ball.attachToPaddle(paddle);
        items.clear();
        initInput();
    }

    /**
     * Lưu trạng thái game hiện tại
     */
    private void saveCurrentGameState() {
        try {
            GameState state = new GameState();

            // Lưu thông tin cơ bản
            state.setScore(score);
            state.setLives(playerLives);
            state.setElapsedSeconds(totalPlayedSeconds);
            state.setCurrentLevel(mapManager.getCurrentLevelIndex());

            // Lưu thông tin paddle - Quan trọng!
            state.setPaddleX(paddle.getX());
            state.setPaddleY(paddle.getY());
            state.setPaddleWidth((int) paddle.getWidth());
            state.setPaddleHeight((int) paddle.getHeight());

            // Lưu thông tin ball - Quan trọng!
            state.setBallX(ball.getX());
            state.setBallY(ball.getY());
            state.setBallRadius((int) ball.getRadius());
            state.setBallSpeedX(ball.getSpeedX());
            state.setBallSpeedY(ball.getSpeedY());
            state.setBallAttached(ball.isAttachedToPaddle());

            // Lưu buff/debuff
            state.setBuffText(buffText);
            state.setBuffTime(buffTime);
            state.setDebuffText(debuffText);
            state.setDebuffTime(debuffTime);

            // Lưu thông tin map
            state.setCurrentMapPath(mapManager.getCurrentMapPath());
            state.setActiveBrickCount(bricks.getActiveBrickCount());

            // Lưu trạng thái bricks - CỰC KỲ QUAN TRỌNG!
            state.setBricksState(bricks.getBricksStateForSave());

            // Lưu timestamp
            state.setSavedTimestamp(System.currentTimeMillis());

            GameSaveManager.saveGame(state);

            System.out.println("========== GAME SAVED ==========");
            System.out.println("Score: " + score + " | Lives: " + playerLives + " | Time: " + totalPlayedSeconds + "s");
            System.out.println("Ball: (" + ball.getX() + ", " + ball.getY() + ") | Speed: (" + ball.getSpeedX() + ", " + ball.getSpeedY() + ")");
            System.out.println("Paddle: (" + paddle.getX() + ", " + paddle.getY() + ") | Size: " + paddle.getWidth() + "x" + paddle.getHeight());
            System.out.println("Ball attached: " + ball.isAttachedToPaddle());
            System.out.println("Active bricks: " + bricks.getActiveBrickCount());
            System.out.println("================================");
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi lưu game: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Tải trạng thái game đã lưu
     */
    /**
     * Tải trạng thái game đã lưu
     */
    private void loadGameState() {
        try {
            GameState state = GameSaveManager.loadGame();
            if (state == null) {
                System.out.println("⚠ Không tìm thấy save game, bắt đầu game mới");
                initEntities();
                return;
            }

            // Khôi phục thông tin cơ bản
            score = state.getScore();
            playerLives = state.getLives();
            totalPlayedSeconds = state.getElapsedSeconds();
            elapsedSeconds = totalPlayedSeconds;

            // Khôi phục paddle với ĐÚNG vị trí và kích thước đã lưu
            paddle = new Paddle(
                    (int) state.getPaddleX(),
                    (int) state.getPaddleY(),
                    state.getPaddleWidth(),
                    state.getPaddleHeight()
            );

            // ✅ FIX: Khôi phục ball đúng cách
            // Bước 1: Tạo ball với constructor chuẩn (để init các biến internal)
            ball = new Ball(BALL_X, BALL_Y, state.getBallRadius(), this.soundManager);

            // Bước 2: Set vị trí thực tế từ save file
            ball.setX(state.getBallX());
            ball.setY(state.getBallY());

            // Bước 3: Set tốc độ
            ball.setSpeedX(state.getBallSpeedX());
            ball.setSpeedY(state.getBallSpeedY());

            // Bước 4: Khôi phục trạng thái attached
            if (state.isBallAttached()) {
                ball.attachToPaddle(paddle);
            }

            // Khôi phục buff/debuff
            buffText = state.getBuffText();
            buffTime = state.getBuffTime();
            debuffText = state.getDebuffText();
            debuffTime = state.getDebuffTime();

            // Khôi phục bricks - LOAD TRẠNG THÁI BRICKS
            bricks = new BrickGrid(state.getCurrentMapPath());
            mapManager.loadLevelByIndex(bricks, state.getCurrentLevel());

            // Khôi phục trạng thái từng viên gạch (brick nào vỡ, brick nào còn)
            if (state.getBricksState() != null && !state.getBricksState().isEmpty()) {
                bricks.restoreBricksState(state.getBricksState());
            }

            // Khởi tạo scoreManager
            scoreManager = new Score();

            items.clear();

            System.out.println("========== GAME LOADED ==========");
            System.out.println("Score: " + score + " | Lives: " + playerLives + " | Level: " + state.getCurrentLevel() + " | Time: " + totalPlayedSeconds + "s");
            System.out.println("Ball: (" + ball.getX() + ", " + ball.getY() + ") | Speed: (" + ball.getSpeedX() + ", " + ball.getSpeedY() + ")");
            System.out.println("Paddle: (" + paddle.getX() + ", " + paddle.getY() + ") | Size: " + paddle.getWidth() + "x" + paddle.getHeight());
            System.out.println("Ball attached: " + ball.isAttachedToPaddle());
            System.out.println("Active bricks: " + bricks.getActiveBrickCount());
            System.out.println("=================================");
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi load game: " + e.getMessage());
            e.printStackTrace();
            initEntities();
        }
    }

    private void exitToMenu() {
        // Lưu game trước khi thoát (nếu game đang chạy và chưa kết thúc)
        if (gameRunning && !gameEnded) {
            calculateTotalPlayedTime();
            saveCurrentGameState();
        }

        if (timer != null) {
            timer.stop();
        }
        returnToMenu();
    }

    private void showGameOverScreen() {
        gameEnded = true;
        // Xóa save khi game over
        GameSaveManager.deleteSave();

        soundManager.stopMusic(); // Dừng nhạc "GameRun"
        soundManager.playSfx("GameOver");

        GameOverScreen gameOverScreen = new GameOverScreen(score, this::returnToMenu);
        rootPane.getChildren().add(gameOverScreen);
    }

    private void showGameCompleteScreen() {
        gameEnded = true;
        // Xóa save khi hoàn thành game
        GameSaveManager.deleteSave();

        soundManager.stopMusic(); // Dừng nhạc "GameRun"
        soundManager.playMusic("GameClear", false);

        GameCompleteScreen completeScreen = new GameCompleteScreen(
                score,
                elapsedSeconds,
                this::returnToMenu
        );
        rootPane.getChildren().add(completeScreen);
    }

    private void returnToMenu() {
        soundManager.stopMusic();
        resetGame();
        MainMenu menu = new MainMenu(stage, soundManager);
        stage.setScene(new Scene(menu, SCENE_WIDTH, SCENE_HEIGHT));
    }

    private void resetGame() {
        mapManager.resetGame();
        playerLives = INITIAL_LIVES;
        gameEnded = false;
        totalPlayedSeconds = 0;
    }

    public static void addLives(int n) {
        playerLives += n;
        if (playerLives > MAX_LIVES) {
            playerLives = MAX_LIVES;
        }
        System.out.println("Player gained " + n + " life(s)! Current lives: " + playerLives);
    }
}