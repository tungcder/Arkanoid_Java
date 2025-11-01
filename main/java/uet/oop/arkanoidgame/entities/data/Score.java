package uet.oop.arkanoidgame.entities.data;

/**
 * Hệ thống tính điểm nâng cao cho Arkanoid Game
 * - Điểm theo loại gạch
 * - Hệ thống combo
 * - Bonus theo thời gian
 * - Điểm từ items
 */
public class Score {
    private int totalScore = 0;
    private int combo = 0;
    private int maxCombo = 0;
    private int bricksDestroyed = 0;

    // === ĐIỂM CƠ BẢN THEO LOẠI GẠCH ===
    private static final int NORMAL_BRICK_SCORE = 10;      // Gạch thường
    private static final int MEDIUM_BRICK_SCORE = 15;      // Gạch trung bình
    private static final int STRONG_BRICK_SCORE = 20;      // Gạch chắc
    private static final int MOVE_BRICK_SCORE = 30;        // Gạch di động
    private static final int POWERUP_BRICK_SCORE = 25;     // Gạch có item

    // === HỆ THỐNG COMBO ===
    private static final int COMBO_THRESHOLD = 3;          // Bắt đầu combo từ 3 viên
    private static final int COMBO_BONUS_PER_BRICK = 5;    // Mỗi viên combo +5 điểm
    private static final int MAX_COMBO_MULTIPLIER = 5;     // Combo tối đa x5

    // === ĐIỂM ITEMS ===
    private static final int ITEM_COLLECTED_SCORE = 50;    // Điểm khi ăn item
    private static final int GOOD_ITEM_BONUS = 100;        // Item tốt (buff)
    private static final int BAD_ITEM_PENALTY = -20;       // Item xấu (debuff)

    // === BONUS LEVEL ===
    private static final int LEVEL_COMPLETE_BONUS = 500;   // Hoàn thành level
    private static final int SPEED_BONUS_THRESHOLD_1 = 60; // < 60s
    private static final int SPEED_BONUS_1 = 300;          // +300 điểm
    private static final int SPEED_BONUS_THRESHOLD_2 = 120;// < 120s
    private static final int SPEED_BONUS_2 = 150;          // +150 điểm

    // === BONUS KHÔNG MẤT MẠNG ===
    private static final int NO_DEATH_BONUS = 200;         // Không mất mạng trong level

    private long levelStartTime = 0;
    private boolean levelStarted = false;
    private int livesAtStart = 0;

    public Score() {
        this.totalScore = 0;
        this.combo = 0;
        this.maxCombo = 0;
        this.bricksDestroyed = 0;
    }

    /**
     * Bắt đầu game mới
     */
    public void startNewGame() {
        this.totalScore = 0;
        this.combo = 0;
        this.maxCombo = 0;
        this.bricksDestroyed = 0;
        this.levelStarted = false;
        System.out.println("🎮 New game started!");
    }

    /**
     * Bắt đầu level mới
     */
    public void startNewLevel(int currentLives) {
        this.levelStartTime = System.currentTimeMillis();
        this.levelStarted = true;
        this.livesAtStart = currentLives;
        this.combo = 0;
        System.out.println("🎯 Level started! Lives: " + currentLives);
    }

    /**
     * Tính điểm khi phá gạch
     * @param brickType Loại gạch (NORMAL, MEDIUM, STRONG, MOVE, POWERUP)
     */
    public void brickBroken(String brickType) {
        // Điểm cơ bản theo loại gạch
        int baseScore = getBaseScoreForBrickType(brickType);

        // Tăng combo
        combo++;
        if (combo > maxCombo) {
            maxCombo = combo;
        }
        bricksDestroyed++;

        // Tính điểm combo
        int comboBonus = calculateComboBonus();

        // Tổng điểm
        int earnedScore = baseScore + comboBonus;
        totalScore += earnedScore;

        // Hiển thị thông báo
        if (combo >= COMBO_THRESHOLD) {
            System.out.println("🔥 +" + earnedScore + " điểm! (Combo x" + combo + ")");
        } else {
            System.out.println("✓ +" + earnedScore + " điểm");
        }
    }

    /**
     * Tính điểm khi phá gạch - Version đơn giản (không cần biết loại)
     */
    public void brickBroken() {
        brickBroken("NORMAL");
    }

    /**
     * Lấy điểm cơ bản theo loại gạch
     */
    private int getBaseScoreForBrickType(String brickType) {
        return switch (brickType.toUpperCase()) {
            case "MEDIUM" -> MEDIUM_BRICK_SCORE;
            case "STRONG" -> STRONG_BRICK_SCORE;
            case "MOVE" -> MOVE_BRICK_SCORE;
            case "POWERUP" -> POWERUP_BRICK_SCORE;
            default -> NORMAL_BRICK_SCORE;
        };
    }

    /**
     * Tính điểm bonus từ combo
     */
    private int calculateComboBonus() {
        if (combo < COMBO_THRESHOLD) {
            return 0;
        }

        // Công thức: (combo - threshold) * bonus * multiplier
        int comboLevel = Math.min(combo - COMBO_THRESHOLD, MAX_COMBO_MULTIPLIER);
        return comboLevel * COMBO_BONUS_PER_BRICK;
    }

    /**
     * Reset combo (khi bóng chạm paddle hoặc mất mạng)
     */
    public void resetCombo() {
        if (combo >= COMBO_THRESHOLD) {
            System.out.println("💔 Combo reset! (Đã đạt x" + combo + ")");
        }
        combo = 0;
    }

    /**
     * Ăn item (không phân biệt loại)
     */
    public void itemCollected() {
        totalScore += ITEM_COLLECTED_SCORE;
        System.out.println("⭐ Item collected! +" + ITEM_COLLECTED_SCORE + " điểm");
    }

    /**
     * Ăn item tốt (buff)
     */
    public void goodItemCollected() {
        totalScore += GOOD_ITEM_BONUS;
        System.out.println("💎 Good item! +" + GOOD_ITEM_BONUS + " điểm");
    }

    /**
     * Ăn item xấu (debuff) - trừ điểm
     */
    public void badItemCollected() {
        totalScore += BAD_ITEM_PENALTY;
        if (totalScore < 0) totalScore = 0; // Không cho điểm âm
        System.out.println("⚠️ Bad item! " + BAD_ITEM_PENALTY + " điểm");
    }

    /**
     * Tính điểm khi hoàn thành level
     */
    public void levelCompleted(int currentLives) {
        if (!levelStarted) return;

        // Bonus hoàn thành level
        totalScore += LEVEL_COMPLETE_BONUS;
        System.out.println("🎉 Level Complete! +" + LEVEL_COMPLETE_BONUS + " điểm");

        // Bonus theo thời gian
        long elapsedTime = (System.currentTimeMillis() - levelStartTime) / 1000;
        int timeBonus = calculateTimeBonus(elapsedTime);
        if (timeBonus > 0) {
            totalScore += timeBonus;
            System.out.println("⚡ Speed bonus! +" + timeBonus + " điểm (Hoàn thành trong " + elapsedTime + "s)");
        }

        // Bonus không mất mạng
        if (currentLives >= livesAtStart) {
            totalScore += NO_DEATH_BONUS;
            System.out.println("❤️ No death bonus! +" + NO_DEATH_BONUS + " điểm");
        }

        // Bonus max combo
        if (maxCombo >= 10) {
            int comboBonus = maxCombo * 10;
            totalScore += comboBonus;
            System.out.println("🔥 Max combo bonus! +" + comboBonus + " điểm (x" + maxCombo + ")");
        }

        System.out.println("📊 Total Score: " + totalScore);

        // Reset cho level tiếp theo
        levelStarted = false;
        combo = 0;
    }

    /**
     * Tính bonus theo thời gian hoàn thành level
     */
    private int calculateTimeBonus(long seconds) {
        if (seconds < SPEED_BONUS_THRESHOLD_1) {
            return SPEED_BONUS_1;
        } else if (seconds < SPEED_BONUS_THRESHOLD_2) {
            return SPEED_BONUS_2;
        }
        return 0;
    }

    /**
     * Ghi nhận kết thúc game
     */
    public void recordGameEnd() {
        System.out.println("=================================");
        System.out.println("🏆 GAME STATISTICS");
        System.out.println("=================================");
        System.out.println("Final Score: " + totalScore);
        System.out.println("Bricks Destroyed: " + bricksDestroyed);
        System.out.println("Max Combo: x" + maxCombo);
        System.out.println("=================================");
    }

    // === GETTERS ===
    public int getScore() {
        return totalScore;
    }

    public int getCombo() {
        return combo;
    }

    public int getMaxCombo() {
        return maxCombo;
    }

    public int getBricksDestroyed() {
        return bricksDestroyed;
    }

    // === SETTERS (cho save/load) ===
    public void setScore(int score) {
        this.totalScore = score;
    }

    public void setCombo(int combo) {
        this.combo = combo;
    }

    public void setMaxCombo(int maxCombo) {
        this.maxCombo = maxCombo;
    }

    public void setBricksDestroyed(int bricksDestroyed) {
        this.bricksDestroyed = bricksDestroyed;
    }
}