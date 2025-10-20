package uet.oop.arkanoidgame.entities.paddle;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Paddle {
    private double x, y, width, height;
    private double speed = 6;
    private Set<KeyCode> keys = new HashSet<>();
    private Image paddleImage;

    private static final double CANVAS_WIDTH = 800;

    public Paddle(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        // ✅ Load ảnh paddle từ file paddle_normal.png
        paddleImage = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/uet/oop/arkanoidgame/entities/paddle/paddle_image/paddle_normal.png")
        ));
    }

    // ====== Update vị trí theo bàn phím ======
    public void update() {
        if (keys.contains(KeyCode.LEFT) && x > 0) x -= speed;
        if (keys.contains(KeyCode.RIGHT) && x + width < CANVAS_WIDTH) x += speed;
    }

    // ====== Điều khiển paddle bằng chuột ======
    public void handleMouseMove(MouseEvent e) {
        double mouseX = e.getX();
        // Căn giữa paddle theo chuột và giới hạn không ra khỏi màn hình
        x = Math.max(0, Math.min(mouseX - width / 2, CANVAS_WIDTH - width));
    }

    // ====== Vẽ paddle ======
    public void render(GraphicsContext gc) {
        gc.drawImage(paddleImage, x, y, width, height);
    }

    // ====== Xử lý phím ======
    public void addKey(KeyCode code) {
        keys.add(code);
    }

    public void removeKey(KeyCode code) {
        keys.remove(code);
    }

    // ====== Getter ======
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
}
