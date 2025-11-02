package uet.oop.arkanoidgame.entities.item.DeBuff;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import uet.oop.arkanoidgame.entities.ball.Ball;
import uet.oop.arkanoidgame.entities.item.Item;
import uet.oop.arkanoidgame.entities.paddle.Paddle;

public class DeBuff_ReversePaddle extends Item {
    private static final String IMAGE_PATH = "/Images/Items/DeBuff/ReversePaddle.png";
    private static final double DEBUFF_DURATION = 5.0;

    public DeBuff_ReversePaddle(double x, double y) {
        super(x, y, IMAGE_PATH);
    }

    @Override
    public void apply(Paddle paddle, Ball ball) {
        paddle.setReverseDirection(true);

        Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(DEBUFF_DURATION),
                ae -> paddle.setReverseDirection(false)
        ));
        timeline.play();
    }

    @Override
    public String getBuffName() {
        return "Reverse Control";
    }

    @Override
    public int getDurationSeconds() {
        return (int) DEBUFF_DURATION;
    }

    @Override
    public boolean isBuff() {
        return false;
    }
}