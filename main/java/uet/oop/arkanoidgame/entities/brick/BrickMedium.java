package uet.oop.arkanoidgame.entities.brick;

import javafx.scene.paint.Color;

public class BrickMedium extends Brick {
    public BrickMedium(double x, double y, double width, double height) {
        // 2 ảnh: nguyên vẹn → nứt → vỡ
        super(x, y, width, height, 2, "medium.png", "medium1.png");
    }

    @Override
    protected Color getFallbackColor() {
        return Color.YELLOW;
    }
}