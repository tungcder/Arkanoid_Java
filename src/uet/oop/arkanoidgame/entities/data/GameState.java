package uet.oop.arkanoidgame.entities.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Lớp lưu trữ trạng thái game để có thể save/load
 */
public class GameState {
    // Thông tin game cơ bản
    private int score;
    private int lives;
    private int elapsedSeconds;
    private int currentLevel;

    // Thông tin paddle
    private double paddleX;
    private double paddleY;
    private int paddleWidth;
    private int paddleHeight;

    // Thông tin ball
    private double ballX;
    private double ballY;
    private double ballSpeedX;
    private double ballSpeedY;
    private boolean ballAttached;
    private int ballRadius;

    // Thông tin buff/debuff
    private String buffText;
    private int buffTime;
    private String debuffText;
    private int debuffTime;

    // Thông tin bricks - lưu chi tiết trạng thái từng brick
    private String currentMapPath;
    private int activeBrickCount;
    private List<BrickData> bricksState; // Trạng thái từng viên gạch

    // Trạng thái game
    private long savedTimestamp;

    public GameState() {
        this.buffText = "None";
        this.debuffText = "None";
        this.savedTimestamp = System.currentTimeMillis();
        this.bricksState = new ArrayList<>();
    }

    // === INNER CLASS cho Brick Data ===
    public static class BrickData {
        private int row;
        private int col;
        private int health;
        private boolean active;

        public BrickData(int row, int col, int health, boolean active) {
            this.row = row;
            this.col = col;
            this.health = health;
            this.active = active;
        }

        public int getRow() { return row; }
        public int getCol() { return col; }
        public int getHealth() { return health; }
        public boolean isActive() { return active; }

        public void setRow(int row) { this.row = row; }
        public void setCol(int col) { this.col = col; }
        public void setHealth(int health) { this.health = health; }
        public void setActive(boolean active) { this.active = active; }

        @Override
        public String toString() {
            return row + "," + col + "," + health + "," + active;
        }

        public static BrickData fromString(String str) {
            String[] parts = str.split(",");
            return new BrickData(
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2]),
                    Boolean.parseBoolean(parts[3])
            );
        }
    }

    // === GETTERS ===

    public int getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

    public int getElapsedSeconds() {
        return elapsedSeconds;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public double getPaddleX() {
        return paddleX;
    }

    public double getPaddleY() {
        return paddleY;
    }

    public int getPaddleWidth() {
        return paddleWidth;
    }

    public int getPaddleHeight() {
        return paddleHeight;
    }

    public double getBallX() {
        return ballX;
    }

    public double getBallY() {
        return ballY;
    }

    public double getBallSpeedX() {
        return ballSpeedX;
    }

    public double getBallSpeedY() {
        return ballSpeedY;
    }

    public boolean isBallAttached() {
        return ballAttached;
    }

    public int getBallRadius() {
        return ballRadius;
    }

    public String getBuffText() {
        return buffText;
    }

    public int getBuffTime() {
        return buffTime;
    }

    public String getDebuffText() {
        return debuffText;
    }

    public int getDebuffTime() {
        return debuffTime;
    }

    public String getCurrentMapPath() {
        return currentMapPath;
    }

    public int getActiveBrickCount() {
        return activeBrickCount;
    }

    public long getSavedTimestamp() {
        return savedTimestamp;
    }

    public List<BrickData> getBricksState() {
        return bricksState;
    }

    // === SETTERS ===

    public void setScore(int score) {
        this.score = score;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public void setElapsedSeconds(int elapsedSeconds) {
        this.elapsedSeconds = elapsedSeconds;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public void setPaddleX(double paddleX) {
        this.paddleX = paddleX;
    }

    public void setPaddleY(double paddleY) {
        this.paddleY = paddleY;
    }

    public void setPaddleWidth(int paddleWidth) {
        this.paddleWidth = paddleWidth;
    }

    public void setPaddleHeight(int paddleHeight) {
        this.paddleHeight = paddleHeight;
    }

    public void setBallX(double ballX) {
        this.ballX = ballX;
    }

    public void setBallY(double ballY) {
        this.ballY = ballY;
    }

    public void setBallSpeedX(double ballSpeedX) {
        this.ballSpeedX = ballSpeedX;
    }

    public void setBallSpeedY(double ballSpeedY) {
        this.ballSpeedY = ballSpeedY;
    }

    public void setBallAttached(boolean ballAttached) {
        this.ballAttached = ballAttached;
    }

    public void setBallRadius(int ballRadius) {
        this.ballRadius = ballRadius;
    }

    public void setBuffText(String buffText) {
        this.buffText = buffText;
    }

    public void setBuffTime(int buffTime) {
        this.buffTime = buffTime;
    }

    public void setDebuffText(String debuffText) {
        this.debuffText = debuffText;
    }

    public void setDebuffTime(int debuffTime) {
        this.debuffTime = debuffTime;
    }

    public void setCurrentMapPath(String currentMapPath) {
        this.currentMapPath = currentMapPath;
    }

    public void setActiveBrickCount(int activeBrickCount) {
        this.activeBrickCount = activeBrickCount;
    }

    public void setSavedTimestamp(long savedTimestamp) {
        this.savedTimestamp = savedTimestamp;
    }

    public void setBricksState(List<BrickData> bricksState) {
        this.bricksState = bricksState;
    }

    @Override
    public String toString() {
        return "GameState{" +
                "score=" + score +
                ", lives=" + lives +
                ", level=" + currentLevel +
                ", elapsedSeconds=" + elapsedSeconds +
                ", paddleX=" + paddleX +
                ", paddleY=" + paddleY +
                ", ballX=" + ballX +
                ", ballY=" + ballY +
                ", ballAttached=" + ballAttached +
                ", bricksCount=" + (bricksState != null ? bricksState.size() : 0) +
                '}';
    }
}