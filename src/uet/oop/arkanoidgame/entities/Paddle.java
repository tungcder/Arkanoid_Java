package uet.oop.arkanoidgame.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import java.util.HashSet;
import java.util.Set;

public class Paddle {
    private double x, y, width, height;
    private double speed = 6;
    private Set<KeyCode> keys = new HashSet<>();

    public Paddle(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void update() {
        if (keys.contains(KeyCode.LEFT) && x > 0) x -= speed;
        if (keys.contains(KeyCode.RIGHT) && x + width < 800) x += speed;
    }

    public void render(GraphicsContext gc) {
        gc.setFill(Color.ORANGE);
        gc.fillRect(x, y, width, height);
    }

    public void addKey(KeyCode code) {
        keys.add(code);
    }

    public void removeKey(KeyCode code) {
        keys.remove(code);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
}
