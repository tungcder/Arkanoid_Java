package uet.oop.arkanoidgame;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import uet.oop.arkanoidgame.entities.menu.MainMenu;
import uet.oop.arkanoidgame.Setting.SettingManager;

public class ArkanoidGame extends Application {

    public static final SoundManager soundManager = new SoundManager();

    @Override
    public void start(Stage stage) {

        // Tải cài đặt đã lưu trước khi bắt đầu
        SettingManager.loadSettings(soundManager);

        // Tạo đối tượng menu
        MainMenu mainMenu = new MainMenu(stage, soundManager);

        // Tạo scene cho menu
        Scene menuScene = new Scene(mainMenu, 800, 600);

        // Gắn scene menu vào stage
        stage.setTitle("Arkanoid - Main Menu");
        stage.setScene(menuScene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
