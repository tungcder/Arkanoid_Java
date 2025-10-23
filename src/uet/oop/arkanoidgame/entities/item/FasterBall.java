package uet.oop.arkanoidgame.entities.item;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import uet.oop.arkanoidgame.entities.ball.Ball;
import uet.oop.arkanoidgame.entities.paddle.Paddle;

public class FasterBall extends Item {
    private static final String IMAGE_PATH = "/uet/oop/arkanoidgame/entities/ball/Ball_Image/fast.png";
    private static final double SPEED_INCREASE = 1.5; // Tăng tốc độ bóng lên 150%
    private static final double DEBUFF_DURATION = 15.0; // Thời gian hiệu ứng (giây)

    public FasterBall(double x, double y) {
        super(x, y, IMAGE_PATH);
    }

    @Override
    public void apply(Paddle paddle, Ball ball) {
        ball.setSpeedMultiplier(SPEED_INCREASE); // Áp dụng debuff

        // Revert sau thời gian
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(DEBUFF_DURATION),
                ae -> ball.setSpeedMultiplier(1.0)
        ));
        timeline.play();
    }
}