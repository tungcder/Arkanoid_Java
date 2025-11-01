package uet.oop.arkanoidgame.entities.item;

import uet.oop.arkanoidgame.entities.ball.Ball;
import uet.oop.arkanoidgame.entities.paddle.Paddle;

public class DeBuff_FastBall extends Item {
    private static final String IMAGE_PATH = "/uet/oop/arkanoidgame/entities/item/DeBuff/DeBuff_Image/FastBall.png";
    private static final double SPEED_INCREASE = 2;
    private static final double DEBUFF_DURATION  = 7.0;

    public DeBuff_FastBall(double x, double y) {
        super(x, y, IMAGE_PATH);
    }

    @Override
    public void apply(Paddle paddle, Ball ball) {
        ball.applySpeedBuff(SPEED_INCREASE, DEBUFF_DURATION);
    }
}
