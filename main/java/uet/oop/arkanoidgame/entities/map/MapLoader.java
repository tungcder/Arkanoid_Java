package uet.oop.arkanoidgame.entities.map;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MapLoader {

    // Map mặc định – chỉ tạo 1 lần
    private static final int[][] DEFAULT_MAP = {
            {0, 0, 1, 1, 1, 0, 0},
            {0, 2, 2, 3, 2, 2, 0},
            {1, 1, 1, 4, 1, 1, 1},
            {0, 0, 2, 2, 2, 0, 0}
    };

    public static int[][] loadMap(String filePath) {
        String fullPath = "/" + filePath;
        List<int[]> rows = new ArrayList<>();
        int maxCols = 0;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(MapLoader.class.getResourceAsStream(fullPath)))) {

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue; // Bỏ qua dòng trống
                }

                System.out.println("Đọc dòng CSV: " + line);
                String[] values = line.split(",");

                int[] row = new int[values.length];
                for (int i = 0; i < values.length; i++) {
                    String val = values[i].trim();
                    try {
                        row[i] = Integer.parseInt(val);
                    } catch (NumberFormatException e) {
                        System.out.println("Lỗi định dạng số: '" + val + "' → Dùng map mặc định.");
                        return DEFAULT_MAP;
                    }
                }

                rows.add(row);
                maxCols = Math.max(maxCols, row.length);
            }

            if (rows.isEmpty()) {
                System.out.println("File CSV trống: " + fullPath + " → Dùng map mặc định.");
                return DEFAULT_MAP;
            }

            // Tạo mảng 2D + padding 0
            int[][] map = new int[rows.size()][maxCols];
            for (int i = 0; i < rows.size(); i++) {
                int[] src = rows.get(i);
                System.arraycopy(src, 0, map[i], 0, src.length);
                for (int j = src.length; j < maxCols; j++) {
                    map[i][j] = 0;
                }
            }

            System.out.println("Tải map thành công: " + rows.size() + "x" + maxCols);
            return map;

        } catch (IOException e) {
            System.out.println("Lỗi đọc file: " + fullPath + " → Dùng map mặc định.");
            return DEFAULT_MAP;
        }
    }
}