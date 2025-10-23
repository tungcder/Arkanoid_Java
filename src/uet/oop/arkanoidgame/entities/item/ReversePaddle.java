package uet.oop.arkanoidgame.entities.item;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import uet.oop.arkanoidgame.entities.ball.Ball;
import uet.oop.arkanoidgame.entities.paddle.Paddle;

public class ReversePaddle extends Item {
    private static final String IMAGE_PATH = "/uet/oop/arkanoidgame/entities/paddle/Paddle_Image/reverse.png";
    private static final double DEBUFF_DURATION = 5.0; // Thời gian hiệu ứng (giây)

    public ReversePaddle(double x, double y) {
        super(x, y, IMAGE_PATH);
    }

    @Override
    public void apply(Paddle paddle, Ball ball) {
        paddle.setReverseDirection(true); // Áp dụng debuff

        // Revert sau thời gian
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(DEBUFF_DURATION),
                ae -> paddle.setReverseDirection(false)
        ));
        timeline.play();
    }
}