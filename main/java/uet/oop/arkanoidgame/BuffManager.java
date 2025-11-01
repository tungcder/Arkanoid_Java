package uet.oop.arkanoidgame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class BuffManager {
    private String currentBuffName = "None";
    private int buffTimeLeft = 0;
    private Timeline buffTimer;

    private String currentDebuffName = "None";
    private int debuffTimeLeft = 0;
    private Timeline debuffTimer;

    private Runnable onUpdate; // Callback để update UI

    public BuffManager() {
    }

    public void setOnUpdate(Runnable callback) {
        this.onUpdate = callback;
    }

    // ✅ Kích hoạt buff
    public void activateBuff(String buffName, int durationSeconds) {
        // Dừng buff cũ nếu có
        if (buffTimer != null) {
            buffTimer.stop();
        }

        currentBuffName = buffName;
        buffTimeLeft = durationSeconds;

        // Tạo timer đếm ngược mỗi giây
        buffTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            buffTimeLeft--;
            if (buffTimeLeft <= 0) {
                currentBuffName = "None";
                buffTimeLeft = 0;
                buffTimer.stop();
            }
            notifyUpdate();
        }));
        buffTimer.setCycleCount(durationSeconds);
        buffTimer.play();

        notifyUpdate();
    }

    // ✅ Kích hoạt debuff
    public void activateDebuff(String debuffName, int durationSeconds) {
        // Dừng debuff cũ nếu có
        if (debuffTimer != null) {
            debuffTimer.stop();
        }

        currentDebuffName = debuffName;
        debuffTimeLeft = durationSeconds;

        // Tạo timer đếm ngược mỗi giây
        debuffTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            debuffTimeLeft--;
            if (debuffTimeLeft <= 0) {
                currentDebuffName = "None";
                debuffTimeLeft = 0;
                debuffTimer.stop();
            }
            notifyUpdate();
        }));
        debuffTimer.setCycleCount(durationSeconds);
        debuffTimer.play();

        notifyUpdate();
    }

    // ✅ Dừng tất cả buff/debuff (dùng khi pause game)
    public void pauseTimers() {
        if (buffTimer != null) buffTimer.pause();
        if (debuffTimer != null) debuffTimer.pause();
    }

    public void resumeTimers() {
        if (buffTimer != null) buffTimer.play();
        if (debuffTimer != null) debuffTimer.play();
    }

    public void stopAllTimers() {
        if (buffTimer != null) buffTimer.stop();
        if (debuffTimer != null) debuffTimer.stop();
        currentBuffName = "None";
        currentDebuffName = "None";
        buffTimeLeft = 0;
        debuffTimeLeft = 0;
    }

    private void notifyUpdate() {
        if (onUpdate != null) {
            onUpdate.run();
        }
    }

    // Getters
    public String getCurrentBuffName() {
        return currentBuffName;
    }

    public int getBuffTimeLeft() {
        return buffTimeLeft;
    }

    public String getCurrentDebuffName() {
        return currentDebuffName;
    }

    public int getDebuffTimeLeft() {
        return debuffTimeLeft;
    }
}