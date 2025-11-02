package uet.oop.arkanoidgame.entities.item.Buff;

import uet.oop.arkanoidgame.entities.ball.Ball;
import uet.oop.arkanoidgame.entities.item.Item;
import uet.oop.arkanoidgame.entities.paddle.Paddle;

public class Buff_SlowerBall extends Item {
    private static final String IMAGE_PATH = "/Images/Items/Buff/SlowerBall.png";
    private static final double SPEED_DECREASE = 0.5;
    private static final double BUFF_DURATION  = 7.0;

    public Buff_SlowerBall(double x, double y) {
        super(x, y, IMAGE_PATH);
    }

    @Override
    public void apply(Paddle paddle, Ball ball) {
        ball.applySpeedBuff(SPEED_DECREASE, BUFF_DURATION);
    }

    @Override
    public String getBuffName() {
        return "Slower Ball";
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