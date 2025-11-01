package uet.oop.arkanoidgame.entities.data;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Quản lý việc lưu và tải trạng thái game vào file text
 */
public class GameSaveManager {
    private static final String SAVE_FILE = "gamesave.txt";
    private static final String DELIMITER = "=";
    private static final String BRICK_DELIMITER = ";";

    /**
     * Lưu trạng thái game vào file
     */
    public static boolean saveGame(GameState state) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE))) {
            // Ghi thông tin game cơ bản
            writer.write("score" + DELIMITER + state.getScore());
            writer.newLine();
            writer.write("lives" + DELIMITER + state.getLives());
            writer.newLine();
            writer.write("elapsedSeconds" + DELIMITER + state.getElapsedSeconds());
            writer.newLine();
            writer.write("currentLevel" + DELIMITER + state.getCurrentLevel());
            writer.newLine();

            // Ghi thông tin paddle
            writer.write("paddleX" + DELIMITER + state.getPaddleX());
            writer.newLine();
            writer.write("paddleY" + DELIMITER + state.getPaddleY());
            writer.newLine();
            writer.write("paddleWidth" + DELIMITER + state.getPaddleWidth());
            writer.newLine();
            writer.write("paddleHeight" + DELIMITER + state.getPaddleHeight());
            writer.newLine();

            // Ghi thông tin ball
            writer.write("ballX" + DELIMITER + state.getBallX());
            writer.newLine();
            writer.write("ballY" + DELIMITER + state.getBallY());
            writer.newLine();
            writer.write("ballSpeedX" + DELIMITER + state.getBallSpeedX());
            writer.newLine();
            writer.write("ballSpeedY" + DELIMITER + state.getBallSpeedY());
            writer.newLine();
            writer.write("ballAttached" + DELIMITER + state.isBallAttached());
            writer.newLine();
            writer.write("ballRadius" + DELIMITER + state.getBallRadius());
            writer.newLine();

            // Ghi thông tin buff/debuff
            writer.write("buffText" + DELIMITER + state.getBuffText());
            writer.newLine();
            writer.write("buffTime" + DELIMITER + state.getBuffTime());
            writer.newLine();
            writer.write("debuffText" + DELIMITER + state.getDebuffText());
            writer.newLine();
            writer.write("debuffTime" + DELIMITER + state.getDebuffTime());
            writer.newLine();

            // Ghi thông tin map
            writer.write("currentMapPath" + DELIMITER + state.getCurrentMapPath());
            writer.newLine();
            writer.write("activeBrickCount" + DELIMITER + state.getActiveBrickCount());
            writer.newLine();

            // Ghi timestamp
            writer.write("savedTimestamp" + DELIMITER + state.getSavedTimestamp());
            writer.newLine();

            // Ghi trạng thái bricks - QUAN TRỌNG!
            List<GameState.BrickData> bricksState = state.getBricksState();
            if (bricksState != null && !bricksState.isEmpty()) {
                writer.write("bricksCount" + DELIMITER + bricksState.size());
                writer.newLine();

                // Mỗi brick được lưu trên 1 dòng: row,col,health,active
                for (GameState.BrickData brick : bricksState) {
                    String brickLine = "brick" + DELIMITER + brick.toString();
                    writer.write(brickLine);
                    writer.newLine();
                }
            } else {
                writer.write("bricksCount" + DELIMITER + "0");
                writer.newLine();
            }

            System.out.println("✓ Game đã lưu thành công vào " + SAVE_FILE);
            return true;

        } catch (IOException e) {
            System.err.println("❌ Lỗi khi lưu game: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Tải trạng thái game từ file
     */
    public static GameState loadGame() {
        if (!hasSavedGame()) {
            System.out.println("⚠ Không tìm thấy file save");
            return null;
        }

        GameState state = new GameState();
        List<GameState.BrickData> bricksList = new ArrayList<>();
        int expectedBricksCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(SAVE_FILE))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(DELIMITER, 2);
                if (parts.length != 2) continue;

                String key = parts[0].trim();
                String value = parts[1].trim();

                // Parse các giá trị
                switch (key) {
                    case "score":
                        state.setScore(Integer.parseInt(value));
                        break;
                    case "lives":
                        state.setLives(Integer.parseInt(value));
                        break;
                    case "elapsedSeconds":
                        state.setElapsedSeconds(Integer.parseInt(value));
                        break;
                    case "currentLevel":
                        state.setCurrentLevel(Integer.parseInt(value));
                        break;
                    case "paddleX":
                        state.setPaddleX(Double.parseDouble(value));
                        break;
                    case "paddleY":
                        state.setPaddleY(Double.parseDouble(value));
                        break;
                    case "paddleWidth":
                        state.setPaddleWidth(Integer.parseInt(value));
                        break;
                    case "paddleHeight":
                        state.setPaddleHeight(Integer.parseInt(value));
                        break;
                    case "ballX":
                        state.setBallX(Double.parseDouble(value));
                        break;
                    case "ballY":
                        state.setBallY(Double.parseDouble(value));
                        break;
                    case "ballSpeedX":
                        state.setBallSpeedX(Double.parseDouble(value));
                        break;
                    case "ballSpeedY":
                        state.setBallSpeedY(Double.parseDouble(value));
                        break;
                    case "ballAttached":
                        state.setBallAttached(Boolean.parseBoolean(value));
                        break;
                    case "ballRadius":
                        state.setBallRadius(Integer.parseInt(value));
                        break;
                    case "buffText":
                        state.setBuffText(value);
                        break;
                    case "buffTime":
                        state.setBuffTime(Integer.parseInt(value));
                        break;
                    case "debuffText":
                        state.setDebuffText(value);
                        break;
                    case "debuffTime":
                        state.setDebuffTime(Integer.parseInt(value));
                        break;
                    case "currentMapPath":
                        state.setCurrentMapPath(value);
                        break;
                    case "activeBrickCount":
                        state.setActiveBrickCount(Integer.parseInt(value));
                        break;
                    case "savedTimestamp":
                        state.setSavedTimestamp(Long.parseLong(value));
                        break;
                    case "bricksCount":
                        expectedBricksCount = Integer.parseInt(value);
                        break;
                    case "brick":
                        // Parse brick data: row,col,health,active
                        try {
                            GameState.BrickData brick = GameState.BrickData.fromString(value);
                            bricksList.add(brick);
                        } catch (Exception e) {
                            System.err.println("⚠ Lỗi parse brick data: " + value);
                        }
                        break;
                }
            }

            // Set bricks state vào game state
            state.setBricksState(bricksList);

            System.out.println("✓ Game đã load thành công từ " + SAVE_FILE);
            System.out.println("  - Đã load " + bricksList.size() + "/" + expectedBricksCount + " bricks");
            return state;

        } catch (IOException | NumberFormatException e) {
            System.err.println("❌ Lỗi khi load game: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Kiểm tra xem có file save không
     */
    public static boolean hasSavedGame() {
        return Files.exists(Paths.get(SAVE_FILE));
    }

    /**
     * Xóa file save
     */
    public static boolean deleteSave() {
        try {
            if (hasSavedGame()) {
                Files.delete(Paths.get(SAVE_FILE));
                System.out.println("✓ Đã xóa file save: " + SAVE_FILE);
                return true;
            }
            return false;
        } catch (IOException e) {
            System.err.println("❌ Lỗi khi xóa save: " + e.getMessage());
            return false;
        }
    }
}