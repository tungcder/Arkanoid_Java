package uet.oop.arkanoidgame.entities.item.Buff;

import uet.oop.arkanoidgame.entities.ball.Ball;
import uet.oop.arkanoidgame.entities.item.Item;
import uet.oop.arkanoidgame.entities.paddle.Paddle;

public class Buff_ExplosiveBall extends Item {
    private static final String IMAGE_PATH = "/Images/Items/Buff/ExplosiveBall.png";
    private static final double EXPLOSION_RADIUS = 80.0;
    private static final double DURATION_SECONDS = 7.0;

    public Buff_ExplosiveBall(double x, double y) {
        super(x, y, IMAGE_PATH);
    }

    @Override
    public void apply(Paddle paddle, Ball ball) {
        ball.applyExplosiveBuff(EXPLOSION_RADIUS, DURATION_SECONDS);
    }

    @Override
    public String getBuffName() {
        return "Explosive Ball";
    }

    @Override
    public int getDurationSeconds() {
        return (int) DURATION_SECONDS;
    }

    @Override
    public boolean isBuff() {
        return true;
    }
}