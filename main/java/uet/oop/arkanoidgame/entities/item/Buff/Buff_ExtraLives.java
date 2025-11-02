package uet.oop.arkanoidgame.entities.item.Buff;

import uet.oop.arkanoidgame.entities.ball.Ball;
import uet.oop.arkanoidgame.entities.item.Item;
import uet.oop.arkanoidgame.entities.paddle.Paddle;
import uet.oop.arkanoidgame.GamePanel;

public class Buff_ExtraLives extends Item {
    private static final String IMAGE_PATH = "/Images/Items/Buff/ExtraLives.png";
    private static final int EXTRA_LIVES = 1;

    public Buff_ExtraLives(double x, double y) {
        super(x, y, IMAGE_PATH);
    }

    @Override
    public void apply(Paddle paddle, Ball ball) {
        GamePanel.addLives(EXTRA_LIVES);
    }

    @Override
    public String getBuffName() {
        return "Extra Life";
    }

    @Override
    public int getDurationSeconds() {
        return 0; // Không có thời gian, hiệu ứng tức thì
    }

    @Override
    public boolean isBuff() {
        return true;
    }
}