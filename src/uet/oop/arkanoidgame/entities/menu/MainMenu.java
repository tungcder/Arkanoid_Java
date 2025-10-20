package uet.oop.arkanoidgame.entities.menu;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import uet.oop.arkanoidgame.GamePanel;

public class MainMenu extends StackPane {

    public MainMenu(Stage stage) {
        // Ảnh nền
        Image bgImage = new Image(
                getClass().getResource("/uet/oop/arkanoidgame/entities/menu/menu_images/menu_bg.png").toExternalForm()
        );
        ImageView background = new ImageView(bgImage);
        background.setFitWidth(800);
        background.setFitHeight(600);

        // Hiệu ứng đổ bóng
        DropShadow shadow = new DropShadow(15, Color.BLACK);

        // Style cơ bản
        String normalStyle = "-fx-background-color: rgba(255, 204, 0, 0.85);"
                + "-fx-text-fill: black;"
                + "-fx-font-size: 26px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 15;"
                + "-fx-padding: 10 60 10 60;";
        String hoverStyle = "-fx-background-color: linear-gradient(to bottom, #ffaa00, #ff6600);"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 26px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 15;"
                + "-fx-padding: 10 60 10 60;";

        // Các nút
        Button startBtn = new Button("START GAME");
        Button optionsBtn = new Button("OPTIONS");
        Button exitBtn = new Button("EXIT");

        startBtn.setEffect(shadow);
        optionsBtn.setEffect(shadow);
        exitBtn.setEffect(shadow);

        startBtn.setStyle(normalStyle);
        optionsBtn.setStyle(normalStyle);
        exitBtn.setStyle(normalStyle);

        // Hover
        startBtn.setOnMouseEntered(e -> startBtn.setStyle(hoverStyle));
        startBtn.setOnMouseExited(e -> startBtn.setStyle(normalStyle));

        optionsBtn.setOnMouseEntered(e -> optionsBtn.setStyle(hoverStyle));
        optionsBtn.setOnMouseExited(e -> optionsBtn.setStyle(normalStyle));

        exitBtn.setOnMouseEntered(e -> exitBtn.setStyle(hoverStyle));
        exitBtn.setOnMouseExited(e -> exitBtn.setStyle(normalStyle));

        // Hành động nút
        startBtn.setOnAction(e -> {
            GamePanel gamePanel = new GamePanel(stage);
            Scene gameScene = new Scene(new javafx.scene.layout.StackPane(gamePanel), 800, 600);
            stage.setScene(gameScene);
            gamePanel.startGame();
        });

        optionsBtn.setOnAction(e -> System.out.println("Open Options!"));
        exitBtn.setOnAction(e -> stage.close());

        // VBox chứa các nút (căn giữa)
        VBox menuBox = new VBox(25);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.getChildren().addAll(startBtn, optionsBtn, exitBtn);
        menuBox.setTranslateY(60); // dịch xuống 80px


        // Thêm tất cả vào StackPane
        getChildren().addAll(background, menuBox);
        setAlignment(Pos.CENTER);
    }

    public Scene createScene() {
        return new Scene(this, 800, 600);
    }
}
