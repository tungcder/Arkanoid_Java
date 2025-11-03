package uet.oop.arkanoidgame.entities.paddle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import uet.oop.arkanoidgame.ThemeManager;

import java.util.*;

public class Paddle {
    private double x, y, width, height;
    private double speed = 12;
    private Set<KeyCode> keys = new HashSet<>();
    private static final double CANVAS_WIDTH = 600;

    private List<Image> paddleFrames;
    private int currentFrameIndex;
    private int animationCounter;
    private static final int ANIMATION_DELAY = 30;

    private boolean reverseDirection = false;

    // Buff paddle
    private double baseWidth;   // chiều rộng gốc
    private double sizeMultiplier = 1.0;
    private Timeline sizeBuffTimer;

    public Paddle(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.baseWidth = width;

        this.paddleFrames = new ArrayList<>();
        this.currentFrameIndex = 0;
        this.animationCounter = 0;

        try {
            Image frame1 = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream(ThemeManager.getImagePath("paddle/Paddle1.png"))
            ));
            Image frame2 = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream(ThemeManager.getImagePath("paddle/Paddle2.png"))
            ));
            paddleFrames.add(frame1);
            paddleFrames.add(frame2);
        } catch (NullPointerException e) {
            //System.err.println("Không thể tải ảnh cho paddle. Hãy kiểm tra lại đường dẫn file.");
        }
    }

    /**
     * Animation của paddle.
     */
    public void update() {
        // Nếu paddle đang ở frame 2 (frame "hit")
        if (currentFrameIndex == 1) {
            animationCounter++;
            if (animationCounter >= ANIMATION_DELAY) {
                animationCounter = 0;
                currentFrameIndex = 0; // Quay lại frame 1
            }
        }
    }

    /**
     * Điều khiển paddle bằng chuột.
     */
    public void handleMouseMove(MouseEvent e) {
        double mouseX = e.getX();
        if (reverseDirection) {
            mouseX = CANVAS_WIDTH - mouseX;
        }
        x = Math.max(0, Math.min(mouseX - width / 2, CANVAS_WIDTH - width));
    }

    /**
     * Kiểm tra va chạm ball để load frame 2.
     */
    public void handleHit() {
        // Chỉ kích hoạt nếu đang ở frame 1
        if (this.currentFrameIndex == 0) {
            this.currentFrameIndex = 1;
            this.animationCounter = 0;
        }
    }

    public void render(GraphicsContext gc) {
        if (paddleFrames.isEmpty()) {
            return;
        }
        Image currentFrame = paddleFrames.get(currentFrameIndex);
        gc.drawImage(currentFrame, x, y, width, height);
    }

    /**
    * Buff Paddle: Bigger / Smaller.
    */
    public void applySizeBuff(double multiplier, double durationSeconds) {
        // Hủy buff cũ nếu đang chạy
        if (sizeBuffTimer != null) sizeBuffTimer.stop();

        sizeMultiplier = multiplier;
        width = baseWidth * sizeMultiplier;

        // Giữ tâm paddle khi thay đổi kích thước
        double centerX = x + width / (2 * sizeMultiplier);
        x = Math.max(0, Math.min(centerX - width / 2, CANVAS_WIDTH - width));

        // Hết thời gian → trả lại kích thước ban đầu
        sizeBuffTimer = new Timeline(new KeyFrame(
                Duration.seconds(durationSeconds),
                e -> {
                    sizeMultiplier = 1.0;
                    width = baseWidth;

                    // Giữ paddle không vượt biên khi revert
                    x = Math.max(0, Math.min(x, CANVAS_WIDTH - width));
                }
        ));
        sizeBuffTimer.play();
    }

    public void setReverseDirection(boolean reverse) {
        this.reverseDirection = reverse;
    }

    // Getter / Setter cơ bản
    public void addKey(KeyCode code) { keys.add(code); }
    public void removeKey(KeyCode code) { keys.remove(code); }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }

    public void setWidth(double width) { this.width = width; }
    public void setHeight(double height) { this.height = height; }

}
