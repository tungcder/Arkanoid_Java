package uet.oop.arkanoidgame.entities.brick;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javafx.scene.canvas.GraphicsContext;

public class BrickGrid {
    private List<Brick> bricks = new ArrayList<>();
    private static final double BRICK_WIDTH = 100;
    private static final double BRICK_HEIGHT = 50;
    private static final double GRID_OFFSET_X = 0;
    private static final double GRID_OFFSET_Y = 50;

    public BrickGrid(String csvPath) {
        loadFrom(csvPath);
    }

    public void loadFrom(String csvPath) {
        bricks.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String line;
            int row = 0;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                for (int col = 0; col < values.length; col++) {
                    int type = Integer.parseInt(values[col].trim());
                    if (type != 0) {
                        double x = GRID_OFFSET_X + col * BRICK_WIDTH;
                        double y = GRID_OFFSET_Y + row * BRICK_HEIGHT;
                        Brick brick = BrickFactory.createBrick(type, x, y, BRICK_WIDTH, BRICK_HEIGHT);
                        bricks.add(brick);
                    }
                }
                row++;
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading CSV: " + e.getMessage());
        }
    }

    public void render(GraphicsContext gc) {
        for (Brick brick : bricks) {
            brick.render(gc);
        }
    }

    public boolean isLevelComplete() {
        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && brick.isBreakable()) {
                return false;
            }
        }
        return true;
    }

    public List<Brick> getBricks() {
        return bricks;
    }
}