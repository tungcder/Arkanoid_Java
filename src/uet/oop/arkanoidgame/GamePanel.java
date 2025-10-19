package uet.oop.arkanoidgame;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
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

        paddle = new Paddle(350, 550, 100, 15);
        ball = new Ball(390, 300, 15);
        bricks = new BrickGrid(8, 5);

        setFocusTraversable(true);
        setOnKeyPressed(e -> paddle.addKey(e.getCode()));
        setOnKeyReleased(e -> paddle.removeKey(e.getCode()));
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
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);");

        Button backToMenu = new Button("Back to Menu");
        backToMenu.setStyle("-fx-font-size: 20px; -fx-background-color: cyan; -fx-text-fill: black;");
        backToMenu.setOnAction(e -> {
            // Quay lại menu
            MainMenu menu = new MainMenu(stage);
            stage.setScene(new javafx.scene.Scene(menu, 800, 600));
        });

        overlay.getChildren().add(backToMenu);
        StackPane.setAlignment(backToMenu, javafx.geometry.Pos.CENTER);

        // Gắn lớp overlay vào stage
        stage.getScene().setRoot(overlay);
    }
}
