package uet.oop.arkanoidgame.entities.map;

import uet.oop.arkanoidgame.entities.brick.BrickGrid;

public class MapManager {
    private String[] levelFiles = {
            "src/main/resourse/Levels/Map1.csv" // Có thể thêm nhiều level hơn
    };
    private int currentLevel = 0;

    public void loadLevel(BrickGrid brickGrid) {
        if (currentLevel < levelFiles.length) {
            brickGrid.reset(levelFiles[currentLevel]);
            System.out.println("Tải level " + (currentLevel + 1) + ": " + levelFiles[currentLevel]);
        } else {
            System.out.println("Trò chơi hoàn thành! Không còn level nào nữa.");
        }
    }

    public void nextLevel(BrickGrid brickGrid) {
        currentLevel++;
        loadLevel(brickGrid);
    }

    public int getCurrentLevel() {
        return currentLevel + 1; // Trả về level bắt đầu từ 1
    }

    public boolean hasNextLevel() {
        return currentLevel < levelFiles.length - 1;
    }

    public void resetGame() {
        currentLevel = 0;
    }
}