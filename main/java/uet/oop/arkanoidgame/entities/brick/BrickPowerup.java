package uet.oop.arkanoidgame.entities.brick;

import javafx.scene.paint.Color;
import uet.oop.arkanoidgame.entities.item.*;
import uet.oop.arkanoidgame.entities.item.Buff.Buff_BiggerPaddle;
import uet.oop.arkanoidgame.entities.item.Buff.Buff_BiggerBall;
import uet.oop.arkanoidgame.entities.item.Buff.Buff_ExplosiveBall;
import uet.oop.arkanoidgame.entities.item.Buff.Buff_ExtraLives;
import uet.oop.arkanoidgame.entities.item.DeBuff.DeBuff_FastBall;
import uet.oop.arkanoidgame.entities.item.DeBuff.DeBuff_SmallerBall;
import uet.oop.arkanoidgame.entities.item.DeBuff.DeBuff_SmallerPaddle;
import uet.oop.arkanoidgame.entities.item.DeBuff.DeBuff_ReversePaddle;
import uet.oop.arkanoidgame.entities.item.Buff.Buff_SlowerBall;

public class BrickPowerup extends Brick {
    private static final String IMAGE_PATH = "weak.png";
    private static final double DROP_CHANCE = 1; // 100% cơ hội drop vật phẩm

    public BrickPowerup(double x, double y, double width, double height) {
        super(x, y, width, height, 1, IMAGE_PATH);
    }

    @Override
    public Item getPowerup() {
        if (destroyed && Math.random() < DROP_CHANCE) {
            // Chọn ngẫu nhiên giữa các vật phẩm
            int itemType = (int) (Math.random() * 9);
            switch (itemType) {
                case 0:
                    return new Buff_BiggerPaddle(x + width / 2, y + height);
                case 1:
                    return new DeBuff_ReversePaddle(x + width / 2, y + height);
                case 2:
                    return new DeBuff_FastBall(x + width / 2, y + height);
                case 3:
                    return new Buff_SlowerBall(x + width / 2, y + height);
                case 4:
                    return new Buff_BiggerBall(x + width / 2, y + height);
                case 5:
                    return new DeBuff_SmallerBall(x + width / 2, y + height);
                case 6:
                    return new DeBuff_SmallerPaddle(x + width / 2, y + height);
                case 7:
                    return new Buff_ExplosiveBall(x + width / 2, y + height);
                case 8:
                    return new Buff_ExtraLives( x + width / 2, y + height);
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