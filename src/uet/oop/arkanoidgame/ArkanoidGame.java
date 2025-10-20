package uet.oop.arkanoidgame;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import uet.oop.arkanoidgame.entities.menu.MainMenu;

public class ArkanoidGame extends Application {

    @Override
    public void start(Stage stage) {
        // Tạo đối tượng menu
        MainMenu mainMenu = new MainMenu(stage);

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
