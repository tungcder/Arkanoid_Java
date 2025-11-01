package uet.oop.arkanoidgame.entities.item;

import uet.oop.arkanoidgame.entities.ball.Ball;
import uet.oop.arkanoidgame.entities.paddle.Paddle;

public class DeBuff_SmallerBall extends Item {
    private static final String IMAGE_PATH = "/Images/Items/DeBuff/SmallerBall.png";
    private static final double SIZE_DECREASE = 0.8; // nhỏ còn 70%
    private static final double DEBUFF_DURATION = 7.0; // giây

    public DeBuff_SmallerBall(double x, double y) {
        super(x, y, IMAGE_PATH);
    }

    @Override
    public void apply(Paddle paddle, Ball ball) {
        ball.applySizeBuff(SIZE_DECREASE, DEBUFF_DURATION);
    }
}
