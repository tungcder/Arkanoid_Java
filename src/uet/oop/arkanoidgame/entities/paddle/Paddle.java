package uet.oop.arkanoidgame.entities.paddle;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import uet.oop.arkanoidgame.entities.ball.Ball;

import java.util.*;

public class Paddle {
    private  double x, y, width, height;
    private double speed = 6;
    private Set<KeyCode> keys = new HashSet<>();
    private static final double CANVAS_WIDTH = 800;

    private List<Image> paddleFrames;
    private int currentFrameIndex;
    private int animationCounter;
    private static final int ANIMATION_DELAY = 30;

    public Paddle(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.paddleFrames = new ArrayList<>();
        this.currentFrameIndex = 0;
        this.animationCounter = 0;

        // Đảm bảo bạn có 2 file ảnh này trong đúng thư mục
        try {
            Image frame1 = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/uet/oop/arkanoidgame/entities/paddle/Paddle_Image/PaddleA1.png")
            ));
            Image frame2 = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/uet/oop/arkanoidgame/entities/paddle/Paddle_Image/PaddleA2.png")
            ));
            paddleFrames.add(frame1);
            paddleFrames.add(frame2);
        } catch (NullPointerException e) {
            System.err.println("Không thể tải ảnh cho paddle. Hãy kiểm tra lại đường dẫn file.");
            // Bạn có thể thêm một ảnh mặc định ở đây nếu cần
        }
    }


    // Update vị trí theo bàn phím
    public void update() {
        if (keys.contains(KeyCode.LEFT) && x > 0) x -= speed;
        if (keys.contains(KeyCode.RIGHT) && x + width < CANVAS_WIDTH) x += speed;

        // --- LOGIC CẬP NHẬT ANIMATION ---
        animationCounter++;
        if (animationCounter >= ANIMATION_DELAY) {
            animationCounter = 0; // Reset bộ đếm
            // Chuyển sang frame tiếp theo, quay vòng lại nếu hết danh sách
            currentFrameIndex = (currentFrameIndex + 1) % paddleFrames.size();
        }
    }

    // Điều khiển paddle bằng chuột
    public void handleMouseMove(MouseEvent e) {
        double mouseX = e.getX();
        // Căn giữa paddle theo chuột và giới hạn không ra khỏi màn hình
        x = Math.max(0, Math.min(mouseX - width / 2, CANVAS_WIDTH - width));
    }

    public void render(GraphicsContext gc) {
        // Kiểm tra xem danh sách frame có rỗng không
        if (paddleFrames.isEmpty()) {
            return; // Không vẽ gì nếu không có ảnh
        }
        // Lấy frame hiện tại từ danh sách
        Image currentFrame = paddleFrames.get(currentFrameIndex);
        // Vẽ frame đó
        gc.drawImage(currentFrame, x, y, width, height);
    }

    // Xử lý phím
    public void addKey(KeyCode code) {
        keys.add(code);
    }

    public void removeKey(KeyCode code) {
        keys.remove(code);
    }

    // Getter
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
}
