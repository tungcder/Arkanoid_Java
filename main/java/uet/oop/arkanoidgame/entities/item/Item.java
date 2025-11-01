package uet.oop.arkanoidgame.entities.item;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import uet.oop.arkanoidgame.entities.ball.Ball;
import uet.oop.arkanoidgame.entities.paddle.Paddle;
import java.util.Objects;

public abstract class Item {
    protected double x, y, width = 40, height = 40;
    protected double fallSpeed = 1.0;
    protected Image image;

    public Item(double x, double y, String imagePath) {
        this.x = x - width / 2;
        this.y = y;
        try {
            this.image = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream(imagePath)
            ));
            if (image.isError()) {
                System.err.println("Lỗi load ảnh item: " + imagePath + " (isError=true)");
                this.image = null;
            }
        } catch (NullPointerException e) {
            System.err.println("Không tìm thấy file item: " + imagePath);
            this.image = null;
        }
    }

    public void update() {
        y += fallSpeed;
    }

    public void render(GraphicsContext gc) {
        if (image != null) {
            gc.drawImage(image, x, y, width, height);
        } else {
            gc.setFill(Color.LIME);
            gc.fillRect(x, y, width, height);
            gc.setStroke(Color.BLACK);
            gc.strokeRect(x, y, width, height);
        }
    }

    public boolean collidesWith(Paddle paddle) {
        return x < paddle.getX() + paddle.getWidth() &&
                x + width > paddle.getX() &&
                y < paddle.getY() + paddle.getHeight() &&
                y + height > paddle.getY();
    }

    public abstract void apply(Paddle paddle, Ball ball);

    // ✅ THÊM CÁC METHOD MỚI
    public abstract String getBuffName();
    public abstract int getDurationSeconds();
    public abstract boolean isBuff(); // true = buff, false = debuff

    public double getY() { return y; }
}