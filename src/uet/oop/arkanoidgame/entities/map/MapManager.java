package uet.oop.arkanoidgame.entities.map;

import uet.oop.arkanoidgame.entities.brick.BrickGrid;

public class MapManager {
    private String[] levelPaths = {
            "src/main/resources/Levels/Map1.csv",
            "src/main/resources/Levels/Map2.csv"
            // Add more levels as needed
    };
    private int currentLevel = 0;

    public void loadLevel(BrickGrid bricks) {
        if (currentLevel < levelPaths.length) {
            bricks.loadFrom(levelPaths[currentLevel]);
        }
    }

    public boolean hasNextLevel() {
        return currentLevel + 1 < levelPaths.length;
    }

    public void nextLevel(BrickGrid bricks) {
        currentLevel++;
        loadLevel(bricks);
    }

    public void resetGame() {
        currentLevel = 0;
    }

    // === Thêm các method cho Save/Load ===

    /**
     * Lấy chỉ số level hiện tại
     */
    public int getCurrentLevelIndex() {
        return currentLevel;
    }

    /**
     * Lấy đường dẫn map hiện tại
     */
    public String getCurrentMapPath() {
        if (currentLevel >= 0 && currentLevel < levelPaths.length) {
            return levelPaths[currentLevel];
        }
        return "src/main/resources/Levels/Map1.csv";
    }

    /**
     * Load level theo index cụ thể (dùng khi restore save)
     */
    public void loadLevelByIndex(BrickGrid bricks, int levelIndex) {
        if (levelIndex >= 0 && levelIndex < levelPaths.length) {
            currentLevel = levelIndex;
            bricks.loadFrom(levelPaths[currentLevel]);
        } else {
            System.err.println("Invalid level index: " + levelIndex);
            // Fallback về level 0 nếu index không hợp lệ
            currentLevel = 0;
            bricks.loadFrom(levelPaths[0]);
        }
    }
}