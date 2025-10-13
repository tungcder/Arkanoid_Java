package uet.oop.arkanoidgame.entities;

import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.List;

public class BrickGrid {
    private List<Brick> bricks = new ArrayList<>();

    public BrickGrid(int cols, int rows) {
        double brickWidth = 80;
        double brickHeight = 25;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                double x = col * (brickWidth + 5) + 40;
                double y = row * (brickHeight + 5) + 50;
                bricks.add(new Brick(x, y, brickWidth, brickHeight));
            }
        }
    }

    public void render(GraphicsContext gc) {
        for (Brick brick : bricks) {
            brick.render(gc);
        }
    }

    public List<Brick> getBricks() {
        return bricks;
    }
}
