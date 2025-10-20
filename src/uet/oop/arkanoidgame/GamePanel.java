package uet.oop.arkanoidgame;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import uet.oop.arkanoidgame.entities.ball.Ball;
import uet.oop.arkanoidgame.entities.brick.BrickGrid;
import uet.oop.arkanoidgame.entities.paddle.Paddle;
import uet.oop.arkanoidgame.entities.menu.MainMenu;

public class GamePanel extends Canvas {

    private Paddle paddle;
    private Ball ball;
    private BrickGrid bricks;
    private boolean gameRunning = true;
    private AnimationTimer timer;
    private Stage stage; // để quay lại menu khi game over

    public GamePanel(Stage stage) {
        super(800, 600);
        this.stage = stage;

        paddle = new Paddle(350, 550, 140, 40);
        ball = new Ball(390, 300, 15);
        bricks = new BrickGrid(8, 5);

        setFocusTraversable(true);
        setOnKeyPressed(e -> paddle.addKey(e.getCode()));
        setOnKeyReleased(e -> paddle.removeKey(e.getCode()));

        this.setOnMouseMoved(e -> paddle.handleMouseMove(e));
        this.setOnMouseDragged(e -> paddle.handleMouseMove(e));
    }

    public void startGame() {
        GraphicsContext gc = getGraphicsContext2D();

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (gameRunning) {
                    update();
                    render(gc);
                } else {
                    stop();
                    showGameOverScreen();
                }
            }
        };
        timer.start();
    }

    private void update() {
        ball.update(getWidth(), getHeight());
        paddle.update();
        ball.checkCollision(paddle);
        ball.checkCollision(bricks);

        // Nếu bóng rơi ra ngoài màn hình -> thua
        if (ball.getY() > getHeight()) {
            gameRunning = false;
        }
    }

    private void render(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, getWidth(), getHeight());

        bricks.render(gc);
        paddle.render(gc);
        ball.render(gc);
    }

    // Hiển thị màn hình "Game Over" + nút quay lại menu
    private void showGameOverScreen() {
        StackPane overlay = new StackPane();
        overlay.setPrefSize(800, 600);

        // Ảnh nền Game Over (full màn hình)
        Image image = new Image(getClass().getResource(
                "/uet/oop/arkanoidgame/entities/menu/menu_images/game_over.png"
        ).toExternalForm());
        ImageView gameOverView = new ImageView(image);
        gameOverView.setFitWidth(800);
        gameOverView.setFitHeight(600);
        gameOverView.setPreserveRatio(false); // ảnh fill full

        // Nút "Back to Menu"
        Button backToMenu = new Button("Back to Menu");
        backToMenu.setStyle(
                "-fx-font-size: 22px; " +
                        "-fx-background-color: #ffcc00; " +
                        "-fx-text-fill: black; " +
                        "-fx-background-radius: 10;"
        );

        // Hiệu ứng hover cho nút
        backToMenu.setOnMouseEntered(e ->
                backToMenu.setStyle("-fx-font-size: 22px; -fx-background-color: #ffaa00; -fx-text-fill: white; -fx-background-radius: 10;")
        );
        backToMenu.setOnMouseExited(e ->
                backToMenu.setStyle("-fx-font-size: 22px; -fx-background-color: #ffcc00; -fx-text-fill: black; -fx-background-radius: 10;")
        );

        backToMenu.setOnAction(e -> {
            MainMenu menu = new MainMenu(stage);
            stage.setScene(new Scene(menu, 800, 600));
        });

        // Bố trí: ảnh full + nút giữa màn
        StackPane.setAlignment(backToMenu, Pos.CENTER);
        overlay.getChildren().addAll(gameOverView, backToMenu);

        // Gán overlay vào stage
        stage.getScene().setRoot(overlay);
    }
}
