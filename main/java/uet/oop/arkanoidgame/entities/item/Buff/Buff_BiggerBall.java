package uet.oop.arkanoidgame.entities.item;

import uet.oop.arkanoidgame.entities.ball.Ball;
import uet.oop.arkanoidgame.entities.paddle.Paddle;

public class Buff_BiggerBall extends Item {
    private static final String IMAGE_PATH = "/uet/oop/arkanoidgame/entities/item/Buff/Buff_Image/BiggerBall.png";
    private static final double SIZE_INCREASE = 1.25; // to hơn 150%
    private static final double BUFF_DURATION = 7.0; // giây

    public Buff_BiggerBall(double x, double y) {
        super(x, y, IMAGE_PATH);
    }

    @Override
    public void apply(Paddle paddle, Ball ball) {
        ball.applySizeBuff(SIZE_INCREASE, BUFF_DURATION);
    }
}
