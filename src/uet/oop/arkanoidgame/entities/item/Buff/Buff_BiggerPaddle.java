package uet.oop.arkanoidgame.entities.item;

import uet.oop.arkanoidgame.entities.ball.Ball;
import uet.oop.arkanoidgame.entities.paddle.Paddle;

public class Buff_BiggerPaddle extends Item {
    private static final String IMAGE_PATH = "/uet/oop/arkanoidgame/entities/item/Buff/Buff_Image/BiggerPaddle.png";
    private static final double SIZE_INCREASE = 1.25;
    private static final double BUFF_DURATION = 7.0;  // Giây

    public Buff_BiggerPaddle(double x, double y) {
        super(x, y, IMAGE_PATH);
    }

    @Override
    public void apply(Paddle paddle, Ball ball) {
        paddle.applySizeBuff(SIZE_INCREASE, BUFF_DURATION);
    }
}
