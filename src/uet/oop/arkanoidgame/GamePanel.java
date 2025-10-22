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
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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

public class GamePanel extends Canvas {

    private Paddle paddle;
    private Ball ball;
    private BrickGrid bricks;
    private boolean gameRunning = true;
    private AnimationTimer timer;
    private Stage stage;
    private MapManager mapManager;
    private Pane gamePane;
    private List<Item> items = new ArrayList<>();

    public GamePanel(Stage stage) {
        super(800, 600);
        this.stage = stage;
        this.mapManager = new MapManager();

        gamePane = new Pane();
        gamePane.getChildren().add(this);

        paddle = new Paddle(350, 550, 140, 40);
        ball = new Ball(390, 300, 15);
        bricks = new BrickGrid("src/main/resources/Levels/Map1.csv");
        mapManager.loadLevel(bricks);

        setFocusTraversable(true);
        setOnKeyPressed(e -> paddle.addKey(e.getCode()));
        setOnKeyReleased(e -> paddle.removeKey(e.getCode()));

        this.setOnMouseMoved(e -> paddle.handleMouseMove(e));
        this.setOnMouseDragged(e -> paddle.handleMouseMove(e));

        stage.setScene(new Scene(gamePane, 800, 600));
    }

    public void startGame() {
        GraphicsContext gc = getGraphicsContext2D();

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (gameRunning) {
                    update();
                    render(gc);
                    if (bricks.isLevelComplete() && mapManager.hasNextLevel()) {
                        mapManager.nextLevel(bricks);
                        resetBallAndPaddle();
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
        ball.update(getWidth(), getHeight());
        paddle.update();
        ball.checkCollision(paddle);
        Item spawned = ball.checkCollision(bricks);
        if (spawned != null) {
            items.add(spawned);
        }

        // Update items
        Iterator<Item> iter = items.iterator();
        while (iter.hasNext()) {
            Item item = iter.next();
            item.update();
            if (item.getY() > getHeight()) {
                iter.remove();
            } else if (item.collidesWith(paddle)) {
                item.apply(paddle);
                iter.remove();
            }
        }

        if (ball.getY() > getHeight()) {
            gameRunning = false;
        }
    }

    private void render(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, getWidth(), getHeight());

        bricks.render(gc); // Đảm bảo BrickGrid có phương thức render
        paddle.render(gc);
        ball.render(gc);

        // Render items
        for (Item item : items) {
            item.render(gc);
        }
    }

    private void resetBallAndPaddle() {
        ball = new Ball(390, 300, 15);
        paddle = new Paddle(350, 550, 140, 40);
        setOnKeyPressed(e -> paddle.addKey(e.getCode()));
        setOnKeyReleased(e -> paddle.removeKey(e.getCode()));
        this.setOnMouseMoved(e -> paddle.handleMouseMove(e));
        this.setOnMouseDragged(e -> paddle.handleMouseMove(e));
    }

    private void showGameOverScreen() {
        StackPane overlay = new StackPane();
        overlay.setPrefSize(800, 600);

        Image image = new Image(getClass().getResource(
                "/uet/oop/arkanoidgame/entities/menu/menu_images/game_over.png"
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

        Image image = new Image(getClass().getResource(
                "/uet/oop/arkanoidgame/entities/menu/menu_images/game_complete.png"
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