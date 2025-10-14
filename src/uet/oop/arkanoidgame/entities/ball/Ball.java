package uet.oop.arkanoidgame.entities.ball;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import uet.oop.arkanoidgame.entities.brick.Brick;
import uet.oop.arkanoidgame.entities.BrickGrid;
import uet.oop.arkanoidgame.entities.paddle.Paddle;

public class Ball {
    private double x, y;
    private double radius;
    private double dx = 3, dy = -3; // tốc độ ban đầu

    public Ball(double x, double y, double radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public void update() {
        x += dx;
        y += dy;

        // Va chạm tường
        if (x <= 0 || x + radius * 2 >= 800) dx *= -1;
        if (y <= 0) dy *= -1;
    }

    public void checkCollision(Paddle paddle) {
        if (x + radius * 2 > paddle.getX() &&
                x < paddle.getX() + paddle.getWidth() &&
                y + radius * 2 >= paddle.getY() &&
                y + radius * 2 <= paddle.getY() + paddle.getHeight()) {
            dy *= -1;
            y = paddle.getY() - radius * 2; // tránh dính paddle
        }
    }

    public void checkCollision(BrickGrid grid) {
        for (Brick brick : grid.getBricks()) {
            if (!brick.isDestroyed() &&
                    x + radius * 2 > brick.getX() &&
                    x < brick.getX() + brick.getWidth() &&
                    y + radius * 2 > brick.getY() &&
                    y < brick.getY() + brick.getHeight()) {

                dy *= -1;
                brick.setDestroyed(true);
                break;
            }
        }
    }

    public void render(GraphicsContext gc) {
        gc.setFill(Color.YELLOW);
        gc.fillOval(x, y, radius * 2, radius * 2);
    }
}
