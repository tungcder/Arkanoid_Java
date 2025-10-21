package uet.oop.arkanoidgame.entities.brick;

import javafx.scene.canvas.GraphicsContext;
import uet.oop.arkanoidgame.entities.map.MapLoader; // Import MapLoader mới
import java.util.ArrayList;
import java.util.List;

public class BrickGrid {
    private List<Brick> bricks = new ArrayList<>();
    private double brickWidth = 80;
    private double brickHeight = 25;
    private double paddingX = 5;
    private double paddingY = 5;
    private double startX = 40;
    private double startY = 50;

    public BrickGrid(String csvFilePath) {
        reset(csvFilePath);
    }

    public void reset(String csvFilePath) {
        bricks.clear();
        int[][] map = MapLoader.loadMap(csvFilePath);
        if (map == null || map.length == 0) {
            System.out.println("Không thể tải map từ " + csvFilePath);
            return;
        }

        int rows = map.length;
        int cols = map[0].length; // Giả sử tất cả dòng có cùng số cột

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int type = map[row][col];
                if (type != 0) {
                    double x = startX + col * (brickWidth + paddingX);
                    double y = startY + row * (brickHeight + paddingY);
                    Brick brick = BrickFactory.createBrick(type, x, y, brickWidth, brickHeight);
                    if (brick != null) {
                        bricks.add(brick);
                        System.out.println("Thêm gạch loại " + type + " tại (" + x + ", " + y + ")");
                    } else {
                        System.out.println("Loại gạch không hợp lệ: " + type);
                    }
                }
            }
        }
        System.out.println("Tổng số gạch tải được: " + bricks.size());
    }

    public void render(GraphicsContext gc) {
        for (Brick brick : bricks) {
            brick.render(gc);
        }
    }

    public List<Brick> getBricks() {
        return bricks;
    }

    public boolean isLevelComplete() {
        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && brick.getHitsRequired() > 0) {
                return false;
            }
        }
        return true;
    }
}