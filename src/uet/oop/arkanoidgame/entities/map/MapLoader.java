package uet.oop.arkanoidgame.entities.map;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MapLoader {
    // Map thủ công mặc định nếu không load được file CSV
    private static int[][] getDefaultMap() {
        return new int[][] {
                {0, 0, 1, 1, 1, 0, 0},
                {0, 2, 2, 3, 2, 2, 0},
                {1, 1, 1, 4, 1, 1, 1},
                {0, 0, 2, 2, 2, 0, 0}
        };
    }

    public static int[][] loadMap(String filePath) {
        List<List<Integer>> tempMap = new ArrayList<>();
        String fullPath = "/" + filePath;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(MapLoader.class.getResourceAsStream(fullPath)))) {
            String line;
            int maxCols = 0;
            while ((line = br.readLine()) != null) {
                System.out.println("Đọc dòng CSV: " + line);
                String[] values = line.split(",");
                List<Integer> row = new ArrayList<>();
                for (String val : values) {
                    String trimmed = val.trim();
                    try {
                        row.add(Integer.parseInt(trimmed));
                    } catch (NumberFormatException e) {
                        System.out.println("Định dạng số không hợp lệ: " + trimmed);
                        return getDefaultMap(); // Trả về map mặc định nếu có lỗi
                    }
                }
                if (!row.isEmpty()) {
                    tempMap.add(row);
                    maxCols = Math.max(maxCols, row.size());
                }
            }

            if (tempMap.isEmpty()) {
                System.out.println("File CSV trống: " + fullPath + ". Sử dụng map mặc định.");
                return getDefaultMap();
            }

            // Chuyển sang mảng 2D, padding nếu cần
            int rows = tempMap.size();
            int[][] map = new int[rows][maxCols];
            for (int i = 0; i < rows; i++) {
                List<Integer> rowList = tempMap.get(i);
                for (int j = 0; j < rowList.size(); j++) {
                    map[i][j] = rowList.get(j);
                }
                // Padding với 0 nếu dòng ngắn hơn maxCols
                for (int j = rowList.size(); j < maxCols; j++) {
                    map[i][j] = 0;
                }
            }
            return map;
        } catch (IOException e) {
            System.out.println("Lỗi IO khi tải CSV: " + fullPath + ". Sử dụng map mặc định.");
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.out.println("Không tìm thấy file CSV: " + fullPath + ". Sử dụng map mặc định.");
            e.printStackTrace();
        }
        return getDefaultMap(); // Trả về map mặc định nếu có lỗi
    }
}