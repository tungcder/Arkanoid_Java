package uet.oop.arkanoidgame;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import uet.oop.arkanoidgame.entities.BrickGrid;
import uet.oop.arkanoidgame.entities.paddle.Paddle;
import uet.oop.arkanoidgame.entities.ball.Ball;

public class GamePanel extends Canvas {

    private Paddle paddle;
    private Ball ball;
    private BrickGrid bricks;

    public GamePanel() {
        super(800, 600);
        paddle = new Paddle(350, 550, 100, 15);
        ball = new Ball(390, 300, 10);
        bricks = new BrickGrid(8, 5);

        setFocusTraversable(true);

        setOnKeyPressed(e -> paddle.addKey(e.getCode()));
        setOnKeyReleased(e -> paddle.removeKey(e.getCode()));
    }

    public void startGame() {
        GraphicsContext gc = getGraphicsContext2D();

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render(gc);
            }
        }.start();
    }

    private void update() {
        ball.update();
        paddle.update();
        ball.checkCollision(paddle);
        ball.checkCollision(bricks);
    }

    private void render(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, getWidth(), getHeight());

        bricks.render(gc);
        paddle.render(gc);
        ball.render(gc);
    }
}
