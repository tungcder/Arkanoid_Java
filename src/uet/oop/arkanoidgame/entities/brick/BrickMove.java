package uet.oop.arkanoidgame.entities.brick;

import javafx.scene.paint.Color;
import java.util.List;

public class BrickMove extends Brick {
    private static final String IMAGE_PATH = "weak.png";
    private static final double SPEED = 2.0;
    private int direction = 1;

    private double minX;
    private double maxX;

    public BrickMove(double x, double y, double width, double height) {
        super(x, y, width, height, 1, IMAGE_PATH);
    }

    public void initMovementRange(BrickGrid brickGrid) {
        List<Brick> allBricks = brickGrid.getBricks();
        double tolerance = 5.0; // Chấp nhận sai số nhỏ do float

        // Tìm tất cả brick cùng hàng với BrickMove (dựa trên y)
        double centerY = y + height / 2.0;

        // Tìm biên trái: gạch gần nhất bên trái (cùng hàng)
        double leftEdge = 0;
        for (Brick b : allBricks) {
            if (!b.isDestroyed() &&
                    Math.abs((b.getY() + b.getHeight() / 2.0) - centerY) < tolerance &&
                    b.getX() + b.getWidth() <= x) {
                leftEdge = Math.max(leftEdge, b.getX() + b.getWidth());
            }
        }

        // Tìm biên phải: gạch gần nhất bên phải
        double rightEdge = 600; // canvas width
        for (Brick b : allBricks) {
            if (!b.isDestroyed() &&
                    Math.abs((b.getY() + b.getHeight() / 2.0) - centerY) < tolerance &&
                    b.getX() >= x + width) {
                rightEdge = Math.min(rightEdge, b.getX());
            }
        }

        // Khoảng trống = từ leftEdge đến rightEdge
        minX = leftEdge;
        maxX = rightEdge - width; // trừ width để không đè

        // Giới hạn trong màn hình
        minX = Math.max(0, minX);
        maxX = Math.min(600 - width, maxX);

        System.out.println("BrickMove(" + x + "," + y +
                ") di chuyển: " + minX + " → " + maxX);
    }

    @Override
    public void update() {
        if (destroyed) return;

        x += SPEED * direction;

        if (x <= minX) {
            x = minX;
            direction = 1;
        } else if (x >= maxX) {
            x = maxX;
            direction = -1;
        }
    }

    @Override
    protected Color getFallbackColor() {
        return Color.LIME;
    }
}