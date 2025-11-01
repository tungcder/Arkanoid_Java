package uet.oop.arkanoidgame.entities.item;

import uet.oop.arkanoidgame.entities.ball.Ball;
import uet.oop.arkanoidgame.entities.paddle.Paddle;

public class Buff_ExplosiveBall extends Item {
    private static final String IMAGE_PATH = "/uet/oop/arkanoidgame/entities/item/Buff/Buff_Image/ExplosiveBall.png";
    private static final double EXPLOSION_RADIUS = 80.0; // px quanh điểm va chạm
    private static final double DURATION_SECONDS = 7.0;  // giây

    public Buff_ExplosiveBall(double x, double y) {
        super(x, y, IMAGE_PATH);
    }

    @Override
    public void apply(Paddle paddle, Ball ball) {
        ball.applyExplosiveBuff(EXPLOSION_RADIUS, DURATION_SECONDS);
    }
}
