package uet.oop.arkanoidgame;

import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import uet.oop.arkanoidgame.entities.ball.Ball;
import uet.oop.arkanoidgame.entities.brick.BrickGrid;
import uet.oop.arkanoidgame.entities.map.MapManager;
import uet.oop.arkanoidgame.entities.paddle.Paddle;
import uet.oop.arkanoidgame.entities.menu.MainMenu;
import uet.oop.arkanoidgame.entities.item.Item;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class GamePanel extends Canvas {

    private Paddle paddle;
    private Ball ball;
    private BrickGrid bricks;
    private boolean gameRunning = true;
    private AnimationTimer timer;
    private final Stage stage;
    private final MapManager mapManager;
    private final Pane gamePane;
    public static int playerLives = 3;
    private final List<Item> items = new ArrayList<>();

    public GamePanel(Stage stage) {
        super(800, 600);
        this.stage = stage;
        this.mapManager = new MapManager();

        gamePane = new Pane();
        gamePane.getChildren().add(this);

        // Init entities
        paddle = new Paddle(350, 550, 140, 40);
        ball = new Ball(390, 500, 15);
        bricks = new BrickGrid("src/main/resources/Levels/Map1.csv");
        mapManager.loadLevel(bricks);

        // Ball starts attached to paddle
        ball.attachToPaddle(paddle);

        // Input
        setFocusTraversable(true);
        setOnKeyPressed(e -> paddle.addKey(e.getCode()));
        setOnKeyReleased(e -> paddle.removeKey(e.getCode()));
        this.setOnMouseMoved(paddle::handleMouseMove);
        this.setOnMouseDragged(paddle::handleMouseMove);

        // Click to release ball
        this.setOnMouseClicked(e -> {
            if (ball.isAttachedToPaddle()) {
                ball.releaseFromPaddle();
            }
        });

        stage.setScene(new Scene(gamePane, 800, 600));
    }

    public static void addLives(int n) {
        playerLives += n;
        if (playerLives > 5) playerLives = 5;
        System.out.println("Player gained " + n + " life(s)! Current lives: " + playerLives);
    }

    public void startGame() {
        GraphicsContext gc = getGraphicsContext2D();

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (gameRunning) {
                    update();
                    render(gc);

                    // Level progression
                    if (bricks.isLevelComplete() && mapManager.hasNextLevel()) {
                        mapManager.nextLevel(bricks);
                        resetForNextLevel();
                    } else if (bricks.isLevelComplete()) {
                        gameRunning = false;
                        showGameCompleteScreen();
                    }
                } else {
                    stop();
                    showGameOverScreen();
                }
            }
        };
        timer.start();
    }

    private void update() {
        // Update theo cơ chế dính/thả bóng
        ball.update(paddle, getWidth(), getHeight());
        paddle.update();

        // Va chạm
        ball.checkCollision(paddle);
        Item spawned = ball.checkCollision(bricks);
        if (spawned != null) items.add(spawned);

        // Update items
        Iterator<Item> iter = items.iterator();
        while (iter.hasNext()) {
            Item item = iter.next();
            item.update();
            if (item.getY() > getHeight()) {
                iter.remove();
            } else if (item.collidesWith(paddle)) {
                item.apply(paddle, ball);
                iter.remove();
            }
        }

        // Mất bóng → trừ mạng & respawn dính paddle
        if (ball.isOutOfScreen(getHeight())) {
            playerLives--;
            System.out.println("💔 Lost a life! Lives left: " + playerLives);

            if (playerLives > 0) {
                // Gắn lại bóng vào paddle (đợi người chơi click để thả)
                ball.attachToPaddle(paddle);
                // (tuỳ chọn) xoá buff tốc độ/nổ nếu muốn cân bằng:
                // ball.applySpeedBuff(1.0, 0);
                // ball.applyExplosiveBuff(0, 0);
            } else {
                gameRunning = false;
            }
        }
    }

    private void render(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, getWidth(), getHeight());

        bricks.render(gc);
        paddle.render(gc);
        ball.render(gc);

        // Render items
        for (Item item : items) {
            item.render(gc);
        }

        // (tuỳ chọn) Vẽ số mạng còn lại
        gc.setFill(Color.WHITE);
        gc.fillText("Lives: " + playerLives, 10, 18);
    }

    /** Reset khi qua màn mới: giữ mạng, giữ paddle; bóng dính lại. */
    private void resetForNextLevel() {
        // Đặt lại vị trí hợp lý và gắn bóng
        paddle = new Paddle(350, 550, 140, 40);
        ball = new Ball(390, 500, 15);
        ball.attachToPaddle(paddle);

        // Bind input lại cho paddle mới
        setOnKeyPressed(e -> paddle.addKey(e.getCode()));
        setOnKeyReleased(e -> paddle.removeKey(e.getCode()));
        this.setOnMouseMoved(paddle::handleMouseMove);
        this.setOnMouseDragged(paddle::handleMouseMove);

        // Click lại để thả bóng
        this.setOnMouseClicked(e -> {
            if (ball.isAttachedToPaddle()) ball.releaseFromPaddle();
        });

        // Xoá item đang rơi (cho gọn)
        items.clear();
    }

    /** (Giữ lại nếu muốn reset cả 2 entity giữa chừng) */
    @SuppressWarnings("unused")
    private void resetBallAndPaddle() {
        ball = new Ball(390, 300, 15);
        paddle = new Paddle(350, 550, 140, 40);
        ball.attachToPaddle(paddle);

        setOnKeyPressed(e -> paddle.addKey(e.getCode()));
        setOnKeyReleased(e -> paddle.removeKey(e.getCode()));
        this.setOnMouseMoved(paddle::handleMouseMove);
        this.setOnMouseDragged(paddle::handleMouseMove);
        this.setOnMouseClicked(e -> {
            if (ball.isAttachedToPaddle()) ball.releaseFromPaddle();
        });
    }

    private void showGameOverScreen() {
        StackPane overlay = new StackPane();
        overlay.setPrefSize(800, 600);

        Image image = new Image(Objects.requireNonNull(
                getClass().getResource("/uet/oop/arkanoidgame/entities/menu/menu_images/game_over.png"),
                "Missing resource: game_over.png"
        ).toExternalForm());
        ImageView gameOverView = new ImageView(image);
        gameOverView.setFitWidth(800);
        gameOverView.setFitHeight(600);
        gameOverView.setPreserveRatio(false);

        Button backToMenu = new Button("Back to Menu");
        backToMenu.setStyle(
                "-fx-font-size: 22px; " +
                        "-fx-background-color: #ffcc00; " +
                        "-fx-text-fill: black; " +
                        "-fx-background-radius: 10;"
        );

        backToMenu.setOnMouseEntered(e ->
                backToMenu.setStyle("-fx-font-size: 22px; -fx-background-color: #ffaa00; -fx-text-fill: white; -fx-background-radius: 10;")
        );
        backToMenu.setOnMouseExited(e ->
                backToMenu.setStyle("-fx-font-size: 22px; -fx-background-color: #ffcc00; -fx-text-fill: black; -fx-background-radius: 10;")
        );

        backToMenu.setOnAction(e -> {
            mapManager.resetGame();
            MainMenu menu = new MainMenu(stage);
            stage.setScene(new Scene(menu, 800, 600));
        });

        StackPane.setAlignment(backToMenu, Pos.CENTER);
        overlay.getChildren().addAll(gameOverView, backToMenu);

        gamePane.getChildren().add(overlay);
    }

    private void showGameCompleteScreen() {
        StackPane overlay = new StackPane();
        overlay.setPrefSize(800, 600);

        Image image = new Image(Objects.requireNonNull(
                getClass().getResource("/uet/oop/arkanoidgame/entities/menu/menu_images/game_complete.png"),
                "Missing resource: game_complete.png"
        ).toExternalForm());
        ImageView gameCompleteView = new ImageView(image);
        gameCompleteView.setFitWidth(800);
        gameCompleteView.setFitHeight(600);
        gameCompleteView.setPreserveRatio(false);

        Button backToMenu = new Button("Back to Menu");
        backToMenu.setStyle(
                "-fx-font-size: 22px; " +
                        "-fx-background-color: #ffcc00; " +
                        "-fx-text-fill: black; " +
                        "-fx-background-radius: 10;"
        );

        backToMenu.setOnMouseEntered(e ->
                backToMenu.setStyle("-fx-font-size: 22px; -fx-background-color: #ffaa00; -fx-text-fill: white; -fx-background-radius: 10;")
        );
        backToMenu.setOnMouseExited(e ->
                backToMenu.setStyle("-fx-font-size: 22px; -fx-background-color: #ffcc00; -fx-text-fill: black; -fx-background-radius: 10;")
        );

        backToMenu.setOnAction(e -> {
            mapManager.resetGame();
            MainMenu menu = new MainMenu(stage);
            stage.setScene(new Scene(menu, 800, 600));
        });

        StackPane.setAlignment(backToMenu, Pos.CENTER);
        overlay.getChildren().addAll(gameCompleteView, backToMenu);

        gamePane.getChildren().add(overlay);
    }
}
