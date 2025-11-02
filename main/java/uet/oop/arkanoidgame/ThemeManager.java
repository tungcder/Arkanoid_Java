package uet.oop.arkanoidgame;

public class ThemeManager {

    private static String currentTheme = "Theme1";

    public static String getCurrentTheme() {
        return currentTheme;
    }

    /**
     * @param themeName: tên chủ đề.
     * Cài đặt Theme.
     */
    public static void setCurrentTheme(String themeName) {
        if (themeName == null || themeName.isEmpty()) {
            //System.err.println("Tên chủ đề không hợp lệ, giữ nguyên chủ đề: " + currentTheme);
            return;
        }
        //System.out.println("Đã đổi chủ đề thành: " + themeName);
        currentTheme = themeName;
     }

    /**
     Cấu trúc đường dẫn.
     */
    public static String getImagePath(String assetPath) {
        return "/Images/" + currentTheme + "/" + assetPath;
    }
}
