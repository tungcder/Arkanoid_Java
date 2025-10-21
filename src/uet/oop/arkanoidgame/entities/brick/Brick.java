package uet.oop.arkanoidgame.entities.brick;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import uet.oop.arkanoidgame.entities.item.BiggerPaddle;
import uet.oop.arkanoidgame.entities.item.Item;

public abstract class Brick {
    private double x;
    private double y;
    private double width;
    private double height;
    private int hitsRequired;
    private int currentHits = 0;
    private boolean destroyed = false;
    private String imagePath;

    public boolean isDestroyed() {
        return destroyed;
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getWidth() {
        return width;
    }
    public double getHeight() {
        return height;
    }
    public void setDestroyed(boolean initdestroyed) {
        this.destroyed = initdestroyed;
    }

    public Brick(double initx, double inity, double initwidth, double initheight, int initHitsRequired, String initImagePath) {
        this.x = initx;
        this.y = inity;
        this.width = initwidth;
        this.height = initheight;
        this.hitsRequired = initHitsRequired;
        this.imagePath = initImagePath;
    }

    public void render(GraphicsContext gc) {
        if (!destroyed) {
            try {
                String fullPath = "/" + imagePath;  // Thêm / nếu chưa có
                Image image = new Image(getClass().getResourceAsStream(fullPath));
                gc.drawImage(image, x, y, width, height);
            } catch (NullPointerException e) {
                System.out.println("Không tìm thấy ảnh: " + imagePath);
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("Lỗi tải ảnh: " + imagePath);
                e.printStackTrace();
            }
        }
    }

    public void hit() {
        currentHits++;
        if (currentHits >= hitsRequired && hitsRequired > 0) {
            destroyed = true;
            if (Math.random() <0.3) {
                Item item;
                double rand = Math.random();
                if (rand < 0.7) {
                    item = new BiggerPaddle(x + width / 2, y);
                }
            }
        }
    }

    public int getHitsRequired() {
        return hitsRequired;
    }
}