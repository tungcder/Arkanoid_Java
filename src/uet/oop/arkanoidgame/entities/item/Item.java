package uet.oop.arkanoidgame.entities.item;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import uet.oop.arkanoidgame.entities.ball.Ball;
import uet.oop.arkanoidgame.entities.paddle.Paddle;

public abstract class Item {
    private double x, y, width, height;
    private double fallSpeed = 2;
    private Color color;
    private boolean active = true;

    public Item(double x, double y, double width, double height, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    public void update() {
        if (active) {
            y += fallSpeed;
            if (y > 600) {
                active = false;
            }
        }
    }

    public void render(GraphicsContext gc) {
        if (active) {
            gc.setFill(color);
            gc.fillRect(x, y, width, height);
        }
    }

    public boolean checkCollision(Paddle paddle, Ball ball) {
        if (active &&
                x + width > paddle.getX() &&
                x < paddle.getX() + paddle.getWidth() &&
                y + height > paddle.getY() &&
                y < paddle.getY() + paddle.getHeight()) {
            applyEffect(paddle, ball);
            active = false;
            return true;
        }
        return false;
    }

    protected abstract void applyEffect(Paddle paddle, Ball ball);

    public boolean isActive() { return active; }
}