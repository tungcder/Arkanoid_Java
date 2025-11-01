package uet.oop.arkanoidgame.entities.item;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import uet.oop.arkanoidgame.entities.ball.Ball;
import uet.oop.arkanoidgame.entities.paddle.Paddle;
import uet.oop.arkanoidgame.GamePanel;

public class Buff_ExtraLives extends Item {
    private static final String IMAGE_PATH = "/Images/Items/Buff/ExtraLives.png";
    private static final int EXTRA_LIVES = 1; // số mạng cộng thêm

    public Buff_ExtraLives(double x, double y) {
        super(x, y, IMAGE_PATH);
    }

    @Override
    public void apply(Paddle paddle, Ball ball) {
        // Kiểm tra xem GamePanel có hàm tăng mạng hay không
        GamePanel.addLives(EXTRA_LIVES);

        // Nếu muốn hiển thị thông báo hay hiệu ứng nhỏ thì có thể thêm:
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(0.1),
                e -> System.out.println("+1 Life!")
        ));
        timeline.play();
    }
}
