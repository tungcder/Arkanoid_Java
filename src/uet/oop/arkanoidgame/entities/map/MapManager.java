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
}