package uet.oop.arkanoidgame.entities.item;

import uet.oop.arkanoidgame.entities.ball.Ball;
import uet.oop.arkanoidgame.entities.paddle.Paddle;

public class DeBuff_SmallerPaddle extends Item {
    private static final String IMAGE_PATH = "/uet/oop/arkanoidgame/entities/item/DeBuff/DeBuff_Image/SmallerPaddle.png";
    private static final double SIZE_DECREASE = 0.7;  // Giảm còn 70%
    private static final double BUFF_DURATION = 7.0;  // Giây

    public DeBuff_SmallerPaddle(double x, double y) {
        super(x, y, IMAGE_PATH);
    }

    @Override
    public void apply(Paddle paddle, Ball ball) {
        paddle.applySizeBuff(SIZE_DECREASE, BUFF_DURATION);
    }
}
