package uet.oop.arkanoidgame;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;
import java.util.HashMap;

/**
 * Quản lý tất cả âm thanh trong game.
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
    private String currentMusicName = null;

    // --- BIẾN QUẢN LÝ ÂM LƯỢNG ---
    // Mặc định tất cả là 100% (1.0)
    private double masterVolume = 1.0;
    private double musicVolume = 1.0;
    private double sfxVolume = 1.0;

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
        loadSfx("BrickHit", SOUND_DIR + "BrickHit.mp3");
        loadSfx("PaddleHit", SOUND_DIR + "PaddleHit.wav");
        loadSfx("LevelClear", SOUND_DIR + "LevelClear.wav");
        loadSfx("GameOver", SOUND_DIR + "GameOver.wav");
    }

    // --- Phương thức cho SFX (AudioClip) ---

    private void loadSfx(String name, String resourcePath) {
        try {
            URL resourceUrl = getClass().getResource(resourcePath);
            if (resourceUrl == null) {
                //System.err.println("Không tìm thấy file SFX: " + resourcePath);
                return;
            }
            AudioClip clip = new AudioClip(resourceUrl.toExternalForm());
            sfxCache.put(name, clip);
        } catch (Exception e) {
            //System.err.println("Lỗi khi tải SFX: " + resourcePath + " - " + e.getMessage());
        }
    }

    /**
     * Phát một hiệu ứng âm thanh (SFX) ngắn.
     */
    public void playSfx(String name) {
        AudioClip clip = sfxCache.get(name);
        if (clip != null) {
            // Tính toán âm lượng cuối cùng và phát
            clip.play(masterVolume * sfxVolume);
        } else {
            //System.err.println("SFX '" + name + "' chưa được tải!");
        }
    }

    // --- Phương thức cho Music (MediaPlayer) ---

    private void loadMusic(String name, String resourcePath) {
        try {
            URL resourceUrl = getClass().getResource(resourcePath);
            if (resourceUrl == null) {
                //System.err.println("Không tìm thấy file Music: " + resourcePath);
                return;
            }
            musicPaths.put(name, resourceUrl.toExternalForm());
        } catch (Exception e) {
            //System.err.println("Lỗi khi tải Music path: " + resourcePath + " - " + e.getMessage());
        }
    }

    /**
     * Phát một file nhạc nền.
     * @param name : tên nhạc.
     * @param loop : phát lại.
     */
    public void playMusic(String name, boolean loop) {

        // Nếu nhạc được yêu cầu đã và đang phát, không làm gì cả
        if (name.equals(currentMusicName) && currentMusicPlayer != null &&
                currentMusicPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            return; // Đã đang phát, không cần load lại
        }

        // Dừng nhạc cũ trước khi phát nhạc mới
        stopMusic();

        String path = musicPaths.get(name);
        if (path == null) {
            //System.err.println("Music '" + name + "' chưa được tải!");
            return;
        }

        try {
            Media media = new Media(path);
            currentMusicPlayer = new MediaPlayer(media);

            // Đặt âm lượng ngay khi chơi
            currentMusicPlayer.setVolume(masterVolume * musicVolume);

            if (loop) {
                currentMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            }

            currentMusicPlayer.play();
            currentMusicName = name;
        } catch (Exception e) {
            //System.err.println("Lỗi khi phát music: " + name + " - " + e.getMessage());
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
            currentMusicName = null;
        }
    }

    // --- CÁC PHƯƠNG THỨC SET/GET ÂM LƯỢNG ---

    /**
     * Cập nhật âm lượng của nhạc nền đang phát (nếu có).
     */
    private void updateCurrentMusicVolume() {
        if (currentMusicPlayer != null) {
            currentMusicPlayer.setVolume(masterVolume * musicVolume);
        }
    }

    public void setMasterVolume(double masterVolume) {
        this.masterVolume = clamp(masterVolume, 0.0, 1.0);
        updateCurrentMusicVolume(); // Cập nhật nhạc nền ngay lập tức
    }

    public void setMusicVolume(double musicVolume) {
        this.musicVolume = clamp(musicVolume, 0.0, 1.0);
        updateCurrentMusicVolume(); // Cập nhật nhạc nền ngay lập tức
    }

    public void setSfxVolume(double sfxVolume) {
        // Âm thanh SFX được đọc khi playSfx, nên chỉ cần gán
        this.sfxVolume = clamp(sfxVolume, 0.0, 1.0);
    }

    // Getters để Slider có thể đọc giá trị hiện tại
    public double getMasterVolume() {
        return masterVolume;
    }

    public double getMusicVolume() {
        return musicVolume;
    }

    public double getSfxVolume() {
        return sfxVolume;
    }

    // Hàm tiện ích
    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
