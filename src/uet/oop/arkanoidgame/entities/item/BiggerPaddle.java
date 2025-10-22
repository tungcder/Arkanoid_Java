package uet.oop.arkanoidgame.entities.item;

import uet.oop.arkanoidgame.entities.paddle.Paddle;

public class BiggerPaddle extends Item {
    private static final String IMAGE_PATH = "/uet/oop/arkanoidgame/entities/item/images/bigger_paddle.png";
    private static final double WIDTH_INCREASE = 50; // Pixels to add to paddle width

    public BiggerPaddle(double x, double y) {
        super(x, y, IMAGE_PATH);
    }

    @Override
    public void apply(Paddle paddle) {
        paddle.setWidth(paddle.getWidth() + WIDTH_INCREASE);
    }
}