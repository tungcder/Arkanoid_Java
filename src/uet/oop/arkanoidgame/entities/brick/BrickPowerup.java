package uet.oop.arkanoidgame.entities.brick;

import javafx.scene.paint.Color;
import uet.oop.arkanoidgame.entities.item.BiggerPaddle;
import uet.oop.arkanoidgame.entities.item.FasterBall;
import uet.oop.arkanoidgame.entities.item.Item;
import uet.oop.arkanoidgame.entities.item.ReversePaddle;

public class BrickPowerup extends Brick {
    private static final String IMAGE_PATH = "weak.png";
    private static final double DROP_CHANCE = 1; // 50% cơ hội drop vật phẩm

    public BrickPowerup(double x, double y, double width, double height) {
        super(x, y, width, height, 1, IMAGE_PATH);
    }

    @Override
    public Item getPowerup() {
        if (destroyed && Math.random() < DROP_CHANCE) {
            // Chọn ngẫu nhiên giữa các vật phẩm
            int itemType = (int) (Math.random() * 3); // 0: BiggerPaddle, 1: ReversePaddle, 2: FasterBall
            switch (itemType) {
                case 0:
                    return new BiggerPaddle(x + width / 2, y + height);
                case 1:
                    return new ReversePaddle(x + width / 2, y + height);
                case 2:
                    return new FasterBall(x + width / 2, y + height);
                default:
                    return null; // Không bao giờ xảy ra
            }
        }
        return null;
    }

    @Override
    protected Color getFallbackColor() {
        return Color.PURPLE; // Màu fallback cho Powerup
    }
}