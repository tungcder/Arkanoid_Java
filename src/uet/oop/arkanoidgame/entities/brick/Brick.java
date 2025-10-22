package uet.oop.arkanoidgame.entities.brick;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import uet.oop.arkanoidgame.entities.item.Item;

import java.io.InputStream;

/**
 * Lớp cơ sở cho các loại Brick trong game Arkanoid.
 * Hỗ trợ load ảnh từ resources, fallback khi không tìm thấy ảnh,
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

    protected Image image;
    protected Item powerup;

    // --- Constructor ---
    public Brick(double x, double y, double width, double height, int hitsRequired, String imageFileName) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hitsRequired = hitsRequired;
        this.breakable = true;

        this.image = loadImage("/uet/oop/arkanoidgame/entities/brick/Sprites/" + imageFileName);
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

    // --- Khi bị trúng bóng ---
    public boolean hit() {
        if (!breakable || destroyed) return false;
        currentHits++;
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

    // --- Vẽ Brick ---
    public void render(GraphicsContext gc) {
        if (destroyed) return;

        if (image != null) {
            gc.drawImage(image, x, y, width, height);
        } else {
            gc.setFill(getFallbackColor());
            gc.fillRect(x, y, width, height);
            gc.setStroke(Color.BLACK);
            gc.strokeRect(x, y, width, height);
        }
    }

    // --- Màu fallback ---
    protected abstract Color getFallbackColor();

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

    public void setDestroyed(boolean b) {
    }
}
