package uet.oop.arkanoidgame.entities.data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HighScore implements Serializable, Comparable<HighScore> {
    private static final long serialVersionUID = 1L;

    private int score;
    private int timeInSeconds;
    private String dateTime;
    private int levelsCompleted;
    private boolean gameCompleted;

    public HighScore(int score, int timeInSeconds, int levelsCompleted, boolean gameCompleted) {
        this.score = score;
        this.timeInSeconds = timeInSeconds;
        this.dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        this.levelsCompleted = levelsCompleted;
        this.gameCompleted = gameCompleted;
    }

    public HighScore(int score, int timeInSeconds, String dateTime, int levelsCompleted, boolean gameCompleted) {
        this.score = score;
        this.timeInSeconds = timeInSeconds;
        this.dateTime = dateTime;
        this.levelsCompleted = levelsCompleted;
        this.gameCompleted = gameCompleted;
    }

    public int getScore() {
        return score;
    }

    public int getTimeInSeconds() {
        return timeInSeconds;
    }

    public String getDateTime() {
        return dateTime;
    }

    public int getLevelsCompleted() {
        return levelsCompleted;
    }

    public boolean isGameCompleted() {
        return gameCompleted;
    }

    public String getFormattedTime() {
        int minutes = timeInSeconds / 60;
        int seconds = timeInSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public String getFormattedDate() {
        return dateTime;
    }

    @Override
    public int compareTo(HighScore other) {
        return Integer.compare(other.score, this.score);
    }

    public String toFileLine() {
        return score + "," + timeInSeconds + "," + dateTime + "," + levelsCompleted + "," + gameCompleted;
    }

    public static HighScore fromFileLine(String line) {
        try {
            String[] parts = line.split(",");
            if (parts.length == 5) {
                return new HighScore(
                        Integer.parseInt(parts[0]),
                        Integer.parseInt(parts[1]),
                        parts[2],
                        Integer.parseInt(parts[3]),
                        Boolean.parseBoolean(parts[4])
                );
            }
        } catch (Exception e) {
            System.err.println("Lỗi đọc dòng: " + line);
        }
        return null;
    }
}