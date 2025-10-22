package uet.oop.arkanoidgame.entities.brick;

import javafx.scene.paint.Color;

public class BrickStrong extends Brick {
    private static final String IMAGE_PATH = "5.png";

    public BrickStrong(double x, double y, double width, double height) {
        super(x, y, width, height, 3, IMAGE_PATH);
    }

    @Override
    protected Color getFallbackColor() {
        return Color.ORANGE; // Màu fallback cho Strong
    }
}