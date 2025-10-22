package uet.oop.arkanoidgame.entities.item;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import uet.oop.arkanoidgame.entities.paddle.Paddle;
import java.util.Objects;

/**
 * Abstract base class for items (buffs/debuffs) in the Arkanoid game.
 */
public abstract class Item {
    protected double x, y, width = 30, height = 30;
    protected double fallSpeed = 2.0;
    protected Image image;

    public Item(double x, double y, String imagePath) {
        this.x = x - width / 2; // Center on drop position
        this.y = y;
        try {
            this.image = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream(imagePath)
            ));
        } catch (NullPointerException e) {
            System.err.println("Cannot load item image: " + imagePath);
        }
    }

    /**
     * Updates the item position (falls down).
     */
    public void update() {
        y += fallSpeed;
    }

    /**
     * Renders the item.
     * @param gc GraphicsContext.
     */
    public void render(GraphicsContext gc) {
        if (image != null) {
            gc.drawImage(image, x, y, width, height);
        }
    }

    /**
     * Checks collision with paddle.
     * @param paddle The paddle.
     * @return true if collides.
     */
    public boolean collidesWith(Paddle paddle) {
        return x < paddle.getX() + paddle.getWidth() &&
                x + width > paddle.getX() &&
                y < paddle.getY() + paddle.getHeight() &&
                y + height > paddle.getY();
    }

    /**
     * Applies the item effect to the paddle.
     * @param paddle The paddle to apply to.
     */
    public abstract void apply(Paddle paddle);

    // Getters
    public double getY() { return y; }
}