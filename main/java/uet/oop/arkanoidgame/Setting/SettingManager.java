package uet.oop.arkanoidgame.Setting;

import uet.oop.arkanoidgame.SoundManager;
import uet.oop.arkanoidgame.ThemeManager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Lớp tĩnh quản lý việc ĐỌC và LƯU file cài đặt game.
 * Sử dụng file .properties đơn giản.
 */
public class SettingManager {

    // Tên file cài đặt
    private static final String CONFIG_FILE_NAME = "ArkanoidSettings.properties";

    // Đường dẫn đầy đủ đến file (lưu ở thư mục home của user, ví dụ: C:\Users\ADMIN)
    private static final String CONFIG_FILE_PATH = System.getProperty("user.home") + "/" + CONFIG_FILE_NAME;

    /**
     * Tải cài đặt từ file và áp dụng cho SoundManager, ThemeManager.
     * Được gọi khi game khởi động.
     */
    public static void loadSettings(SoundManager soundManager) {
        Properties props = new Properties();

        try (InputStream input = new FileInputStream(CONFIG_FILE_PATH)) {
            props.load(input);

            // 1. Tải cài đặt Âm thanh
            soundManager.setMasterVolume(
                    Double.parseDouble(props.getProperty("volume.master", "1.0"))
            );
            soundManager.setMusicVolume(
                    Double.parseDouble(props.getProperty("volume.music", "1.0"))
            );
            soundManager.setSfxVolume(
                    Double.parseDouble(props.getProperty("volume.sfx", "1.0"))
            );

            // 2. Tải cài đặt Chủ đề
            ThemeManager.setCurrentTheme(
                    props.getProperty("theme.current", "Theme1")
            );

            System.out.println("Đã tải cài đặt từ: " + CONFIG_FILE_PATH);

        } catch (IOException e) {
            // Không tìm thấy file (lần đầu chơi) hoặc file bị lỗi.
            // Không cần làm gì cả, game sẽ dùng cài đặt mặc định (1.0, Theme1).
            System.out.println("Không tìm thấy file cài đặt, sử dụng mặc định.");
        }
    }

    /**
     * Lưu cài đặt hiện tại (từ SoundManager, ThemeManager) vào file.
     * Được gọi mỗi khi người chơi thay đổi cài đặt trong SettingScreen.
     */
    public static void saveSettings(SoundManager soundManager) {
        Properties props = new Properties();

        // 1. Lấy giá trị Âm thanh hiện tại
        props.setProperty("volume.master", String.valueOf(soundManager.getMasterVolume()));
        props.setProperty("volume.music", String.valueOf(soundManager.getMusicVolume()));
        props.setProperty("volume.sfx", String.valueOf(soundManager.getSfxVolume()));

        // 2. Lấy giá trị Chủ đề hiện tại
        props.setProperty("theme.current", ThemeManager.getCurrentTheme());

        // 3. Ghi ra file
        try (OutputStream output = new FileOutputStream(CONFIG_FILE_PATH)) {
            props.store(output, "Arkanoid Game Settings");
            System.out.println("Đã lưu cài đặt vào: " + CONFIG_FILE_PATH);

        } catch (IOException e) {
            System.err.println("Lỗi khi lưu cài đặt: " + e.getMessage());
        }
    }
}