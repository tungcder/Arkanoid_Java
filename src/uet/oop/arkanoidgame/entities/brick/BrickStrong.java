package uet.oop.arkanoidgame.entities.brick;

import javafx.scene.paint.Color;

public class BrickStrong extends Brick {
    public BrickStrong(double x, double y, double width, double height) {
        // 3 ảnh: nguyên vẹn → nứt nhẹ → nứt nặng → vỡ
        super(x, y, width, height, 3,
                "strong.png", "strong1.png", "strong2.png");
    }

    @Override
    protected Color getFallbackColor() {
        return Color.ORANGE;
    }
}