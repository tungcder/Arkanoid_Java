package uet.oop.arkanoidgame.entities.brick;

import javafx.scene.paint.Color;

public class BrickWeak extends Brick {
    private static final String IMAGE_PATH = "weak.png";

    public BrickWeak(double x, double y, double width, double height) {
        super(x, y, width, height, 1, IMAGE_PATH);
    }

    @Override
    protected Color getFallbackColor() {
        return Color.LIGHTGREEN; // Màu fallback cho Weak
    }
}