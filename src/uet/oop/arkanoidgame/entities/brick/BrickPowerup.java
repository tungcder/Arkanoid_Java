package uet.oop.arkanoidgame.entities.brick;

import javafx.scene.paint.Color;
import uet.oop.arkanoidgame.entities.item.BiggerPaddle;
import uet.oop.arkanoidgame.entities.item.Item;

public class BrickPowerup extends Brick {
    private static final String IMAGE_PATH = "7.png";

    public BrickPowerup(double x, double y, double width, double height) {
        super(x, y, width, height, 1, IMAGE_PATH);
    }

    @Override
    public Item getPowerup() {
        if (destroyed) {
            return new BiggerPaddle(x + width / 2, y + height);
        }
        return null;
    }

    @Override
    protected Color getFallbackColor() {
        return Color.PURPLE; // Màu fallback cho Powerup
    }
}