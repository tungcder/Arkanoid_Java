package uet.oop.arkanoidgame;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import uet.oop.arkanoidgame.entities.*;

public class ArkanoidGame extends Application {

    @Override
    public void start(Stage stage) {
        GamePanel panel = new GamePanel();

        Pane root = new Pane(panel);
        Scene scene = new Scene(root);
        stage.setTitle("Arkanoid JavaFX");
        stage.setScene(scene);
        stage.show();

        panel.startGame();
    }

    public static void main(String[] args) {
        launch();
    }
}
