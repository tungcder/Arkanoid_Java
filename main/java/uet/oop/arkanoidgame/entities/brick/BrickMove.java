// src/main/java/uet/oop/arkanoidgame/entities/brick/BrickMove.java
package uet.oop.arkanoidgame.entities.brick;

import javafx.scene.paint.Color;
import java.util.List;

public class BrickMove extends Brick implements Movable {
    private static final String IMAGE_PATH = "unbreak.png";
    private static final double SPEED = 1.0;
    private int direction = 1;

    private double minX;
    private double maxX;
    private boolean rangeInitialized = false;

    public BrickMove(double x, double y, double width, double height) {
        super(x, y, width, height, Integer.MAX_VALUE, IMAGE_PATH);
        this.breakable = false;  // ✅ Sửa: Đặt rõ ràng là unbreakable
    }

    @Override
    public boolean isBreakable() {
        return false;  // ✅ Sửa: Không coi là breakable
    }

    @Override
    public boolean hit() {
        return false;  // ✅ Sửa: Không cho phép hit (giống BrickUnbreakable)
    }

    @Override
    public void initMovementRange(BrickGrid brickGrid) {
        if (rangeInitialized) return;

        List<Brick> allBricks = brickGrid.getBricks();
        double tolerance = 5.0;
        double centerY = y + height / 2.0;

        double leftEdge = 0;
        for (Brick b : allBricks) {
            if (!b.isDestroyed() &&
                    Math.abs((b.getY() + b.getHeight() / 2.0) - centerY) < tolerance &&
                    b.getX() + b.getWidth() <= x) {
                leftEdge = Math.max(leftEdge, b.getX() + b.getWidth());
            }
        }

        double rightEdge = 600;
        for (Brick b : allBricks) {
            if (!b.isDestroyed() &&
                    Math.abs((b.getY() + b.getHeight() / 2.0) - centerY) < tolerance &&
                    b.getX() >= x + width) {
                rightEdge = Math.min(rightEdge, b.getX());
            }
        }

        minX = Math.max(0, leftEdge);
        maxX = Math.min(600 - width, rightEdge - width);
        rangeInitialized = true;

        System.out.println("BrickMove(" + x + "," + y + ") range: " + minX + " → " + maxX);
    }

    @Override
    public void update() {
        if (destroyed || !rangeInitialized) return;

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
    public void reset() {
        rangeInitialized = false;
        direction = 1;
    }

    @Override
    protected Color getFallbackColor() {
        return Color.LIME;
    }
}