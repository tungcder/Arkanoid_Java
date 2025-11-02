package uet.oop.arkanoidgame.entities.brick;

import javafx.scene.paint.Color;

public class BrickMedium extends Brick {
    public BrickMedium(double x, double y, double width, double height) {
        super(x, y, width, height, 2, "medium.png", "medium1.png");
    }

    @Override
    protected Color getFallbackColor() {
        return Color.YELLOW;
    }
}