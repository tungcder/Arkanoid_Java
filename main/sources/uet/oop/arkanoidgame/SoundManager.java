package uet.oop.arkanoidgame;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;
import java.util.HashMap;

/**
 * Quản lý tất cả âm thanh trong game.
 * - Sử dụng MediaPlayer cho Music (dài, lặp lại).
 * - Sử dụng AudioClip cho SFX (ngắn, độ trễ thấp).
 */
public class SoundManager {

    // Đường dẫn thư mục âm thanh trong resources
    private static final String SOUND_DIR = "/Sounds/";

    // Bộ đệm cho SFX (dùng AudioClip)
    private final HashMap<String, AudioClip> sfxCache = new HashMap<>();

    // Bộ đệm cho đường dẫn Music (dùng MediaPlayer)
    private final HashMap<String, String> musicPaths = new HashMap<>();

    // Trình phát nhạc nền hiện tại
    private MediaPlayer currentMusicPlayer;

    public SoundManager() {
        // Tải tất cả âm thanh khi khởi tạo
        loadAllSounds();
    }

    private void loadAllSounds() {
        // 1. Tải Music (MediaPlayer)
        loadMusic("Menu", SOUND_DIR + "Menu.mp3");
        loadMusic("GameRun", SOUND_DIR + "GameRun.mp3");
        loadMusic("GameClear", SOUND_DIR + "GameClear.mp3");

        // 2. Tải SFX (AudioClip)
        loadSfx("BrickHit", SOUND_DIR + "BrickHit.mp3"); // .mp3 vẫn chạy được với AudioClip
        loadSfx("PaddleHit", SOUND_DIR + "PaddleHit.wav");
        loadSfx("LevelClear", SOUND_DIR + "LevelClear.wav");
        loadSfx("GameOver", SOUND_DIR + "GameOver.wav");
    }

    // --- Phương thức cho SFX (AudioClip) ---

    private void loadSfx(String name, String resourcePath) {
        try {
            URL resourceUrl = getClass().getResource(resourcePath);
            if (resourceUrl == null) {
                System.err.println("Không tìm thấy file SFX: " + resourcePath);
                return;
            }
            AudioClip clip = new AudioClip(resourceUrl.toExternalForm());
            sfxCache.put(name, clip);
        } catch (Exception e) {
            System.err.println("Lỗi khi tải SFX: " + resourcePath + " - " + e.getMessage());
        }
    }

    /**
     * Phát một hiệu ứng âm thanh (SFX) ngắn.
     */
    public void playSfx(String name) {
        AudioClip clip = sfxCache.get(name);
        if (clip != null) {
            clip.play();
        } else {
            System.err.println("SFX '" + name + "' chưa được tải!");
        }
    }

    // --- Phương thức cho Music (MediaPlayer) ---

    private void loadMusic(String name, String resourcePath) {
        try {
            URL resourceUrl = getClass().getResource(resourcePath);
            if (resourceUrl == null) {
                System.err.println("Không tìm thấy file Music: " + resourcePath);
                return;
            }
            musicPaths.put(name, resourceUrl.toExternalForm());
        } catch (Exception e) {
            System.err.println("Lỗi khi tải Music path: " + resourcePath + " - " + e.getMessage());
        }
    }

    /**
     * Phát một file nhạc nền.
     * @param name Tên nhạc đã tải (ví dụ: "Menu")
     * @param loop Lặp lại (true) hay phát 1 lần (false)
     */
    public void playMusic(String name, boolean loop) {
        // Dừng nhạc cũ trước khi phát nhạc mới
        stopMusic();

        String path = musicPaths.get(name);
        if (path == null) {
            System.err.println("Music '" + name + "' chưa được tải!");
            return;
        }

        try {
            Media media = new Media(path);
            currentMusicPlayer = new MediaPlayer(media);

            if (loop) {
                currentMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            }

            currentMusicPlayer.play();
        } catch (Exception e) {
            System.err.println("Lỗi khi phát music: " + name + " - " + e.getMessage());
        }
    }

    /**
     * Dừng nhạc nền đang phát.
     */
    public void stopMusic() {
        if (currentMusicPlayer != null) {
            currentMusicPlayer.stop();
            currentMusicPlayer.dispose(); // Giải phóng tài nguyên
            currentMusicPlayer = null;
        }
    }
}
