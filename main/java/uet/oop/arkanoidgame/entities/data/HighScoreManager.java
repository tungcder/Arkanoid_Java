package uet.oop.arkanoidgame.entities.data;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighScoreManager {
    private static final String HIGH_SCORE_FILE = "highscores.txt";
    private static final int MAX_HIGH_SCORES = 10;

    public static void saveHighScore(int score, int timeInSeconds, int levelsCompleted, boolean gameCompleted) {
        try {
            List<HighScore> scores = loadHighScores();

            HighScore newScore = new HighScore(score, timeInSeconds, levelsCompleted, gameCompleted);
            scores.add(newScore);

            Collections.sort(scores);

            if (scores.size() > MAX_HIGH_SCORES) {
                scores = scores.subList(0, MAX_HIGH_SCORES);
            }

            saveToFile(scores);

            System.out.println("✅ Đã lưu high score: " + score + " điểm");
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi lưu high score: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static List<HighScore> loadHighScores() {
        List<HighScore> scores = new ArrayList<>();

        try {
            File file = new File(HIGH_SCORE_FILE);
            if (!file.exists()) {
                return scores;
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null) {
                HighScore score = HighScore.fromFileLine(line);
                if (score != null) {
                    scores.add(score);
                }
            }

            reader.close();
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tải high scores: " + e.getMessage());
        }

        return scores;
    }

    private static void saveToFile(List<HighScore> scores) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(HIGH_SCORE_FILE));

        for (HighScore score : scores) {
            writer.write(score.toFileLine());
            writer.newLine();
        }

        writer.close();
    }

    public static void clearHighScores() {
        File file = new File(HIGH_SCORE_FILE);
        if (file.exists()) {
            file.delete();
            System.out.println("✅ Đã xóa tất cả high scores");
        }
    }

    public static boolean isHighScore(int score) {
        List<HighScore> scores = loadHighScores();
        if (scores.size() < MAX_HIGH_SCORES) {
            return true;
        }
        return score > scores.get(scores.size() - 1).getScore();
    }

    public static int getRank(int score) {
        List<HighScore> scores = loadHighScores();
        for (int i = 0; i < scores.size(); i++) {
            if (score > scores.get(i).getScore()) {
                return i + 1;
            }
        }
        return scores.size() + 1;
    }
}