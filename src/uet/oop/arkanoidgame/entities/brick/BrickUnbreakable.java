package uet.oop.arkanoidgame.entities.brick;

import javafx.scene.paint.Color;

public class BrickUnbreakable extends Brick {
    private static final String IMAGE_PATH = "3.png";

    public BrickUnbreakable(double x, double y, double width, double height) {
        super(x, y, width, height, Integer.MAX_VALUE, IMAGE_PATH);
    }

    @Override
    public boolean hit() {
        return false;
    }

    @Override
    public boolean isBreakable() {
        return false;
    }

    @Override
    protected Color getFallbackColor() {
        return Color.GRAY; // Màu fallback cho Unbreakable
    }
}