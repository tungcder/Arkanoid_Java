package uet.oop.arkanoidgame;

/**
 * Lớp static (tĩnh) đơn giản để quản lý chủ đề (theme) hiện tại của game.
 * Các lớp khác (Ball, Paddle, Brick...) sẽ "hỏi" lớp này để biết
 * cần tải ảnh từ thư mục nào (ví dụ: "Theme1", "Theme2", "Theme3").
 */
public class ThemeManager {

    // "Theme1" là chủ đề mặc định khi khởi động game
    private static String currentTheme = "Theme1";

    /**
     * Lấy tên của thư mục chủ đề hiện tại (ví dụ: "Theme1")
     */
    public static String getCurrentTheme() {
        return currentTheme;
    }

    /**
     * Đặt chủ đề hiện tại.
     * Được gọi bởi các nút bấm trong SettingScreen.
     * @param themeName Tên thư mục chủ đề (ví dụ: "Theme1", "Theme2", "Theme3")
     */
    public static void setCurrentTheme(String themeName) {
        if (themeName == null || themeName.isEmpty()) {
            System.err.println("Tên chủ đề không hợp lệ, giữ nguyên chủ đề: " + currentTheme);
            return;
        }
        System.out.println("Đã đổi chủ đề thành: " + themeName);
        currentTheme = themeName;
    }

    /**
     * Hàm trợ giúp quan trọng nhất.
     * Xây dựng đường dẫn đầy đủ đến một tài nguyên (ảnh) dựa trên chủ đề hiện tại.
     *
     * Ví dụ: getImagePath("ball/ball1.png")
     * Sẽ trả về: "/Images/Theme1/ball/ball1.png" (nếu currentTheme là "Theme1")
     * Hoặc:     "/Images/Theme2/ball/ball1.png" (nếu currentTheme là "Theme2")
     *
     * @param assetPath Đường dẫn tương đối của tài nguyên (ví dụ: "ball/ball1.png")
     * @return Đường dẫn tuyệt đối trong resources (ví dụ: "/Images/Theme1/ball/ball1.png")
     */
    public static String getImagePath(String assetPath) {
        // Cấu trúc đường dẫn: /Images/[Tên Thư mục Theme]/[Tên tài sản]
        return "/Images/" + currentTheme + "/" + assetPath;
    }
}
