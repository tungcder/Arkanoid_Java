package uet.oop.arkanoidgame.entities.item.Buff;

import uet.oop.arkanoidgame.entities.ball.Ball;
import uet.oop.arkanoidgame.entities.item.Item;
import uet.oop.arkanoidgame.entities.paddle.Paddle;

public class Buff_BiggerPaddle extends Item {
    private static final String IMAGE_PATH = "/Images/Items/Buff/BiggerPaddle.png";
    private static final double SIZE_INCREASE = 1.25;
    private static final double BUFF_DURATION = 7.0;

    public Buff_BiggerPaddle(double x, double y) {
        super(x, y, IMAGE_PATH);
    }

    @Override
    public void apply(Paddle paddle, Ball ball) {
        paddle.applySizeBuff(SIZE_INCREASE, BUFF_DURATION);
    }

    @Override
    public String getBuffName() {
        return "Bigger Paddle";
    }

    @Override
    public int getDurationSeconds() {
        return (int) BUFF_DURATION;
    }

    @Override
    public boolean isBuff() {
        return true;
    }
}