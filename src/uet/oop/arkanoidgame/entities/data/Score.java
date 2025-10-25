package uet.oop.arkanoidgame.entities.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Quản lý điểm số, thời gian chơi và danh sách High Score.
 * File high score được lưu tại "data.txt" ở thư mục gốc của dự án.
 */
public class Score {

    // --- Các hằng số ---
    private static final int POINTS_PER_BRICK = 10;
    private static final int MAX_HIGH_SCORES = 10;
    private static final String HIGH_SCORE_FILE = "data.txt";

    // --- Trạng thái game hiện tại ---
    private int currentScore;
    private long startTime; // Thời điểm bắt đầu màn chơi (tính bằng mili giây)
    private long elapsedTime; // Tổng thời gian đã chơi của màn (tính bằng mili giây)

    // --- High Scores ---
    private List<Integer> highScores;

    /**
     * Hàm khởi tạo, tự động tải high scores từ file.
     */
    public Score() {
        this.highScores = new ArrayList<>();
        loadHighScores();
        startNewGame(); // Chuẩn bị cho game mới
    }

    // ==================================================================
    // 1. QUẢN LÝ GAME HIỆN TẠI
    // ==================================================================

    /**
     * Bắt đầu một màn chơi mới: reset điểm và bắt đầu bấm giờ.
     */
    public void startNewGame() {
        this.currentScore = 0;
        this.elapsedTime = 0;
        startTimer();
    }

    /**
     * Được gọi khi một viên gạch bị phá vỡ.
     */
    public void brickBroken() {
        this.currentScore += POINTS_PER_BRICK;
    }

    /**
     * Bắt đầu hoặc tiếp tục bấm giờ.
     */
    public void startTimer() {
        // Lấy thời gian hiện tại của hệ thống
        this.startTime = System.currentTimeMillis();
    }

    /**
     * Dừng bấm giờ (ví dụ: khi game over hoặc pause).
     */
    public void stopTimer() {
        if (this.startTime != 0) {
            this.elapsedTime += (System.currentTimeMillis() - this.startTime);
            this.startTime = 0; // Đánh dấu là đã dừng
        }
    }

    /**
     * Lấy điểm số của màn chơi hiện tại.
     * @return điểm số
     */
    public int getCurrentScore() {
        return this.currentScore;
    }

    /**
     * Lấy tổng thời gian đã trôi qua (tính bằng giây).
     * Hàm này tính cả thời gian đang chạy (nếu timer chưa stop).
     * @return tổng thời gian chơi (giây)
     */
    public long getCurrentGameTimeInSeconds() {
        long currentTotalTime = this.elapsedTime;

        // Nếu timer đang chạy, cộng thêm khoảng thời gian từ lúc startTimer() đến giờ
        if (this.startTime != 0) {
            currentTotalTime += (System.currentTimeMillis() - this.startTime);
        }

        return currentTotalTime / 1000; // Chuyển đổi mili giây sang giây
    }

    /**
     * Lấy thời gian chơi dưới dạng chuỗi MM:SS (phút:giây).
     * Ví dụ: "01:30"
     * @return Chuỗi thời gian đã định dạng.
     */
    public String getFormattedCurrentTime() {
        long totalSeconds = getCurrentGameTimeInSeconds();
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    // ==================================================================
    // 2. QUẢN LÝ HIGH SCORE (FILE)
    // ==================================================================

    /**
     * Được gọi khi màn chơi kết thúc (thắng hoặc thua).
     * Dừng timer, kiểm tra và lưu high score.
     */
    public void recordGameEnd() {
        stopTimer();
        // Gửi điểm số cuối cùng để kiểm tra high score
        boolean newHighScore = addHighScore(this.currentScore);
        if (newHighScore) {
            saveHighScores(); // Chỉ lưu file nếu có sự thay đổi
        }
    }

    /**
     * Lấy danh sách 10 high scores cao nhất.
     * @return List<Integer> chứa các điểm số.
     */
    public List<Integer> getHighScores() {
        return Collections.unmodifiableList(this.highScores);
    }

    /**
     * Thêm một điểm số mới vào danh sách, sắp xếp, và cắt bớt nếu vượt quá 10.
     * @param newScore Điểm số mới
     * @return true nếu điểm số này được vào top 10, false nếu không.
     */
    private boolean addHighScore(int newScore) {
        if (newScore <= 0) {
            return false;
        }

        // Kiểm tra xem có đủ điều kiện vào top 10 không
        if (highScores.size() < MAX_HIGH_SCORES || newScore > highScores.get(highScores.size() - 1)) {
            highScores.add(newScore);
            // Sắp xếp danh sách giảm dần (điểm cao nhất lên đầu)
            Collections.sort(highScores, Collections.reverseOrder());

            // Giữ lại đúng MAX_HIGH_SCORES (10)
            while (highScores.size() > MAX_HIGH_SCORES) {
                highScores.remove(highScores.size() - 1); // Xóa phần tử cuối (điểm thấp nhất)
            }
            return true;
        }
        return false;
    }

    /**
     * Tải danh sách high scores từ file "data.txt".
     */
    private void loadHighScores() {
        File file = new File(HIGH_SCORE_FILE);
        this.highScores.clear();

        // Kiểm tra file tồn tại, nếu không thì tạo file mới
        if (!file.exists()) {
            try {
                file.createNewFile();
                System.out.println("Đã tạo file data.txt mới.");
                return; // Không có gì để đọc
            } catch (IOException e) {
                System.err.println("LỖI: Không thể tạo file data.txt: " + e.getMessage());
                return;
            }
        }

        // Sử dụng try-with-resources để tự động đóng Scanner
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextInt() && highScores.size() < MAX_HIGH_SCORES) {
                highScores.add(scanner.nextInt());
            }
        } catch (FileNotFoundException e) {
            System.err.println("LỖI: Không tìm thấy file data.txt: " + e.getMessage());
        }
    }

    /**
     * Lưu danh sách high scores hiện tại (đã được sắp xếp) vào file "data.txt".
     */
    private void saveHighScores() {
        // Sử dụng try-with-resources để tự động đóng PrintWriter
        try (PrintWriter writer = new PrintWriter(new FileWriter(HIGH_SCORE_FILE))) {
            for (int score : highScores) {
                writer.println(score);
            }
        } catch (IOException e) {
            System.err.println("LỖI: Không thể lưu high scores vào data.txt: " + e.getMessage());
        }
    }
}