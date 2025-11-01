package uet.oop.arkanoidgame.entities.brick;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import uet.oop.arkanoidgame.entities.item.Item;
import uet.oop.arkanoidgame.ThemeManager;

import java.io.InputStream;

/**
 * Lớp cơ sở cho các loại Brick trong game Arkanoid.
 * Hỗ trợ load nhiều trạng thái ảnh nứt (damage states), fallback khi không tìm thấy ảnh,
 * và cho phép định nghĩa gạch không vỡ (unbreakable).
 */
public abstract class Brick {

    // --- Thuộc tính cơ bản ---
    protected double x, y;
    protected double width, height;

    protected int hitsRequired;
    protected int currentHits = 0;
    protected boolean destroyed = false;
    protected boolean breakable = true; // Mặc định gạch có thể phá

    protected Image[] damageImages; // Mảng ảnh: [0]=nguyên, [1]=nứt1, [2]=nứt2,...
    protected Image image; // Ảnh hiện tại (để tương thích cũ)
    protected Item powerup;

    // --- Constructor mới hỗ trợ nhiều ảnh ---
    public Brick(double x, double y, double width, double height, int hitsRequired, String... imageFileNames) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hitsRequired = hitsRequired;
        this.breakable = true;

        // Load tất cả ảnh damage states
        this.damageImages = new Image[imageFileNames.length];
        for (int i = 0; i < imageFileNames.length; i++) {
            String assetPath = "brick/" + imageFileNames[i];
            this.damageImages[i] = loadImage(ThemeManager.getImagePath(assetPath));
        }
        this.image = damageImages[0]; // Mặc định ảnh nguyên vẹn
    }

    // --- Load ảnh ---
    protected static Image loadImage(String fullPath) {
        try (InputStream inputStream = Brick.class.getResourceAsStream(fullPath)) {
            if (inputStream == null) {
                System.err.println("⚠ Không tìm thấy tài nguyên: " + fullPath);
                return null;
            }
            Image img = new Image(inputStream);
            if (img.isError()) {
                System.err.println("⚠ Lỗi khi tải ảnh: " + fullPath);
                return null;
            }
            return img;
        } catch (Exception e) {
            System.err.println("⚠ Lỗi I/O khi tải ảnh: " + fullPath);
            e.printStackTrace();
            return null;
        }
    }

    // --- Khi bị trúng bóng - cập nhật ảnh nứt ---
    public boolean hit() {
        if (!breakable || destroyed) return false;
        currentHits++;

        // Cập nhật ảnh theo số lần bị đánh
        if (damageImages != null && currentHits < hitsRequired) {
            image = damageImages[Math.min(currentHits, damageImages.length - 1)];
        }

        if (currentHits >= hitsRequired) {
            destroyed = true;
            onDestroyed();
            return true;
        }
        return false;
    }

    // --- Khi bị phá (override ở lớp con nếu cần) ---
    protected void onDestroyed() {
        // Tạo powerup hoặc hiệu ứng khi brick vỡ
    }

    // --- Vẽ Brick - dùng ảnh hiện tại ---
    public void render(GraphicsContext gc) {
        if (destroyed) return;

        if (image != null) {
            gc.drawImage(image, x, y, width, height);
        } else {
            // Fallback: vẽ màu nếu không có ảnh
            gc.setFill(getFallbackColor());
            gc.fillRect(x, y, width, height);
            gc.setStroke(Color.BLACK);
            gc.strokeRect(x, y, width, height);
        }
    }

    // --- Màu fallback ---
    protected abstract Color getFallbackColor();

    public void update() {
        // Các brick thông thường không di chuyển
    }

    // --- Getter/Setter ---
    public boolean isDestroyed() { return destroyed; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public int getHitsRequired() { return hitsRequired; }

    public boolean isBreakable() { return breakable; }
    public void setBreakable(boolean value) { this.breakable = value; }

    public Item getPowerup() { return powerup; }
    public void setPowerup(Item powerup) { this.powerup = powerup; }
}