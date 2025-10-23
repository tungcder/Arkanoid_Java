package uet.oop.arkanoidgame.entities.item;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import uet.oop.arkanoidgame.entities.ball.Ball;
import uet.oop.arkanoidgame.entities.paddle.Paddle;

public class BiggerPaddle extends Item {
    private static final String IMAGE_PATH = "/uet/oop/arkanoidgame/entities/paddle/Paddle_Image/bigger.png";
    private static final double SIZE_INCREASE = 1.5; // Tăng kích thước paddle lên 150%
    private static final double BUFF_DURATION = 15.0; // Thời gian hiệu ứng (giây)

    public BiggerPaddle(double x, double y) {
        super(x, y, IMAGE_PATH);
    }

    @Override
    public void apply(Paddle paddle, Ball ball) {
        double originalWidth = paddle.getWidth();
        paddle.setWidth(originalWidth * SIZE_INCREASE); // Áp dụng buff

        // Revert sau thời gian
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(BUFF_DURATION),
                ae -> paddle.setWidth(originalWidth)
        ));
        timeline.play();
    }
}