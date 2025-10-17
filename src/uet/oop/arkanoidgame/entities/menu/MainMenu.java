package uet.oop.arkanoidgame.entities.menu;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import uet.oop.arkanoidgame.GamePanel;

public class MainMenu extends VBox {

    public MainMenu(Stage stage) {
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setStyle("-fx-background-color: black;");

        Text title = new Text("ARKANOID");
        title.setStyle("-fx-font-size: 48px; -fx-fill: cyan; -fx-font-weight: bold;");

        Button startButton = new Button("Start Game");
        Button exitButton = new Button("Exit");

        startButton.setOnAction(e -> {
            GamePanel panel = new GamePanel(stage);
            Scene gameScene = new Scene(new Pane(panel), 800, 600);
            stage.setScene(gameScene);
            panel.startGame();
            panel.requestFocus();
        });

        exitButton.setOnAction(e -> stage.close());

        getChildren().addAll(title, startButton, exitButton);
    }
}
