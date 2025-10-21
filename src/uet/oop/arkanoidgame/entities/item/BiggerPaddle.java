package uet.oop.arkanoidgame.entities.item;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.paint.Color;
import uet.oop.arkanoidgame.entities.ball.Ball;
import uet.oop.arkanoidgame.entities.paddle.Paddle;

public class BiggerPaddle extends Item {
    private double extraWidth = 50;
    private long duration = 10000;

    public BiggerPaddle(double x, double y) {
        super(x, y, 20, 20, Color.BLUE); // Màu xanh dương để nổi bật
    }

    @Override
    protected void applyEffect(Paddle paddle, Ball ball) {
        paddle.setWidth(paddle.getWidth() + extraWidth);
        new Timeline(new KeyFrame(Duration.millis(duration), ae ->
                paddle.setWidth(paddle.getWidth() - extraWidth)
        )).play();
    }
}