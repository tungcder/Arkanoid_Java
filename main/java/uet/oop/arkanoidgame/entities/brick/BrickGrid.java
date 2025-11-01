package uet.oop.arkanoidgame.entities.brick;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import javafx.scene.canvas.GraphicsContext;
import uet.oop.arkanoidgame.entities.data.GameState;

public class BrickGrid {
    private List<Brick> bricks = new ArrayList<>();
    private static final double BRICK_WIDTH = 100;
    private static final double BRICK_HEIGHT = 50;
    private static final double GRID_OFFSET_X = 0;
    private static final double GRID_OFFSET_Y = 50;

    public BrickGrid(String csvPath) {
        loadFrom(csvPath);
    }

    private void updateMovingBricks() {
        for (Brick brick : bricks) {
            if (brick instanceof BrickMove) {
                ((BrickMove) brick).initMovementRange(this);
            }
        }
    }

    public void loadFrom(String csvPath) {
        bricks.clear();

        // ✅ FIX: Dùng getResourceAsStream thay vì FileReader
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream(csvPath)))) {

            if (br == null) {
                System.err.println("❌ Cannot find CSV file: " + csvPath);
                return;
            }

            String line;
            int row = 0;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                for (int col = 0; col < values.length; col++) {
                    int type = Integer.parseInt(values[col].trim());
                    if (type != 0) {
                        double x = GRID_OFFSET_X + col * BRICK_WIDTH;
                        double y = GRID_OFFSET_Y + row * BRICK_HEIGHT;
                        Brick brick = BrickFactory.createBrick(type, x, y, BRICK_WIDTH, BRICK_HEIGHT);
                        bricks.add(brick);
                    }
                }
                row++;
            }

            // ✅ Debug: In ra số gạch đã load
            System.out.println("✅ Loaded " + bricks.size() + " bricks from " + csvPath);

        } catch (IOException | NumberFormatException | NullPointerException e) {
            System.err.println("❌ Error loading CSV: " + e.getMessage());
            e.printStackTrace();
        }
        updateMovingBricks();
    }

    public void render(GraphicsContext gc) {
        for (Brick brick : bricks) {
            brick.render(gc);
        }
    }

    public void update() {
        for (Brick brick : bricks) {
            brick.update();
        }
    }

    public boolean isLevelComplete() {
        int breakableLeft = 0;
        int unbreakableLeft = 0;

        for (Brick brick : bricks) {
            if (!brick.isDestroyed()) {
                if (brick.isBreakable()) {
                    breakableLeft++;
                } else {
                    unbreakableLeft++;
                }
            }
        }

        System.out.println("🔍 Breakable bricks: " + breakableLeft + " | Unbreakable: " + unbreakableLeft);
        return breakableLeft == 0;
    }

    public List<Brick> getBricks() {
        return bricks;
    }

    public int getActiveBrickCount() {
        int count = 0;
        for (Brick b : this.bricks) {
            if (!b.isDestroyed() && b.isBreakable()) {  // ✅ THÊM && b.isBreakable()
                count++;
            }
        }
        return count;
    }

    // ============================================
    // METHODS ĐỂ LƯU/TẢI TRẠNG THÁI BRICKS
    // ============================================

    /**
     * Lấy trạng thái tất cả các bricks để lưu vào save file
     * @return List chứa trạng thái từng brick
     */
    public List<GameState.BrickData> getBricksStateForSave() {
        List<GameState.BrickData> bricksData = new ArrayList<>();

        for (int i = 0; i < bricks.size(); i++) {
            Brick brick = bricks.get(i);

            // Tính row, col từ vị trí x, y
            int col = (int) Math.round((brick.getX() - GRID_OFFSET_X) / BRICK_WIDTH);
            int row = (int) Math.round((brick.getY() - GRID_OFFSET_Y) / BRICK_HEIGHT);

            // Lưu thông tin brick: row, col, health (số lần đập còn lại), active (còn hay đã vỡ)
            GameState.BrickData data = new GameState.BrickData(
                    row,
                    col,
                    brick.getHealth(),
                    !brick.isDestroyed()
            );

            bricksData.add(data);
        }

        System.out.println("✓ Đã lấy trạng thái " + bricksData.size() + " bricks để lưu");
        return bricksData;
    }

    /**
     * Khôi phục trạng thái bricks từ save file
     * @param savedBricksData Danh sách trạng thái bricks đã lưu
     */
    public void restoreBricksState(List<GameState.BrickData> savedBricksData) {
        if (savedBricksData == null || savedBricksData.isEmpty()) {
            System.out.println("⚠ Không có dữ liệu bricks để khôi phục");
            return;
        }

        // Tạo map để tra cứu nhanh brick theo row, col
        java.util.Map<String, GameState.BrickData> savedMap = new java.util.HashMap<>();
        for (GameState.BrickData data : savedBricksData) {
            String key = data.getRow() + "," + data.getCol();
            savedMap.put(key, data);
        }

        // Duyệt qua tất cả bricks hiện tại và khôi phục trạng thái
        int restoredCount = 0;
        int destroyedCount = 0;

        for (Brick brick : bricks) {
            // Tính row, col của brick hiện tại
            int col = (int) Math.round((brick.getX() - GRID_OFFSET_X) / BRICK_WIDTH);
            int row = (int) Math.round((brick.getY() - GRID_OFFSET_Y) / BRICK_HEIGHT);
            String key = row + "," + col;

            // Kiểm tra xem brick này có trong save data không
            GameState.BrickData savedData = savedMap.get(key);
            if (savedData != null) {
                // Khôi phục health (số lần đập còn lại)
                brick.setHealth(savedData.getHealth());

                // Khôi phục trạng thái destroyed
                if (!savedData.isActive()) {
                    brick.destroy();
                    destroyedCount++;
                } else {
                    restoredCount++;
                }
            }
        }

        System.out.println("✓ Đã khôi phục " + restoredCount + " bricks còn nguyên, " + destroyedCount + " bricks đã vỡ");
    }
}