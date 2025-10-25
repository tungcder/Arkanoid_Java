[33mcommit ed87001f204ff2f107574c6c8959dc2bc818ea4c[m[33m ([m[1;36mHEAD[m[33m -> [m[1;32mmain[m[33m, [m[1;31morigin/main[m[33m, [m[1;31morigin/HEAD[m[33m)[m
Author: ngocthai30042k6 <ngocthai30042k6@gmail.com>
Date:   Sat Oct 25 09:51:53 2025 +0700

    Fix Item: Ko Load dc Image, Thieu ham

[1mdiff --git a/src/main/resources/Levels/Map1.csv b/src/main/resources/Levels/Map1.csv[m
[1mindex 12cb0a6..c2a1f30 100644[m
[1m--- a/src/main/resources/Levels/Map1.csv[m
[1m+++ b/src/main/resources/Levels/Map1.csv[m
[36m@@ -1,4 +1,3 @@[m
 1,1,1,1,1,1,1,1,1,1[m
 1,1,1,1,1,1,1,1,1,1[m
 1,1,1,1,1,1,1,1,1,1[m
[31m-1,1,1,1,1,1,1,1,1,1[m
[1mdiff --git a/src/uet/oop/arkanoidgame/GamePanel.java b/src/uet/oop/arkanoidgame/GamePanel.java[m
[1mindex 0775640..660cce0 100644[m
[1m--- a/src/uet/oop/arkanoidgame/GamePanel.java[m
[1m+++ b/src/uet/oop/arkanoidgame/GamePanel.java[m
[36m@@ -19,10 +19,10 @@[m [mimport uet.oop.arkanoidgame.entities.paddle.Paddle;[m
 import uet.oop.arkanoidgame.entities.menu.MainMenu;[m
 import uet.oop.arkanoidgame.entities.item.Item;[m
 [m
[32m+[m[32mimport java.net.URL;[m
 import java.util.ArrayList;[m
 import java.util.Iterator;[m
 import java.util.List;[m
[31m-import java.util.Objects;[m
 [m
 public class GamePanel extends Canvas {[m
 [m
[36m@@ -93,6 +93,8 @@[m [mpublic class GamePanel extends Canvas {[m
                         resetForNextLevel();[m
                     } else if (bricks.isLevelComplete()) {[m
                         gameRunning = false;[m
[32m+[m[32m                        // Hiện màn complete ngay[m
[32m+[m[32m                        stop();[m
                         showGameCompleteScreen();[m
                     }[m
                 } else {[m
[36m@@ -135,11 +137,13 @@[m [mpublic class GamePanel extends Canvas {[m
             if (playerLives > 0) {[m
                 // Gắn lại bóng vào paddle (đợi người chơi click để thả)[m
                 ball.attachToPaddle(paddle);[m
[31m-                // (tuỳ chọn) xoá buff tốc độ/nổ nếu muốn cân bằng:[m
[31m-                // ball.applySpeedBuff(1.0, 0);[m
[31m-                // ball.applyExplosiveBuff(0, 0);[m
[32m+[m[32m                // (optional) clear items nếu muốn[m
[32m+[m[32m                // items.clear();[m
             } else {[m
[32m+[m[32m                // Hết mạng → dừng ngay & show Game Over[m
                 gameRunning = false;[m
[32m+[m[32m                if (timer != null) timer.stop();[m
[32m+[m[32m                showGameOverScreen();[m
             }[m
         }[m
     }[m
[36m@@ -157,14 +161,14 @@[m [mpublic class GamePanel extends Canvas {[m
             item.render(gc);[m
         }[m
 [m
[31m-        // (tuỳ chọn) Vẽ số mạng còn lại[m
[32m+[m[32m        // Vẽ số mạng còn lại[m
         gc.setFill(Color.WHITE);[m
         gc.fillText("Lives: " + playerLives, 10, 18);[m
     }[m
 [m
[31m-    /** Reset khi qua màn mới: giữ mạng, giữ paddle; bóng dính lại. */[m
[32m+[m[32m    /** Reset khi qua màn mới: giữ mạng; bóng dính lại. */[m
     private void resetForNextLevel() {[m
[31m-        // Đặt lại vị trí hợp lý và gắn bóng[m
[32m+[m[32m        // Đặt lại paddle/ball và gắn bóng[m
         paddle = new Paddle(350, 550, 140, 40);[m
         ball = new Ball(390, 500, 15);[m
         ball.attachToPaddle(paddle);[m
[36m@@ -174,17 +178,13 @@[m [mpublic class GamePanel extends Canvas {[m
         setOnKeyReleased(e -> paddle.removeKey(e.getCode()));[m
         this.setOnMouseMoved(paddle::handleMouseMove);[m
         this.setOnMouseDragged(paddle::handleMouseMove);[m
[31m-[m
[31m-        // Click lại để thả bóng[m
         this.setOnMouseClicked(e -> {[m
             if (ball.isAttachedToPaddle()) ball.releaseFromPaddle();[m
         });[m
 [m
[31m-        // Xoá item đang rơi (cho gọn)[m
         items.clear();[m
     }[m
 [m
[31m-    /** (Giữ lại nếu muốn reset cả 2 entity giữa chừng) */[m
     @SuppressWarnings("unused")[m
     private void resetBallAndPaddle() {[m
         ball = new Ball(390, 300, 15);[m
[36m@@ -200,18 +200,22 @@[m [mpublic class GamePanel extends Canvas {[m
         });[m
     }[m
 [m
[32m+[m[32m    // ====== OVERLAY HELPERS (có fallback nếu thiếu ảnh) ======[m
     private void showGameOverScreen() {[m
         StackPane overlay = new StackPane();[m
         overlay.setPrefSize(800, 600);[m
 [m
[31m-        Image image = new Image(Objects.requireNonNull([m
[31m-                getClass().getResource("/uet/oop/arkanoidgame/entities/menu/menu_images/game_over.png"),[m
[31m-                "Missing resource: game_over.png"[m
[31m-        ).toExternalForm());[m
[31m-        ImageView gameOverView = new ImageView(image);[m
[31m-        gameOverView.setFitWidth(800);[m
[31m-        gameOverView.setFitHeight(600);[m
[31m-        gameOverView.setPreserveRatio(false);[m
[32m+[m[32m        URL url = getClass().getResource("/uet/oop/arkanoidgame/entities/menu/menu_images/game_over.png");[m
[32m+[m[32m        ImageView bg = new ImageView();[m
[32m+[m[32m        bg.setFitWidth(800);[m
[32m+[m[32m        bg.setFitHeight(600);[m
[32m+[m[32m        bg.setPreserveRatio(false);[m
[32m+[m[32m        if (url != null) {[m
[32m+[m[32m            bg.setImage(new Image(url.toExternalForm()));[m
[32m+[m[32m        } else {[m
[32m+[m[32m            overlay.setStyle("-fx-background-color: #101018;");[m
[32m+[m[32m            System.err.println("⚠ Missing resource: game_over.png");[m
[32m+[m[32m        }[m
 [m
         Button backToMenu = new Button("Back to Menu");[m
         backToMenu.setStyle([m
[36m@@ -220,22 +224,21 @@[m [mpublic class GamePanel extends Canvas {[m
                         "-fx-text-fill: black; " +[m
                         "-fx-background-radius: 10;"[m
         );[m
[31m-[m
         backToMenu.setOnMouseEntered(e ->[m
                 backToMenu.setStyle("-fx-font-size: 22px; -fx-background-color: #ffaa00; -fx-text-fill: white; -fx-background-radius: 10;")[m
         );[m
         backToMenu.setOnMouseExited(e ->[m
                 backToMenu.setStyle("-fx-font-size: 22px; -fx-background-color: #ffcc00; -fx-text-fill: black; -fx-background-radius: 10;")[m
         );[m
[31m-[m
         backToMenu.setOnAction(e -> {[m
             mapManager.resetGame();[m
[32m+[m[32m            playerLives = 3;[m
             MainMenu menu = new MainMenu(stage);[m
             stage.setScene(new Scene(menu, 800, 600));[m
         });[m
 [m
         StackPane.setAlignment(backToMenu, Pos.CENTER);[m
[31m-        overlay.getChildren().addAll(gameOverView, backToMenu);[m
[32m+[m[32m        overlay.getChildren().addAll(bg, backToMenu);[m
 [m
         gamePane.getChildren().add(overlay);[m
     }[m
[36m@@ -244,14 +247,17 @@[m [mpublic class GamePanel extends Canvas {[m
         StackPane overlay = new StackPane();[m
         overlay.setPrefSize(800, 600);[m
 [m
[31m-        Image image = new Image(Objects.requireNonNull([m
[31m-                getClass().getResource("/uet/oop/arkanoidgame/entities/menu/menu_images/game_complete.png"),[m
[31m-                "Missing resource: game_complete.png"[m
[31m-        ).toExternalForm());[m
[31m-        ImageView gameCompleteView = new ImageView(image);[m
[31m-        gameCompleteView.setFitWidth(800);[m
[31m-        gameCompleteView.setFitHeight(600);[m
[31m-        gameCompleteView.setPreserveRatio(false);[m
[32m+[m[32m        URL url = getClass().getResource("/uet/oop/arkanoidgame/entities/menu/menu_images/game_complete.png");[m
[32m+[m[32m        ImageView bg = new ImageView();[m
[32m+[m[32m        bg.setFitWidth(800);[m
[32m+[m[32m        bg.setFitHeight(600);[m
[32m+[m[32m        bg.setPreserveRatio(false);[m
[32m+[m[32m        if (url != null) {[m
[32m+[m[32m            bg.setImage(new Image(url.toExternalF